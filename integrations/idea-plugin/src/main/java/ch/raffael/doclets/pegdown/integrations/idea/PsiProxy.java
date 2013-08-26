/*
 * Copyright 2013 Raffael Herzog
 *
 * This file is part of pegdown-doclet.
 *
 * pegdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pegdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pegdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.raffael.doclets.pegdown.integrations.idea;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDocCommentOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
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
        return proxy(delegate, new GetParentInterceptor());
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <T> T proxy(T delegate, Interceptor<? super T>... interceptors) {
        Set<Class> interfaces = new HashSet<Class>();
        Class clazz = delegate.getClass();
        while ( !clazz.equals(Object.class) ) {
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        }
        return (T)Proxy.newProxyInstance(delegate.getClass().getClassLoader(), interfaces.toArray(new Class[interfaces.size()]), new Invoker<T>(delegate, interceptors));
    }

    private static Class[] params(Class... types) {
        if ( types == null ) {
            types = new Class[0];
        }
        return types;
    }

    private static abstract class Interceptor<T> {
        private final String name;
        private final Class[] paramTypes;
        protected Interceptor(String name, Class[] paramTypes) {
            this.name = name;
            this.paramTypes = paramTypes;
        }
        private boolean appliesTo(Method method) {
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

        private PsiElement parent = null;

        public GetParentInterceptor() {
            super("getParent", PsiProxy.params());
        }

        @Override
        protected Object intercept(PsiElement proxy, PsiElement target, Method method, Object[] parameters) throws Throwable {
            if ( parent == null ) {
                parent = (PsiElement)method.invoke(target);
                if ( parent instanceof PsiMethod ) {
                    parent = forMethod((PsiMethod)parent);
                }
                else if ( parent != null ) {
                    parent = proxy(parent, new GetParentInterceptor());
                }
            }
            return parent;
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
