import groovy.xml.XmlUtil
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.compile.AbstractCompile


/**
 * @author Raffael Herzog
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class IdeaPluginSync implements Plugin<Project> {

    static final String IDEA_HOME_PROPERTY = 'idea.home'
    static final String UPDATE_IDEA_PLUGIN_IML_TASK = 'updateIdeaPluginIML'
    static final String IDEA_PLUGIN_TASK = 'ideaPlugin'

    @Override
    void apply(Project project) {
        def ideaHome = getIDEAHome(project)
        if ( ideaHome ) {
            applyWithIDEA(project, ideaHome)
        }
        else {
            applyWithoutIDEA(project)
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private File getIDEAHome(Project project) {
        def ideaHome = null
        project.apply plugin:'java'
        project.apply plugin:'idea'
        if ( project.properties.containsKey(IDEA_HOME_PROPERTY) ) {
            def ideaHomeString = project.properties[IDEA_HOME_PROPERTY]
            if ( ideaHomeString ) {
                def f = project.file(ideaHomeString)
                if ( f.isDirectory() && project.file("$f/lib/idea.jar").isFile() ) {
                    ideaHome = f
                }
            }
        }
        return ideaHome
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void applyWithIDEA(Project project, File ideaHome) {
        def imlTask = project.task(UPDATE_IDEA_PLUGIN_IML_TASK,
                                   type: UpdateIdeaPluginIML,
                                   group: 'ide',
                                   description: "Update the IML file to type $UpdateIdeaPluginIML.MODULE_TYPE_IDEA_PLUGIN")
        project.afterEvaluate {
            if ( !imlTask.moduleFile ) {
                def imlFile
                if (project == project.rootProject) {
                    imlFile = project.file("${project.idea.module.name}.iml")
                }
                else {
                    imlFile = project.rootProject.file(".idea/modules/${project.path.substring(1).replace(':', '/')}/${project.idea.module.name}.iml")
                }
                imlTask.moduleFile = imlFile
            }
            project.configurations {
                ideaClasspath
            }
            project.dependencies {
                ideaClasspath project.files(project.fileTree(dir:"$ideaHome/lib", include:'*.jar'))
            }
            project.tasks.withType(AbstractCompile).each { task ->
                task.classpath = project.configurations.ideaClasspath + task.classpath
            }
        }
        def pluginTask = project.task(IDEA_PLUGIN_TASK,
                                      type:Zip,
                                      group:'build',
                                      description:'Package the IDEA plugin')
        pluginTask.configure {
            dependsOn project.tasks.jar

            def tokens = [
                    "version": project.version
            ]

            inputs.property 'tokens', tokens

            ext.lib = project.configurations.compile

            into(project.archivesBaseName) {
                into('META-INF') {
                    from({ project.file(imlTask.pluginDescriptor) }) {
                        filter ReplaceTokens, tokens:tokens
                    }
                }
                into('lib') {
                    from { project.tasks.jar }
                    from { lib }
                }
            }
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void applyWithoutIDEA(Project project) {
        project.logger.warn "Property idea.home not set to a valid IDEA distribution; disabling project"
        project.afterEvaluate {
            project.tasks.each { task ->
                task.enabled = false
            }
        }
    }

    static class UpdateIdeaPluginIML extends DefaultTask {

        static final String MODULE_TYPE_IDEA_PLUGIN = 'PLUGIN_MODULE'
        static final String PLUGIN_DESCRIPTOR_PATH_HEAD = 'file://$MODULE_DIR$/'

        File moduleFile
        String pluginDescriptor = 'src/main/idea-plugin/META-INF/plugin.xml'

        UpdateIdeaPluginIML() {
        }

        private loadModuleXml() {
            new XmlParser().parse(moduleFile)
        }

        @SuppressWarnings("GroovyUnusedDeclaration")
        @TaskAction
        void updateIml() {
            def didChange = false
            def moduleXml = loadModuleXml()
            if ( moduleXml.@type != MODULE_TYPE_IDEA_PLUGIN ) {
                logger.quiet "Setting module type to $MODULE_TYPE_IDEA_PLUGIN"
                didChange = true
                moduleXml.@type = MODULE_TYPE_IDEA_PLUGIN
            }
            Object devKitNode = moduleXml.component.find {
                it.name() == "component" && it.@name == 'DevKit.ModuleBuildProperties'
            }
            def moduleDirPath = moduleFile.parentFile.toPath()
            def pluginDescriptorPath = project.file(pluginDescriptor).toPath()
            def pluginDescriptorUrl = PLUGIN_DESCRIPTOR_PATH_HEAD + moduleDirPath.relativize(pluginDescriptorPath)
            if ( devKitNode == null ) {
                logger.quiet 'Creating new configuration for DevKit component'
                didChange = true
                devKitNode = new Node(moduleXml, 'component')
                devKitNode.@name = 'DevKit.ModuleBuildProperties'
                devKitNode.@url = pluginDescriptorUrl
            }
            else if ( devKitNode.@url != pluginDescriptorUrl ) {
                logger.quiet 'Updating URL of plugin descriptor for DevKit component'
                didChange = true
                devKitNode.@url = pluginDescriptorUrl
            }
            if ( didChange ) {
                moduleFile.setText(XmlUtil.serialize(moduleXml))
                project.gradle.buildFinished {
                    logger.warn "NOTE: The IDEA module file for $project.path has been updated"
                    logger.warn 'NOTE: You need to reload (close and reopen) the project in IDEA to apply the changes'
                }
            }
            else {
                logger.quiet 'IDEA module was up-to-date'
            }
        }
    }
}
