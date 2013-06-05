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

import javax.swing.JComponent;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;


/**
 * Adds the plugin's configuration panel to "Settings -- Pegdown Doclet".
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownConfigurable implements Configurable {

    private final Project project;
    private PegdownDocletOptionsForm editor;

    public PegdownConfigurable(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Pegdown Doclet";
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
            editor = new PegdownDocletOptionsForm(project);
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
