package ch.raffael.doclets.pegdown.integrations.idea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@State(name = Plugin.PLUGIN_NAME + ".ProjectConfiguration",
       storages = {
               @Storage(file = StoragePathMacros.PROJECT_FILE),
               @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/pegdown-doclet.xml")
       })
public class ProjectConfiguration implements ProjectComponent, PersistentStateComponent<PegdownOptions> {

    private PegdownOptions configuration = new PegdownOptions();

    public ProjectConfiguration(Project project) {
        configuration.enabled = false;
        configuration.renderingOptions = new PegdownOptions.RenderingOptions();
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return Plugin.PROJECT_CONFIG_NAME;
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    @Nullable
    @Override
    public synchronized PegdownOptions getState() {
        return new PegdownOptions(configuration);
    }

    @Override
    public synchronized void loadState(PegdownOptions state) {
        configuration = new PegdownOptions(state);
        if ( configuration.enabled == null ) {
            configuration.enabled = false;
        }
        if ( configuration.renderingOptions == null ) {
            configuration.renderingOptions = new PegdownOptions.RenderingOptions();
        }
    }

    public synchronized PegdownOptions getConfiguration() {
        return new PegdownOptions(configuration);
    }

    public synchronized void setConfiguration(PegdownOptions configuration) {
        this.configuration = new PegdownOptions(configuration);
    }

    public synchronized boolean isPegdownEnabled() {
        return configuration.enabled;
    }

    public synchronized PegdownOptions.RenderingOptions getRenderingOptions() {
        return new PegdownOptions.RenderingOptions(configuration.renderingOptions);
    }

}
