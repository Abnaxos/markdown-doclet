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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;

import lombok.EqualsAndHashCode;

/**
 * The project options form.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PegdownDocletOptionsForm {

    private JPanel root;
    private JCheckBox enablePegdownDocletCheckBox;
    private JButton optionsButton;
    private JTable modulesTable;

    private final RenderingOptionsForm.OptionsAction optionsAction;

    private final Project project;
    private PegdownOptions projectOptions;

    private final List<ModuleTableEntry> modules = new ArrayList<>();
    private final ModulesTableModel modulesTableModel = new ModulesTableModel();

    public PegdownDocletOptionsForm(Project project) {
        this.project = project;
        for ( Module module : ModuleManager.getInstance(project).getModules() ) {
            modules.add(new ModuleTableEntry(module));
        }
        Collections.sort(modules, new Comparator<ModuleTableEntry>() {
            @Override
            public int compare(ModuleTableEntry a, ModuleTableEntry b) {
                return String.CASE_INSENSITIVE_ORDER.compare(a.module.getName(), b.module.getName());
            }
        });
        modulesTable.setModel(modulesTableModel);
        modulesTable.setTableHeader(null);
        modulesTable.setShowGrid(false);
        modulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        CellRenderer renderer = new CellRenderer();
        modulesTable.setRowHeight(renderer.getPreferredSize().height);
        modulesTable.getColumn("Module").setCellRenderer(renderer);
        CellRenderer editor = new CellRenderer();
        modulesTable.getColumn("Module").setCellEditor(editor);
        enablePegdownDocletCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                synchronized ( PegdownDocletOptionsForm.this ) {
                    projectOptions.enabled = enablePegdownDocletCheckBox.isSelected();
                }
            }
        });
        optionsAction = new RenderingOptionsForm.OptionsAction(projectOptions);
        optionsButton.setAction(optionsAction);
        reset();
    }

    public JComponent getComponent() {
        return root;
    }

    public synchronized boolean isModified() {
        if ( !Plugin.projectConfiguration(project).getConfiguration().equals(projectOptions) ) {
            return true;
        }
        else {
            for ( ModuleTableEntry entry : modules ) {
                if ( !Objects.equals(Plugin.moduleConfiguration(entry.module).getConfiguration(), entry.options) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized void reset() {
        projectOptions = Plugin.projectConfiguration(project).getConfiguration();
        optionsAction.setOptions(projectOptions);
        for ( ModuleTableEntry entry : modules ) {
            entry.options = Plugin.moduleConfiguration(entry.module).getConfiguration();
        }
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                enablePegdownDocletCheckBox.setSelected(projectOptions.enabled);
            }
        });
    }

    public synchronized void commit() {
        Plugin.projectConfiguration(project).setConfiguration(projectOptions);
        for ( ModuleTableEntry entry : modules ) {
            Plugin.moduleConfiguration(entry.module).setConfiguration(entry.options);
        }
    }

    private class ModulesTableModel extends AbstractTableModel {
        @Override
        public String getColumnName(int column) {
            return "Module";
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return ModuleTableEntry.class;
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
        @Override
        public int getRowCount() {
            return modules.size();
        }
        @Override
        public int getColumnCount() {
            return 1;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return modules.get(rowIndex);
        }
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @EqualsAndHashCode
    private class ModuleTableEntry {
        private final Module module;
        private PegdownOptions options;
        private ModuleTableEntry(Module module) {
            this.module = module;
        }
    }

    private class CellRenderer extends JPanel implements TableCellRenderer, TableCellEditor {
        private final DefaultTableCellRenderer listRenderer = new DefaultTableCellRenderer();
        private final JComboBox<String> confComboBox = new JComboBox<>();
        private final JButton renderOptions = new JButton("Options...");
        private final RenderingOptionsForm.OptionsAction optionsAction = new RenderingOptionsForm.OptionsAction();

        private ModuleTableEntry entry;

        private final CopyOnWriteArrayList<CellEditorListener> listeners = new CopyOnWriteArrayList<>();

        private CellRenderer() {
            super(new BorderLayout());
            add(listRenderer, BorderLayout.CENTER);
            JPanel edit = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            for ( ModuleConfComboBoxEntry e : ModuleConfComboBoxEntry.values() ) {
                confComboBox.addItem(e.text);
            }
            renderOptions.setAction(optionsAction);
            optionsAction.setEnabled(false);
            edit.add(confComboBox);
            edit.add(renderOptions);
            add(edit, BorderLayout.EAST);
            listRenderer.setIcon(AllIcons.Actions.Module);
            listRenderer.setText("Module Name");
            confComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    optionsAction.setEnabled(confComboBox.getSelectedIndex() == ModuleConfComboBoxEntry.CUSTOM.ordinal());
                    if ( entry != null ) {
                        switch ( ModuleConfComboBoxEntry.values()[confComboBox.getSelectedIndex()] ) {
                            case PROJECT:
                                entry.options.enabled = null;
                                break;
                            case DISABLED:
                                entry.options.enabled = false;
                                break;
                            case ENABLED:
                                entry.options.enabled = true;
                                entry.options.renderingOptions = null;
                                break;
                            case CUSTOM:
                                entry.options.enabled = true;
                                entry.options.renderingOptions = Plugin.projectConfiguration(project).getRenderingOptions();
                                break;
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ModuleTableEntry entry = (ModuleTableEntry)value;
            listRenderer.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
            listRenderer.setOpaque(false);
            listRenderer.setIcon(AllIcons.Actions.Module);
            listRenderer.setText(entry.module.getName());
            for ( ModuleConfComboBoxEntry e : ModuleConfComboBoxEntry.values() ) {
                if ( e.matches(entry.options) ) {
                    confComboBox.setSelectedIndex(e.ordinal());
                    break;
                }
            }
            return this;
        }

        @Override
        public Object getCellEditorValue() {
            return entry;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }

        @Override
        public boolean stopCellEditing() {
            for ( CellEditorListener l : listeners ) {
                l.editingStopped(new ChangeEvent(this));
            }
            return true;
        }

        @Override
        public void cancelCellEditing() {
            for ( CellEditorListener l : listeners ) {
                l.editingCanceled(new ChangeEvent(this));
            }
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            setEntry((ModuleTableEntry)value);
            getTableCellRendererComponent(table, value, isSelected, false, row, column);
            return this;
        }

        private void setEntry(ModuleTableEntry entry) {
            this.entry = entry;
            optionsAction.setOptions(entry.options);
        }
    }

    private static enum ModuleConfComboBoxEntry {
        PROJECT("Use Project Settings") {
            @Override
            boolean matches(PegdownOptions options) {
                return options.enabled == null;
            }
        },
        DISABLED("Disabled") {
            @Override
            boolean matches(PegdownOptions options) {
                return Boolean.FALSE.equals(options.enabled);
            }
        },
        ENABLED("Enabled") {
            @Override
            boolean matches(PegdownOptions options) {
                return Boolean.TRUE.equals(options.enabled) && options.renderingOptions == null;
            }
        },
        CUSTOM("Enabled With Custom Settings") {
            @Override
            boolean matches(PegdownOptions options) {
                return Boolean.TRUE.equals(options.enabled) && options.renderingOptions != null;
            }
        };

        private final String text;

        private ModuleConfComboBoxEntry(String text) {
            this.text = text;
        }

        abstract boolean matches(PegdownOptions options);
    }

}
