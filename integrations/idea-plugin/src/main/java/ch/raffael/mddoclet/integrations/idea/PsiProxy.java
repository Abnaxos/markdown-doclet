/*
 * Copyright 2013 Raffael Herzog
 *
 * This file is part of markdown-doclet.
 *
 * markdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with markdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.raffael.mddoclet.integrations.idea;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Optional;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDocCommentOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiParameterListImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Wraps PSI elements and intercepts certain methods. This is used to render method and
 * parameter documentations, as there is no easy way to intercept this. It's a hack, but
 * oh, well ...
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class PsiProxy<T> {

    private final static Logger LOG = Logger.getInstance(PsiProxy.class);

    protected final T delegate;

    protected PsiProxy(T delegate) {
        this.delegate = delegate;
    }

    private static String toString(Object delegate) {
        return "Proxy<" + delegate.toString() + ">";
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public static PsiClassType forType(PsiClassType delegate) {
        return new PsiClassTypeProxy(delegate);
    }

    public static PsiClass forClass(PsiClass delegate) {
        class GetListTypesInterceptor extends Interceptor<PsiClass> {
            private PsiClassType[] types;
            GetListTypesInterceptor(String name, Class[] paramTypes) {
                super(name, paramTypes);
            }
            @Override
            protected Object intercept(PsiClass proxy, PsiClass target, Method method, Object[] parameters) throws Throwable {
                if ( types == null ) {
                    types = (PsiClassType[])method.invoke(target, parameters);
                    if ( types != null ) {
                        types = Arrays.copyOf(types, types.length);
                        for ( int i = 0; i < types.length; i++ ) {
                            types[i] = forType(types[i]);
                        }
                    }
                }
                return Arrays.copyOf(types, types.length);
            }
        }
        return proxy(
                (PsiClass)delegate.getNavigationElement(),
                new GetNavigationElementInterceptor(),
                new GetListTypesInterceptor("getImplementsListTypes", params()),
                new GetListTypesInterceptor("getExtendsListTypes", params()),
                new Interceptor<PsiClass>("findMethodBySignature", params(PsiMethod.class, boolean.class)) {
                    @Override
                    protected Object intercept(PsiClass proxy, PsiClass target, Method method, Object[] parameters) throws Throwable {
                        PsiMethod found = (PsiMethod)method.invoke(target, parameters);
                        if ( found != null ) {
                            found = forMethod(found);
                        }
                        return found;
                    }
                }
        );
    }

    public static PsiMethod forMethod(PsiMethod delegate) {
        return forMethod(delegate, Interceptor.<PsiMethod>nop());
    }

    private static PsiMethod forMethod(PsiMethod delegate, Interceptor<PsiMethod> additionalInterceptor) {
        class FindDeepestSuperMethodsInterceptor extends Interceptor<PsiMethod> {
            private PsiMethod[] deepestSuperMethods;
            private FindDeepestSuperMethodsInterceptor() {
                super("findDeepestSuperMethods", params());
            }
            @Override
            protected Object intercept(PsiMethod proxy, PsiMethod target, Method method, Object[] parameters) throws Throwable {
                if ( deepestSuperMethods == null ) {
                    deepestSuperMethods = (PsiMethod[])method.invoke(target);
                    deepestSuperMethods = Arrays.copyOf(deepestSuperMethods, deepestSuperMethods.length);
                    for ( int i = 0; i < deepestSuperMethods.length; i++ ) {
                        deepestSuperMethods[i] = forMethod(deepestSuperMethods[i]);
                    }
                }
                return Arrays.copyOf(deepestSuperMethods, deepestSuperMethods.length);
            }
        }
        delegate = (PsiMethod)delegate.getNavigationElement();
        return proxy(delegate,
                additionalInterceptor,
                new GetDocCommentInterceptor(),
                GetNavigationElementInterceptor.INSTANCE,
                new FindDeepestSuperMethodsInterceptor(),
                new Interceptor<PsiMethod>("getContainingClass", params()) {
                    private PsiClass containing;
                    @Override
                    protected Object intercept(PsiMethod proxy, PsiMethod target, Method method, Object[] parameters) throws Throwable {
                        if ( containing == null ) {
                            containing = target.getContainingClass();
                            if ( containing != null ) {
                                containing = forClass(containing);
                            }
                        }
                        return containing;
                    }
                });
    }

    public static PsiParameter forParameter(PsiParameter delegate) {
        // OK, there are ugly things in this code. Very ugly things. But this one beats it all. It's just monstrous.
        // So, make sure you sit tight and have your seat belts fastened.
        class GetParameterParentInterceptor extends GetParentInterceptor {
            private String getParameterIndexClassName = PsiParameterListImpl.class.getName();
            private String getParameterListMethodName = getParameterListMethodName();
            private final GetParameterParentInterceptor parent;
            private PsiParameterList parameterList;
            private GetParameterParentInterceptor(GetParameterParentInterceptor parent) {
                this.parent = parent;
            }
            private String getParameterListMethodName() {
                try {
                    return PsiParameterListImpl.class
                            .getDeclaredMethod("getParameterIndex", PsiParameter.class)
                            .getName();
                }
                catch ( NoSuchMethodException e ) {
                    LOG.assertTrue(false, "Exception: " + e + " This indicates an incompatible change in IDEA's code.");
                    throw new AssertionError("Unreachable code reached");
                }
            }
            @Override
            boolean appliesTo(Method method) {
                // Sometimes, psiParameterList.getParameterIndex() is called with a proxy.
                // The implementation of getParameterList() has an assertion that the parent of the proxy is identical
                // (==) to this. However, the parent of the proxy will be another proxy. The method actually works fine
                // that way, but the assertion will fail (see Issue #52).
                // We cannot control where the PsiParameterList comes from, so we can't just intercept
                // getParameterIndex(). It might be the original PsiParameterList and there's nothing, we can do about
                // that.
                // Therefore, this method inspects the stack trace and doesn't intercept getParent(), if it's being
                // called from getParameterIndex().
                //
                // There's no performance issue, this code is called only after Ctrl-Q. The user won't notice.
                // But it's ugly as hell!
                if ( super.appliesTo(method) ) {
                    for ( StackTraceElement elem : Thread.currentThread().getStackTrace() ) {
                        if ( elem.getClassName().equals(getParameterIndexClassName) && elem.getMethodName().equals(getParameterListMethodName) ) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            PsiElement createGetParentInterceptor(final PsiElement element) {
                if ( element instanceof PsiMethod ) {
                    // The ugliness gets worse. The expression used to check whether our parameter is a method parameter
                    // (in contrast to e.g. a foreach parameter), the following expression is used:
                    //    method != null && method.getParameterList() == parameter.getParent();
                    // So, we're intercepting getParameterList() to return our proxy.
                    return forMethod((PsiMethod)element, new Interceptor<PsiMethod>("getParameterList", params()) {
                        @Override
                        protected Object intercept(PsiMethod proxy, PsiMethod target, Method method, Object[] parameters) throws Throwable {
                            GetParameterParentInterceptor root = GetParameterParentInterceptor.this;
                            while ( root.parent != null ) {
                                root = root.parent;
                            }
                            return root.parameterList;
                        }
                    });
                }
                else {
                    return proxy(element, new GetParameterParentInterceptor(GetParameterParentInterceptor.this));
                }
            }
        }
        checkNoDoubleProxy(delegate);
        if ( !isMethodParameter(delegate) ) {
            // TODO: 29.05.16 nothing to do here?
            return delegate;
        }
        GetParameterParentInterceptor interceptor = new GetParameterParentInterceptor(null);
        PsiParameter proxy = proxy(delegate, interceptor);
        // save the original parameter list proxy
        interceptor.parameterList = (PsiParameterList)proxy.getParent();
        return proxy;
    }

    private static boolean isMethodParameter(PsiParameter parameter) {
        PsiMethod method = PsiTreeUtil.getParentOfType(parameter, PsiMethod.class);
        // see JavaDocInfoGenerator::generateMethodParameterJavaDoc
        //noinspection ObjectEquality
        return method != null && method.getParameterList() == parameter.getParent();
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <T> T proxy(T delegate, Interceptor<? super T>... interceptors) {
        checkNoDoubleProxy(delegate);
        Set<Class> interfaces = new HashSet<>();
        Class clazz = delegate.getClass();
        while ( !clazz.equals(Object.class) ) {
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        }
        return (T)Proxy.newProxyInstance(delegate.getClass().getClassLoader(), interfaces.toArray(new Class[interfaces.size()]), new Invoker<T>(delegate, interceptors));
    }

    private static void checkNoDoubleProxy(Object delegate) {
        if ( Proxy.isProxyClass(delegate.getClass()) ) {
            throw new IllegalArgumentException("Attempt to proxy a proxy");
        }
    }

    private static Class[] params(Class... types) {
        if ( types == null ) {
            types = new Class[0];
        }
        return types;
    }

    private static abstract class Interceptor<T> {
        private static final Interceptor<Object> NOP = new Interceptor<Object>(null, null) {
            @Override
            boolean appliesTo(Method method) {
                return false;
            }
            @Override
            protected Object intercept(Object proxy, Object target, Method method, Object[] parameters) throws Throwable {
                throw new IllegalStateException();
            }
            @Override
            public String toString() {
                return Interceptor.class.getName() + "::NOP";
            }
        };
        @SuppressWarnings("unchecked")
        public static <T> Interceptor<T> nop() {
            return (Interceptor<T>)NOP;
        }

        private final String name;
        private final Class[] paramTypes;
        protected Interceptor(String name, Class[] paramTypes) {
            this.name = name;
            this.paramTypes = paramTypes;
        }
        boolean appliesTo(Method method) {
            return method.getName().equals(name)
                    && (paramTypes == null || Arrays.equals(method.getParameterTypes(), paramTypes));
        }
        protected abstract Object intercept(T proxy, T target, Method method, Object[] parameters) throws Throwable;
    }

    private static class Invoker<T> implements InvocationHandler {
        private final T delegate;
        private final Interceptor<? super T>[] interceptors;
        @SafeVarargs
        private Invoker(T delegate, Interceptor<? super T>... interceptors) {
            this.delegate = delegate;
            this.interceptors = Arrays.copyOf(interceptors, interceptors.length + 1);
            this.interceptors[this.interceptors.length - 1] = ToStringInterceptor.INSTANCE;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for ( Interceptor interceptor : interceptors ) {
                if ( interceptor.appliesTo(method) ) {
                    return interceptor.intercept(proxy, delegate, method, args);
                }
            }
            return method.invoke(delegate, args);
        }
    }

    private static class ToStringInterceptor extends Interceptor<Object> {
        private static final ToStringInterceptor INSTANCE = new ToStringInterceptor();
        private ToStringInterceptor() {
            super("toString", params());
        }
        @Override
        protected Object intercept(Object proxy, Object target, Method method, Object[] parameters) throws Throwable {
            return "Proxy<" + method.invoke(target) + ">";
        }
    }

    private static class GetDocCommentInterceptor extends Interceptor<PsiDocCommentOwner> {

        private DocCommentProcessor processor;
        private PsiDocComment docComment;

        private GetDocCommentInterceptor() {
            super("getDocComment", params());

        }

        @Override
        protected Object intercept(PsiDocCommentOwner proxy, PsiDocCommentOwner target, Method method, Object[] parameters) throws Throwable {
            if ( processor == null ) {
                processor = new DocCommentProcessor(target.getContainingFile());
                docComment = processor.processDocComment(target.getDocComment());
            }
            return docComment;
        }
    }

    private static class GetNavigationElementInterceptor extends Interceptor<PsiElement> {
        private static final GetNavigationElementInterceptor INSTANCE = new GetNavigationElementInterceptor();
        private GetNavigationElementInterceptor() {
            super("getNavigationElement", params());
        }
        @Override
        protected Object intercept(PsiElement proxy, PsiElement target, Method method, Object[] parameters) throws Throwable {
            return proxy;
        }
    }

    private static class GetParentInterceptor extends Interceptor<PsiElement> {

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<PsiElement> parent = null;

        public GetParentInterceptor() {
            super("getParent", PsiProxy.params());
        }

        @Override
        protected Object intercept(PsiElement proxy, PsiElement target, Method method, Object[] parameters) throws Throwable {
            if ( parent == null ) {
                PsiElement element = (PsiElement)method.invoke(target);
                if ( element != null ) {
                    parent = Optional.fromNullable(createGetParentInterceptor(element));
                }
                else {
                    parent = Optional.absent();
                }
            }
            return parent.orNull();
        }

        PsiElement createGetParentInterceptor(PsiElement element) {
            if ( element instanceof PsiMethod ) {
                return forMethod((PsiMethod)element);
            }
            else {
                return proxy(element, new GetParentInterceptor());
            }
        }
    }

    private static class PsiClassTypeProxy extends PsiClassType {

        private final PsiClassType delegate;
        private PsiClass resolved = null;

        private PsiClassTypeProxy(PsiClassType delegate) {
            super(delegate.getLanguageLevel(), delegate.getAnnotations());
            this.delegate = delegate;
        }

        public String toString() {
            return "Proxy<" + delegate.toString() + ">";
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        @Override
        @Nullable
        public PsiClass resolve() {
            if ( resolved == null ) {
                resolved = delegate.resolve();
                if ( resolved != null ) {
                    resolved = forClass(resolved);
                }
            }
            return resolved;
        }

        @Override
        public String getClassName() {
            return delegate.getClassName();
        }

        @Override
        @NotNull
        public PsiType[] getParameters() {
            return delegate.getParameters();
        }

        @Override
        @NotNull
        public ClassResolveResult resolveGenerics() {
            return delegate.resolveGenerics();
        }

        @Override
        @NotNull
        public PsiClassType rawType() {
            return delegate.rawType();
        }

        @Override
        @NotNull
        public GlobalSearchScope getResolveScope() {
            return delegate.getResolveScope();
        }

        @Override
        @NotNull
        public LanguageLevel getLanguageLevel() {
            return delegate.getLanguageLevel();
        }

        @Override
        @NotNull
        public PsiClassType setLanguageLevel(@NotNull LanguageLevel languageLevel) {
            return delegate.setLanguageLevel(languageLevel);
        }

        @Override
        public String getPresentableText() {
            return delegate.getPresentableText();
        }

        @Override
        @NonNls
        public String getCanonicalText() {
            return delegate.getCanonicalText();
        }

        @Override
        public String getInternalCanonicalText() {
            return delegate.getInternalCanonicalText();
        }

        @Override
        public boolean isValid() {
            return delegate.isValid();
        }

        @Override
        public boolean equalsToText(@NonNls String text) {
            return delegate.equalsToText(text);
        }
    }

}
