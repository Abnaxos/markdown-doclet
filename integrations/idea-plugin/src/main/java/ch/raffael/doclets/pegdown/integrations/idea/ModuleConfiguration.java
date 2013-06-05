/*
 * Copyright 2013 Raffael Herzog
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
 */
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
 * A module component holding the module-specific configuration.
 *
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
