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

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;


/**
 * The rendering options form.
 *
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Pegdown Extensions"));
        autolinksCheckBox = new JCheckBox();
        autolinksCheckBox.setText("Autolinks");
        panel1.add(autolinksCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wikiStyleLinksCheckBox = new JCheckBox();
        wikiStyleLinksCheckBox.setText("Wiki-Style Links");
        panel1.add(wikiStyleLinksCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        abbreviationsCheckBox = new JCheckBox();
        abbreviationsCheckBox.setText("Abbreviations");
        panel1.add(abbreviationsCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        disableHTMLBlocksCheckBox = new JCheckBox();
        disableHTMLBlocksCheckBox.setText("Disable HTML Blocks");
        panel1.add(disableHTMLBlocksCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        disableInlineHTMLCheckBox = new JCheckBox();
        disableInlineHTMLCheckBox.setText("Disable Inline HTML");
        panel1.add(disableInlineHTMLCheckBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fencedCodeBlocksCheckBox = new JCheckBox();
        fencedCodeBlocksCheckBox.setText("Fenced Code Blocks");
        panel1.add(fencedCodeBlocksCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        definitionsCheckBox = new JCheckBox();
        definitionsCheckBox.setText("Definitions");
        panel1.add(definitionsCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typographicQuotesCheckBox = new JCheckBox();
        typographicQuotesCheckBox.setText("Typographic Quotes");
        panel1.add(typographicQuotesCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typographicEllipsisDashesApostrophesCheckBox = new JCheckBox();
        typographicEllipsisDashesApostrophesCheckBox.setText("Typographic Ellipsis, Dashes, Apostrophes");
        panel1.add(typographicEllipsisDashesApostrophesCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tablesCheckBox = new JCheckBox();
        tablesCheckBox.setText("Tables");
        panel1.add(tablesCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
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
