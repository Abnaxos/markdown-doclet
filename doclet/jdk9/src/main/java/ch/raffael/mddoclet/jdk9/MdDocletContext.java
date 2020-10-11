package ch.raffael.mddoclet.jdk9;

import jdk.javadoc.doclet.DocletEnvironment;

import ch.raffael.mddoclet.core.MarkdownProcessor;
import ch.raffael.mddoclet.jdk9.doctree.DocletEnvironmentProxy;


/**
 * TODO: 06.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public interface MdDocletContext {

    DocletEnvironment docletEnv();
    DocletEnvironment docletEnvProxy();

    MarkdownProcessor mdProcessor();

    class DefaultMdDcoletContext implements MdDocletContext {

        private final DocletEnvironment docletEnv;
        private final DocletEnvironmentProxy docletEnvProxy;
        private final MarkdownProcessor mdProcessor;

        public DefaultMdDcoletContext(DocletEnvironment docletEnv, MarkdownProcessor mdProcessor) {
            this.docletEnv = docletEnv;
            docletEnvProxy = new DocletEnvironmentProxy(docletEnv);
            this.mdProcessor = mdProcessor;
        }

        @Override
        public DocletEnvironment docletEnv() {
            return docletEnv;
        }

        @Override
        public DocletEnvironment docletEnvProxy() {
            return docletEnvProxy;
        }

        @Override
        public MarkdownProcessor mdProcessor() {
            return mdProcessor;
        }
    }

}
