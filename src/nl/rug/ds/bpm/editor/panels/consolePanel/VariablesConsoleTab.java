package nl.rug.ds.bpm.editor.panels.consolePanel;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.KripkeStructure;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Mark Kloosterhuis.
 */
public class VariablesConsoleTab extends AbstractKripkeConsoleTab<VariablesConsoleTab.StructureConsoleTab> {
    public VariablesConsoleTab() {
        super();
    }

    StructureConsoleTab CreateTab(KripkeStructure structure) {
        return new StructureConsoleTab(structure.getId());
    }

    public JPanel getPanel() {
        return panel;
    }

    public class StructureConsoleTab implements IJPanel {
        JScrollPane scrollPanel;
        JTable table;
        JPanel panel;
        VariableTableModel source;
        int structureId;

        public StructureConsoleTab(int structureId) {
            panel = new JPanel();
            panel.setLayout(new GridLayout(0, 1));
            panel.setBorder(new EmptyBorder(0, 0, 0, 0));

            this.structureId = structureId;
            source = new VariableTableModel(new ArrayList<>());
            tabbedPanel.addChangeListener(e -> {
                EventSource.fireEvent(EventType.KRIPKE_CONSOLE_TABVIEW_CHANGED, tabbedPanel.getSelectedIndex());
            });

            table = new JTable(source);
            table.setBackground(Color.WHITE);
            scrollPanel = new JScrollPane(table);
            panel.add(scrollPanel);

            EventSource.addListener(EventType.KRIPKE_STRUCTURE_CHANGE, (e) -> {
                if ((int) e == this.structureId) {
                    getKripkeModelString();
                }
            });
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        private void getKripkeModelString() {
            KripkeStructure structure = AppCore.app.getKripkeStructures().get(this.structureId);
            source.variables = structure.getVariableList();
            source.fireTableDataChanged();
        }

        public JPanel getPanel() {
            return panel;
        }
    }


    public class VariableTableModel extends AbstractTableModel {
        public java.util.List<Variable> variables;

        private final String[] columnNames = new String[]{"Id", "Name", "Values"};

        private final Class[] columnClass = new Class[]{String.class, String.class, String.class};

        public VariableTableModel(java.util.List<Variable> variables) {

            this.variables = variables;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClass[columnIndex];
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return variables.size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Variable row = variables.get(rowIndex);
            if (0 == columnIndex) {
                // return row.edge.getId();
                return row.getId();
            } else if (1 == columnIndex) {
                return row.getName();
            } else if (2 == columnIndex) {
                return row.getValues().toString();
            }
            return null;
        }

    }
}
