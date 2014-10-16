package ch.raffael.doclets.pegdown.integrations.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc

class PegdownDocletPlugin implements Plugin<Project> {
    final def CONFGURATION_NAME = "pegdownDoclet"

    void apply(Project project) {
        // make sure the java plugin is applied
        project.plugins.apply('java')

        // create a new configuration for the doclet dependency
        def config = project.configurations.create(CONFGURATION_NAME)

        // add the doclet dependency
        project.dependencies.add(CONFGURATION_NAME, 'ch.raffael.pegdown-doclet:pegdown-doclet:1.1.1')

        // after the user buildscript is evaluated ...
        project.gradle.projectsEvaluated {

            // ... adjust the javadoc tasks
            project.tasks.withType Javadoc.class, {
                it.options {
                    docletpath = config.files.asType(List)
                    doclet = "ch.raffael.doclets.pegdown.PegdownDoclet"
                    addStringOption("parse-timeout", "10")
                }
            }
        }
    }
}
