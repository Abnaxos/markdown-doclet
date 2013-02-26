package ch.raffael.doclets.pegdown.integrations.idea;

import javax.swing.JComponent;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;


/**
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
