package ch.raffael.doclets.pegdown.integrations.idea;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Plugin {

    public static final String PLUGIN_NAME = "PegdownDocletIdea";

    public static final String PROJECT_CONFIG_NAME = PLUGIN_NAME + ".ProjectConfig";
    public static final String MODULE_CONFIG_NAME = PLUGIN_NAME + ".ModuleConfig";

    private Plugin() {
    }

    public static ProjectConfiguration projectConfiguration(Project project) {
        return (ProjectConfiguration)project.getComponent(PROJECT_CONFIG_NAME);
    }

    public static ModuleConfiguration moduleConfiguration(Module module) {
        return (ModuleConfiguration)module.getComponent(MODULE_CONFIG_NAME);
    }

}
