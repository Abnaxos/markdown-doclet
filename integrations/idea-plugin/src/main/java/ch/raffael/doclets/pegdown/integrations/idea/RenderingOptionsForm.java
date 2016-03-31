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
import java.awt.FlowLayout;
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
    private JCheckBox smartsCheckBox;
    private JCheckBox quotesCheckBox;
    private JCheckBox abbreviationsCheckBox;
    private JCheckBox autolinksCheckBox;
    private JCheckBox tablesCheckBox;
    private JCheckBox definitionsCheckBox;
    private JCheckBox fencedCodeBlocksCheckBox;
    private JCheckBox wikiLinksCheckBox;
    private JCheckBox strikethroughCheckBox;
    private JCheckBox anchorLinksCheckBox;
    private JCheckBox suppressHTMLBlocksCheckBox;
    private JCheckBox suppressInlineHTMLCheckBox;
    private JCheckBox atxHeaderSpaceCheckBox;
    private JCheckBox forceListItemParaCheckBox;
    private JCheckBox relaxedHRulesCheckBox;
    private JCheckBox taskListItemsCheckBox;
    private JCheckBox extAnchorLinksCheckBox;

    private PegdownOptions.RenderingOptions options;
    private PegdownOptions.RenderingOptions originalOptions;

    public RenderingOptionsForm() {
        this(null);
    }

    public RenderingOptionsForm(PegdownOptions.RenderingOptions opts) {
        smartsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.quotes = smartsCheckBox.isSelected();
            }
        });
        quotesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.smarts = quotesCheckBox.isSelected();
            }
        });
        abbreviationsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.abbreviations = abbreviationsCheckBox.isSelected();
            }
        });
        autolinksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.autolinks = autolinksCheckBox.isSelected();
            }
        });
        tablesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.tables = tablesCheckBox.isSelected();
            }
        });
        definitionsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.definitions = definitionsCheckBox.isSelected();
            }
        });
        fencedCodeBlocksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.fencedCodeBlocks = fencedCodeBlocksCheckBox.isSelected();
            }
        });
        wikiLinksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.wikiLinks = wikiLinksCheckBox.isSelected();
            }
        });
        strikethroughCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.strikethrough = strikethroughCheckBox.isSelected();
            }
        });
        anchorLinksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.anchorLinks = anchorLinksCheckBox.isSelected();
                if ( !options.anchorLinks ) {
                    extAnchorLinksCheckBox.setSelected(false);
                }
            }
        });
        suppressHTMLBlocksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.suppressHtmlBlocks = suppressHTMLBlocksCheckBox.isSelected();
            }
        });
        suppressInlineHTMLCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.suppressInlineHtml = suppressInlineHTMLCheckBox.isSelected();
            }
        });
        atxHeaderSpaceCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.atxHeaderSpace = atxHeaderSpaceCheckBox.isSelected();
            }
        });
        forceListItemParaCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.forceListItemPara = forceListItemParaCheckBox.isSelected();
            }
        });
        relaxedHRulesCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.relaxedHRules = relaxedHRulesCheckBox.isSelected();

            }
        });
        taskListItemsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.taskListItems = taskListItemsCheckBox.isSelected();
            }
        });
        extAnchorLinksCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                options.extAnchorLinks = extAnchorLinksCheckBox.isSelected();
                if ( options.extAnchorLinks ) {
                    anchorLinksCheckBox.setSelected(true);
                }
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
        smartsCheckBox.setSelected(options.quotes);
        quotesCheckBox.setSelected(options.smarts);
        abbreviationsCheckBox.setSelected(options.abbreviations);
        autolinksCheckBox.setSelected(options.autolinks);
        tablesCheckBox.setSelected(options.tables);
        definitionsCheckBox.setSelected(options.definitions);
        fencedCodeBlocksCheckBox.setSelected(options.fencedCodeBlocks);
        wikiLinksCheckBox.setSelected(options.wikiLinks);
        strikethroughCheckBox.setSelected(options.strikethrough);
        anchorLinksCheckBox.setSelected(options.anchorLinks);
        suppressHTMLBlocksCheckBox.setSelected(options.suppressHtmlBlocks);
        suppressInlineHTMLCheckBox.setSelected(options.suppressInlineHtml);
        atxHeaderSpaceCheckBox.setSelected(options.atxHeaderSpace);
        forceListItemParaCheckBox.setSelected(options.forceListItemPara);
        relaxedHRulesCheckBox.setSelected(options.relaxedHRules);
        taskListItemsCheckBox.setSelected(options.taskListItems);
        extAnchorLinksCheckBox.setSelected(options.extAnchorLinks);
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
        root.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("Formatting"));
        definitionsCheckBox = new JCheckBox();
        definitionsCheckBox.setText("Definition Lists");
        panel3.add(definitionsCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        taskListItemsCheckBox = new JCheckBox();
        taskListItemsCheckBox.setText("Task List Items");
        panel3.add(taskListItemsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strikethroughCheckBox = new JCheckBox();
        strikethroughCheckBox.setText("Strikethrough");
        panel3.add(strikethroughCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fencedCodeBlocksCheckBox = new JCheckBox();
        fencedCodeBlocksCheckBox.setText("Fenced Code Blocks");
        panel3.add(fencedCodeBlocksCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tablesCheckBox = new JCheckBox();
        tablesCheckBox.setText("Tables");
        panel3.add(tablesCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        abbreviationsCheckBox = new JCheckBox();
        abbreviationsCheckBox.setText("Abbreviations");
        panel3.add(abbreviationsCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forceListItemParaCheckBox = new JCheckBox();
        forceListItemParaCheckBox.setText("Force Paragraphs for List Items");
        panel3.add(forceListItemParaCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("Advanced Formatting"));
        suppressHTMLBlocksCheckBox = new JCheckBox();
        suppressHTMLBlocksCheckBox.setText("Suppress HTML Blocks");
        panel4.add(suppressHTMLBlocksCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        suppressInlineHTMLCheckBox = new JCheckBox();
        suppressInlineHTMLCheckBox.setText("Suppress Inline HTML");
        panel4.add(suppressInlineHTMLCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        atxHeaderSpaceCheckBox = new JCheckBox();
        atxHeaderSpaceCheckBox.setText("Require Space After '#'");
        panel4.add(atxHeaderSpaceCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        relaxedHRulesCheckBox = new JCheckBox();
        relaxedHRulesCheckBox.setText("Relaxed Horizontal Rules");
        panel4.add(relaxedHRulesCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder("Typography"));
        smartsCheckBox = new JCheckBox();
        smartsCheckBox.setText("Typographic Quotes");
        panel5.add(smartsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        quotesCheckBox = new JCheckBox();
        quotesCheckBox.setText("Typographic Ellipsis, Dashes, Apostrophes");
        panel5.add(quotesCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder("Links"));
        autolinksCheckBox = new JCheckBox();
        autolinksCheckBox.setText("Automatic Links");
        panel6.add(autolinksCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wikiLinksCheckBox = new JCheckBox();
        wikiLinksCheckBox.setText("Wiki-Style Links");
        panel6.add(wikiLinksCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel6.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        anchorLinksCheckBox = new JCheckBox();
        anchorLinksCheckBox.setText("Anchors for Headers");
        panel7.add(anchorLinksCheckBox);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel7.add(panel8);
        panel8.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0), null));
        extAnchorLinksCheckBox = new JCheckBox();
        extAnchorLinksCheckBox.setText("Full Header");
        panel8.add(extAnchorLinksCheckBox);
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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
