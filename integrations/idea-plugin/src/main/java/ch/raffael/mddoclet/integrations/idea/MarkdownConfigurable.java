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

import javax.swing.JComponent;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;


/**
 * Adds the plugin's configuration panel to "Settings -- Markdown Doclet".
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class MarkdownConfigurable implements Configurable {

    private final Project project;
    private MarkdownDocletOptionsForm editor;

    public MarkdownConfigurable(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Markdown Doclet";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if ( editor == null ) {
            editor = new MarkdownDocletOptionsForm(project);
        }
        return editor.getComponent();
    }

    @Override
    public boolean isModified() {
        return editor != null && editor.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        if ( editor != null ) {
            editor.commit();
        }
    }

    @Override
    public void reset() {
        if ( editor != null ) {
            editor.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        editor = null;
    }
}
