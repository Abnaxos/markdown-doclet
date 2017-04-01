/*
 * Copyright 2013 Raffael Herzog
 *
 * This file is part of markdown-doclet.
 *
 * markdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with markdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.raffael.mddoclet.integrations.idea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A project component holding the project-wide configuration.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
// todo: for IJ 2016: @State(name = Plugin.PLUGIN_NAME + ".ProjectConfiguration", storages=@Storage("markdown-doclet.xml"))
@State(name = Plugin.PLUGIN_NAME + ".ProjectConfiguration",
       storages = {
               @Storage(file = StoragePathMacros.PROJECT_FILE),
               @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/markdown-doclet.xml")
       })
public class ProjectConfiguration implements ProjectComponent, PersistentStateComponent<MarkdownOptions> {

    private MarkdownOptions configuration = new MarkdownOptions();

    public ProjectConfiguration(Project project) {
        configuration.enabled = false;
        configuration.renderingOptions = new MarkdownOptions.RenderingOptions();
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
    public synchronized MarkdownOptions getState() {
        return new MarkdownOptions(configuration);
    }

    @Override
    public synchronized void loadState(MarkdownOptions state) {
        configuration = new MarkdownOptions(state);
        if ( configuration.enabled == null ) {
            configuration.enabled = false;
        }
        if ( configuration.renderingOptions == null ) {
            configuration.renderingOptions = new MarkdownOptions.RenderingOptions();
        }
    }

    public synchronized MarkdownOptions getConfiguration() {
        return new MarkdownOptions(configuration);
    }

    public synchronized void setConfiguration(MarkdownOptions configuration) {
        this.configuration = new MarkdownOptions(configuration);
    }

    public synchronized boolean isMarkdownEnabled() {
        return configuration.enabled;
    }

    public synchronized MarkdownOptions.RenderingOptions getRenderingOptions() {
        return new MarkdownOptions.RenderingOptions(configuration.renderingOptions);
    }

}
