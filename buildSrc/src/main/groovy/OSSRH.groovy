import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.tasks.bundling.Jar


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class OSSRH implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.with {
            apply plugin:'maven'
            apply plugin:'signing'

            signing {
                sign configurations.archives
            }

            uploadArchives {
                dependsOn build
                repositories {
                    mavenDeployer {
                        def credentials = [
                                userName: project.properties.get('ossrhUsername'),
                                password: project.properties.get('ossrhPassword')
                        ]

                        beforeDeployment { MavenDeployment deployment -> signing.signPom(deployment) }

                        repository(url: "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                            authentication(credentials)
                        }

                        snapshotRepository(url: "https://oss.sonatype.org/content/repositories/snapshots/") {
                            authentication(credentials)
                        }

                        pom.project {
                            name 'Markdown Doclet'
                            description 'A Doclet that allows the use of Markdown and PlantUML in JavaDoc comments.'
                            url 'https://github.com/Abnaxos/markdown-doclet'
                            packaging 'jar'

                            developers {
                                developer {
                                    id 'Abnaxos'
                                    name 'Raffael Herzog'
                                    email 'herzog@raffael.ch'
                                    timezone '+1'
                                }
                            }

                            licenses {
                                license {
                                    name 'GPL 3.0'
                                    url 'http://www.gnu.org/licenses/gpl-3.0-standalone.html'
                                    distribution 'repo'
                                }
                            }

                            scm {
                                url 'https://github.com/Abnaxos/markdown-doclet'
                                connection 'scm:git:https://github.com/Abnaxos/markdown-doclet.git'
                            }
                        }
                    }
                }
            }

            task('javadocJar', type: Jar) {
                classifier = 'javadoc'
                from javadoc
            }
            task('sourceJar', type: Jar) {
                classifier = 'sources'
                from sourceSets.main.allSource
            }
            artifacts {
                archives tasks.javadocJar, tasks.sourceJar
            }

            // disable signing when we're not uploading the archives to OSSRH
            // this avoids errors for people who just want to do a local build and don't have the private key
            gradle.taskGraph.whenReady { graph ->
                if ( !graph.hasTask(tasks.uploadArchives) ) {
                    logger.info 'Disabling JAR signing'
                    tasks.signArchives.enabled = false
                }
            }
        }
    }

}
