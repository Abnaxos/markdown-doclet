package ch.raffael.doclets.pegdown.integrations.idea;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class RenderingOptionsForm {

    private JPanel root;
    private JCheckBox autolinksCheckBox;
    private JCheckBox definitionsCheckBox;
    private JCheckBox typographicQuotesCheckBox;
    private JCheckBox typographicEllipsisDashesApostrophesCheckBox;
    private JCheckBox tablesCheckBox;
    private JCheckBox wikiStyleLinksCheckBox;
    private JCheckBox abbreviationsCheckBox;
    private JCheckBox disableHTMLBlocksCheckBox;
    private JCheckBox disableInlineHTMLCheckBox;
    private JCheckBox fencedCodeBlocksCheckBox;

    private PegdownOptions.RenderingOptions options;
    private PegdownOptions.RenderingOptions originalOptions;

    public RenderingOptionsForm() {
        this(null);
    }

    public RenderingOptionsForm(PegdownOptions.RenderingOptions opts) {
        autolinksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.autolinks = autolinksCheckBox.isSelected();
            }
        });
        definitionsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.definitions = definitionsCheckBox.isSelected();
            }
        });
        typographicQuotesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.quotes = typographicQuotesCheckBox.isSelected();
            }
        });
        typographicEllipsisDashesApostrophesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.smarts = typographicEllipsisDashesApostrophesCheckBox.isSelected();
            }
        });
        tablesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.tables = tablesCheckBox.isSelected();
            }
        });
        wikiStyleLinksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.wikiLinks = wikiStyleLinksCheckBox.isSelected();
            }
        });
        abbreviationsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.abbreviations = abbreviationsCheckBox.isSelected();
            }
        });
        disableHTMLBlocksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.noHtmlBlocks = disableHTMLBlocksCheckBox.isSelected();
            }
        });
        disableInlineHTMLCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.noInlineHtml = disableInlineHTMLCheckBox.isSelected();
            }
        });
        fencedCodeBlocksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.fencedCodeBlocks = fencedCodeBlocksCheckBox.isSelected();
            }
        });
        set(opts);
    }

    public JComponent getComponent() {
        return root;
    }

    public void set(PegdownOptions.RenderingOptions options) {
        if ( options != null ) {
            originalOptions = new PegdownOptions.RenderingOptions(options);
        }
        else {
            originalOptions = new PegdownOptions.RenderingOptions();
        }
        doSet(originalOptions);
    }

    private void doSet(PegdownOptions.RenderingOptions options) {
        this.options = new PegdownOptions.RenderingOptions(options);
        autolinksCheckBox.setSelected(options.autolinks);
        definitionsCheckBox.setSelected(options.definitions);
        typographicQuotesCheckBox.setSelected(options.quotes);
        typographicEllipsisDashesApostrophesCheckBox.setSelected(options.smarts);
        tablesCheckBox.setSelected(options.tables);
        wikiStyleLinksCheckBox.setSelected(options.wikiLinks);
        abbreviationsCheckBox.setSelected(options.abbreviations);
        disableHTMLBlocksCheckBox.setSelected(options.noHtmlBlocks);
        disableInlineHTMLCheckBox.setSelected(options.noInlineHtml);
        fencedCodeBlocksCheckBox.setSelected(options.fencedCodeBlocks);
    }

    public PegdownOptions.RenderingOptions get() {
        return new PegdownOptions.RenderingOptions(options);
    }

    public PegdownOptions.RenderingOptions revert() {
        doSet(originalOptions);
        return options;
    }

    public static class OptionsAction extends AbstractAction {
        private PegdownOptions options;
        public OptionsAction() {
            super("Rendering...");
        }
        public OptionsAction(PegdownOptions options) {
            this();
            this.options = options;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            DialogBuilder dialogBuilder = new DialogBuilder((Component)e.getSource());
            dialogBuilder.setTitle("Pegdown Rendering Options");
            dialogBuilder.addOkAction();
            dialogBuilder.addCancelAction();
            RenderingOptionsForm form = new RenderingOptionsForm(options.renderingOptions);
            dialogBuilder.setCenterPanel(form.getComponent());
            int exitCode = dialogBuilder.show();
            if ( exitCode == DialogWrapper.OK_EXIT_CODE ) {
                options.renderingOptions = form.get();
            }
        }
        public PegdownOptions getOptions() {
            return options;
        }
        public void setOptions(PegdownOptions configuration) {
            this.options = configuration;
        }
    }

}
