package ch.raffael.mddoclet.integrations.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc

class MarkdownDocletPlugin implements Plugin<Project> {
    final def CONFIGURATION_NAME = "markdownDoclet"

    void apply(Project project) {
        // create a new configuration for the doclet dependency
        def config = project.configurations.create(CONFIGURATION_NAME)

        // add the doclet dependency
        def markdownVersion = new InputStreamReader(MarkdownDocletPlugin.class.getResourceAsStream('version.txt')).withReader { reader ->
            reader.text.trim()
        }
        project.dependencies.add(CONFIGURATION_NAME, "ch.raffael.markdown-doclet:markdown-doclet:$markdownVersion")

        // after the user buildscript is evaluated ...
        project.gradle.projectsEvaluated {

            // ... adjust the javadoc tasks
            project.tasks.withType Javadoc.class, {
                it.options {
                    docletpath = config.files.asType(List)
                    doclet = "ch.raffael.mddoclet.MarkdownDoclet"
                    addStringOption("parse-timeout", "10")
                }
            }
        }
    }
}
