/*
 * Copyright 2013 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.doclets.pegdown;

import java.util.Arrays;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;


/**
 * A wrapper for the {@link RootDoc}. It forwards all calls to the original root document
 * exception {@link #options()}, which may contain a modified array of doclet options.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class RootDocWrapper implements RootDoc {

    private final RootDoc delegate;
    private String[][] options;

    public RootDocWrapper(RootDoc delegate, String[][] options) {
        this.delegate = delegate;
        this.options = options;
    }

    /**
     * Append an option to the doclet options.
     *
     * @param option    The option to append.
     */
    public void appendOption(String... option) {
        options = Arrays.copyOf(options, options.length + 1);
        options[options.length - 1] = option;
    }

    /**
     * Overriden to return a modified array of doclet options.
     *
     * @return The modified doclet options.
     */
    @Override
    public String[][] options() {
        return options;
    }

    @Override
    public PackageDoc[] specifiedPackages() {
        return delegate.specifiedPackages();
    }

    @Override
    public ClassDoc[] specifiedClasses() {
        return delegate.specifiedClasses();
    }

    @Override
    public ClassDoc[] classes() {
        return delegate.classes();
    }

    @Override
    public PackageDoc packageNamed(String name) {
        return delegate.packageNamed(name);
    }

    @Override
    public ClassDoc classNamed(String qualifiedName) {
        return delegate.classNamed(qualifiedName);
    }

    @Override
    public String commentText() {
        return delegate.commentText();
    }

    @Override
    public Tag[] tags() {
        return delegate.tags();
    }

    @Override
    public Tag[] tags(String tagname) {
        return delegate.tags(tagname);
    }

    @Override
    public SeeTag[] seeTags() {
        return delegate.seeTags();
    }

    @Override
    public Tag[] inlineTags() {
        return delegate.inlineTags();
    }

    @Override
    public Tag[] firstSentenceTags() {
        return delegate.firstSentenceTags();
    }

    @Override
    public String getRawCommentText() {
        return delegate.getRawCommentText();
    }

    @Override
    public void setRawCommentText(String rawDocumentation) {
        delegate.setRawCommentText(rawDocumentation);
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public int compareTo(Object obj) {
        return delegate.compareTo(obj);
    }

    @Override
    public boolean isField() {
        return delegate.isField();
    }

    @Override
    public boolean isEnumConstant() {
        return delegate.isEnumConstant();
    }

    @Override
    public boolean isConstructor() {
        return delegate.isConstructor();
    }

    @Override
    public boolean isMethod() {
        return delegate.isMethod();
    }

    @Override
    public boolean isAnnotationTypeElement() {
        return delegate.isAnnotationTypeElement();
    }

    @Override
    public boolean isInterface() {
        return delegate.isInterface();
    }

    @Override
    public boolean isException() {
        return delegate.isException();
    }

    @Override
    public boolean isError() {
        return delegate.isError();
    }

    @Override
    public boolean isEnum() {
        return delegate.isEnum();
    }

    @Override
    public boolean isAnnotationType() {
        return delegate.isAnnotationType();
    }

    @Override
    public boolean isOrdinaryClass() {
        return delegate.isOrdinaryClass();
    }

    @Override
    public boolean isClass() {
        return delegate.isClass();
    }

    @Override
    public boolean isIncluded() {
        return delegate.isIncluded();
    }

    @Override
    public SourcePosition position() {
        return delegate.position();
    }

    @Override
    public void printError(String msg) {
        delegate.printError(msg);
    }

    @Override
    public void printError(SourcePosition pos, String msg) {
        delegate.printError(pos, msg);
    }

    @Override
    public void printWarning(String msg) {
        delegate.printWarning(msg);
    }

    @Override
    public void printWarning(SourcePosition pos, String msg) {
        delegate.printWarning(pos, msg);
    }

    @Override
    public void printNotice(String msg) {
        delegate.printNotice(msg);
    }

    @Override
    public void printNotice(SourcePosition pos, String msg) {
        delegate.printNotice(pos, msg);
    }
}
