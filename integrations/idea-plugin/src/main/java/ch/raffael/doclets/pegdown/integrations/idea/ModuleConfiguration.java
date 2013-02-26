package ch.raffael.doclets.pegdown.integrations.idea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@State(name = Plugin.PLUGIN_NAME + ".ModuleConfiguration",
       storages = {
               @Storage(file = "$MODULE_FILE$")
       })
public class ModuleConfiguration implements ModuleComponent, PersistentStateComponent<PegdownOptions> {

    private final Module module;

    private PegdownOptions configuration = new PegdownOptions();
    private final ProjectConfiguration projectConfiguration;

    public ModuleConfiguration(Project project, Module module) {
        this.module = module;
        projectConfiguration = (ProjectConfiguration)project.getComponent(Plugin.PROJECT_CONFIG_NAME);
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void moduleAdded() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return Plugin.MODULE_CONFIG_NAME;
    }

    @Nullable
    //@Override
    public synchronized PegdownOptions getState() {
        return new PegdownOptions(configuration);
    }

    //@Override
    public synchronized void loadState(PegdownOptions state) {
        configuration = new PegdownOptions(state);
    }

    public synchronized PegdownOptions getConfiguration() {
        if ( configuration == null ) {
            return null;
        }
        else {
            return new PegdownOptions(configuration);
        }
    }

    public synchronized void setConfiguration(PegdownOptions configuration) {
        if ( configuration != null ) {
            this.configuration = new PegdownOptions(configuration);
        }
        else {
            this.configuration = null;
        }
    }

    public synchronized boolean isPegdownEnabled() {
        if ( configuration.enabled != null ) {
            return configuration.enabled;
        }
        else {
            return projectConfiguration.isPegdownEnabled();
        }
    }

    public synchronized PegdownOptions.RenderingOptions getRenderingOptions() {
        if ( configuration.renderingOptions != null ) {
            return new PegdownOptions.RenderingOptions(configuration.renderingOptions);
        }
        else {
            return projectConfiguration.getRenderingOptions();
        }
    }

}
