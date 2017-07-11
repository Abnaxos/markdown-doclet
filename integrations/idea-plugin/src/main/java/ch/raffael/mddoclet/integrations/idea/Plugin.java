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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;


/**
 * Some utilities for the plugin.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Plugin {

    public static final boolean DEBUG = Boolean.getBoolean("ch.raffael.mddoclet.integrations.idea.debug");

    public static final String PLUGIN_NAME = "ch.raffael.mddoclet.integrations.idea.MarkdownDocletIdea";

    public static final String TEMP_FILE_MANAGER_NAME = PLUGIN_NAME + ".TempFileManager";
    public static final String PROJECT_CONFIG_NAME = PLUGIN_NAME + ".ProjectConfig";
    public static final String MODULE_CONFIG_NAME = PLUGIN_NAME + ".ModuleConfig";

    private Plugin() {
    }

    public static TempFileManager tempFileManager() {
        return (TempFileManager)ApplicationManager.getApplication().getComponent(TEMP_FILE_MANAGER_NAME);
    }

    public static ProjectConfiguration projectConfiguration(Project project) {
        return (ProjectConfiguration)project.getComponent(PROJECT_CONFIG_NAME);
    }

    public static ModuleConfiguration moduleConfiguration(Module module) {
        return (ModuleConfiguration)module.getComponent(MODULE_CONFIG_NAME);
    }

    public static void print(String description, String output) {
        if ( DEBUG ) {
            System.out.println("\n\n\n*** " + description + "\n");
            System.out.println(output);
        }
    }

}
