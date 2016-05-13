package ch.raffael.doclets.pegdown.integrations.gradle;

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Upload;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before
import org.junit.Ignore;
import org.junit.Test

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail;

/**
 * @since 2015-12-15
 */
public class PegdownDocletPluginTest {
    private Project project;

    @Before
    public void setup() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    public void should_fail_if_no_java_in_classpath() {
        try{
            project.pluginManager.apply PegdownDocletPlugin
        }catch (IllegalStateException ie) {
            assertTrue(ie.message.contains("one of following plugins must be applied"))
        }
        fail("expected exception")
    }

    @Test
    public void should_run_with_java_plugin() {
        project.pluginManager.apply 'java'
        project.pluginManager.apply PegdownDocletPlugin
    }

    @Test
    public void should_run_with_android_application_plugin() {
        project.pluginManager.apply 'com.android.application'
        project.pluginManager.apply PegdownDocletPlugin
    }

    @Test
    public void should_run_with_android_library_plugin() {
        project.pluginManager.apply 'com.android.library'
        project.pluginManager.apply PegdownDocletPlugin
    }


}
