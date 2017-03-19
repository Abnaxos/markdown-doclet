/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
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
 *
 */

package ch.raffael.doclets.pegdown.mdt.gist;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import ch.raffael.doclets.pegdown.mdtaglet.ArgumentValidator;
import ch.raffael.doclets.pegdown.mdtaglet.MarkdownTaglet;
import ch.raffael.doclets.pegdown.mdtaglet.MarkdownTagletBase;
import ch.raffael.doclets.pegdown.mdtaglet.PredefinedWhiteSpacePreserver;
import ch.raffael.doclets.pegdown.mdtaglet.WhiteSpacePreserver;
import ch.raffael.doclets.pegdown.mdtaglet.argval.ArgumentPredicate;
import ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentPredicates;

import static ch.raffael.doclets.pegdown.mdtaglet.argval.IndexFilter.at;
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentPredicates.isInteger;
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentPredicates.options;
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators.allOf;
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators.anyOf;
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators.argumentTypeValidator;
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators.atLeast;

/**
 * # GistMarkdownTaglet is the implementation for &#123;&#123;gist ...&#125;&#125;.
 *
 * It's a standard taglet which is already registered by {@link ch.raffael.doclets.pegdown.mdtaglet.StandardTaglets}.
 */
public final class GistMarkdownTaglet extends MarkdownTagletBase {
    private static final Pattern LINE_START = Pattern.compile("^", Pattern.MULTILINE);
    private static final String OPT_DISABLE_DESCRIPTION = "-desc";
    private static final String OPT_ENABLE_DESCRIPTION = "+desc";
    private static final String OPT_DISABLE_INDENT = "-indent";
    private static final String OPT_ENABLE_INDENT = "+indent";


    // private GithubAccessor githubAccessor;
    private GithubAccessor githubAccessor=new GithubAccessor();
    private Template template;
    private boolean useGistDescription = true;
    private boolean useIndentMarkdown = true;


    public GistMarkdownTaglet() {
    }

    private GistMarkdownTaglet(GithubAccessor githubAccessor, Template template, boolean useIndentMarkdown, boolean useGistDescription) {
        this.githubAccessor = githubAccessor;
        this.template = template;
        this.useIndentMarkdown = useIndentMarkdown;
        this.useGistDescription = useGistDescription;
    }

    @Override
    public String getName() {
        return "gist";
    }

    @Override
    public String getDescription() {
        return fetchMarkdownTagletDescriptionFile("gist-taglet-description.md");
    }

    @Option("gist-description")
    public void setUseGistDescription(String useGistDescription) {
        this.useGistDescription = Boolean.parseBoolean(useGistDescription);
    }

    @Option("gist-indent")
    public void setUseIndentMarkdown(String useIndentMarkdown) {
        this.useIndentMarkdown = Boolean.parseBoolean(useIndentMarkdown);
    }

    @Option("gist-github-properties")
    public void setGitHubPropertyFileName(String gitHubPropertyFileName) {
        githubAccessor.setGitHubPropertyFileName(gitHubPropertyFileName);
    }

    @Option("gist-github-use-cache")
    public void setUseGithubCache(String useCache) {
        githubAccessor.setUseCache(Boolean.parseBoolean(useCache));
    }

    @Option("gist-github-cache-dir")
    public void setGithubCacheDirectory(String githubCacheDirectory) {
        githubAccessor.setCacheDirectoryName(githubCacheDirectory);
    }

    @Option("gist-github-cache-size")
    public void setGithubCacheSize(String githubCacheSize) {
        githubAccessor.setCacheSize(Integer.parseInt(githubCacheSize));
    }

    @Override
    public void afterOptionsSet() throws Exception {
        this.githubAccessor.init();
        final VelocityEngine engine = new VelocityEngine();
        engine.init(loadVelocityProperties("/ch/raffael/doclets/pegdown/mdt/gist/mdt-gist-velocity.properties"));
        this.template = engine.getTemplate("/ch/raffael/doclets/pegdown/mdt/gist/mdt-gist-template.vm");
    }

    private Properties loadVelocityProperties(String fileName) throws IOException {
        final Properties properties = new Properties();
        properties.load(GistMarkdownTaglet.class.getResourceAsStream(fileName));
        return properties;
    }

    @Override
    public MarkdownTaglet createNewInstance() {
        return new GistMarkdownTaglet(githubAccessor, template, useIndentMarkdown, useGistDescription);
    }

    @Override
    public ArgumentValidator getArgumentValidator() {
        final ArgumentPredicate renderingOptions = options(OPT_DISABLE_INDENT, OPT_DISABLE_DESCRIPTION, OPT_ENABLE_INDENT, OPT_ENABLE_DESCRIPTION);
        final ArgumentPredicate gistId = isInteger(PredefinedArgumentPredicates.Radix.HEXADECIMAL);
        return anyOf(
                allOf(atLeast(1), argumentTypeValidator("gist id", at(0), gistId)),
                allOf(atLeast(2), argumentTypeValidator("option", at(0), renderingOptions),
                        argumentTypeValidator("gist id", at(1), gistId)),
                allOf(atLeast(3), argumentTypeValidator("option", at(0, 1), renderingOptions),
                        argumentTypeValidator("gist id", at(2), gistId))
        );
    }

    @Override
    public WhiteSpacePreserver getWhiteSpacePreserver() {
        return PredefinedWhiteSpacePreserver.STRIP_ALL;
    }

    @Override
    public String render(List<String> argumentList) throws Exception {
        final List<String> allArguments = removeOptions(argumentList);
        final String gistId = allArguments.remove(0);
        return emptyLines(1) + blockquote(doRenderGist(gistId, allArguments)) + emptyLines(1);
    }

    private List<String> removeOptions(List<String> argumentList) {
        final List<String> gistArguments = new LinkedList<>();
        for (String arg : argumentList) {
            switch (arg) {
                case OPT_DISABLE_DESCRIPTION:
                    this.useGistDescription = false;
                    break;
                case OPT_ENABLE_DESCRIPTION:
                    this.useGistDescription = true;
                    break;
                case OPT_DISABLE_INDENT:
                    this.useIndentMarkdown = false;
                    break;
                case OPT_ENABLE_INDENT:
                    this.useIndentMarkdown = true;
                    break;
                default:
                    gistArguments.add(arg);
                    break;
            }
        }
        return gistArguments;
    }

    private String blockquote(String markdown) {
        if (useIndentMarkdown) {
            return LINE_START.matcher(markdown).replaceAll("> ");
        }

        return markdown;
    }

    private String doRenderGist(String gistId, List<String> fileSelector) throws IOException {
        final StringBuilder markdown = new StringBuilder();
        final GHGist gist = githubAccessor.getGist(gistId);
        final List<String> renderContent = doRenderGistFiles(gist, fileSelector);
        markdown.append(Joiner.on(htmlNewline()).join(renderContent));
        return markdown.toString();
    }

    private List<String> doRenderGistFiles(GHGist gist, List<String> fileSelector) {
        final List<String> renderContent = new LinkedList<>();
        final Map<String, GHGistFile> files = gist.getFiles();
        boolean first = true;
        for (GHGistFile gistFile : files.values()) {
            if (shouldFileBeRendered(fileSelector, gistFile)) {
                renderContent.add(applyGistTemplate(gist, gistFile, first));
                first = false;
            }
        }
        return renderContent;
    }

    private boolean shouldFileBeRendered(List<String> fileSelector, GHGistFile gistFile) {
        return fileSelector.isEmpty() || fileSelector.contains(gistFile.getFileName());
    }

    private String applyGistTemplate(GHGist gist, GHGistFile gistFile, boolean firstGistFile) {
        final VelocityContext context = new VelocityContext();
        context.put("gist", gist);
        context.put("gistFile", gistFile);
        context.put("renderDescription", firstGistFile && useGistDescription);
        final StringWriter templateWriter = new StringWriter();
        template.merge(context, templateWriter);
        return templateWriter.toString();
    }

}
