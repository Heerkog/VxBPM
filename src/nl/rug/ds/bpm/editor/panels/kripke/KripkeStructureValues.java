package nl.rug.ds.bpm.editor.panels.kripke;

import nl.rug.ds.bpm.editor.models.KripkeStructure;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class KripkeStructureValues {
    private JTable table;
    private JScrollPane scrollPane;
    private JPanel panel;
    private ConditionTableModel source;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemAdd;
    private JComboBox variableCombo;
    List<Variable> variables;
    private KripkeStructure selectedKripkeStructure;

    public KripkeStructureValues() {
        panel = new JPanel(new BorderLayout());
        /*

        List<EdgeCellVariable> variablesValues = new ArrayList<>();
        variablesValues.add(new EdgeCellVariable(0, ">", "test"));
        source = new ConditionTableModel();

        table = new JTable(source) {
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                return getToolTip(rowAtPoint(p), columnAtPoint(p));
            }
        };
        table.setTableHeader(null);
        table.setRowHeight(20);


        scrollPane = new JScrollPane(table);
        panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("");
        panel.add(label, BorderLayout.NORTH);

        panel.add(scrollPane, BorderLayout.CENTER);
        setSize();
        table.getColumnModel().getColumn(1).setMaxWidth(25);

        setVariableColumn(table, table.getColumnModel().getColumn(0));
        setOperatorColumn(table, table.getColumnModel().getColumn(1));

        EventSource.addListener(EventType.VARIABLES_CHANGED, e -> {
            source.variables = AppCore.app.getVariables();
            source.fireTableDataChanged();
            setSize();
        });
        EventSource.addListener(EventType.KRIPKE_STRUCTURE_TAB_CHANGE, e -> {
            selectedKripkeStructure = AppCore.app.getSelectedKripkeStructure();
            label.setText(selectedKripkeStructure.getName());
            source.fireTableDataChanged();
        });*/

    }

   /* private String getToolTip(int rowIndex, int colIndex) {
        try {
            Variable variable = source.variables.get(rowIndex);

            if (colIndex == 0) {
                return AppCore.app.converter.variables.get(variable.getId()).getValues().toString();
            } else if (colIndex == 2) {
                EdgeCellVariable value = selectedKripkeStructure.getValues().stream().filter(v -> v.getVariableId() == variable.getId()).findFirst().orElse(null);
                if (value != null) {
                    return AppCore.app.converter.variables.get(variable.getId()).getValues(value.getCondition(), value.getValue()).toString();
                } else {
                    return AppCore.app.converter.variables.get(variable.getId()).getValues("==", null).toString();
                }

            }

        } catch (Exception e) {
        }
        return "";
    }*/

    public JPanel getPanel() {
        return panel;
    }

    private void setSize() {
        panel.setPreferredSize(new Dimension(190, 30 + (Math.min(source.variables.size(), 5) * table.getRowHeight())));
        panel.updateUI();
    }

    public void setOperatorColumn(JTable table, TableColumn column) {
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("==");
        comboBox.addItem("!=");
        comboBox.addItem("<");
        comboBox.addItem(">");
        comboBox.addItem(">=");
        comboBox.addItem("<=");
        column.setCellEditor(new DefaultCellEditor(comboBox));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        column.setCellRenderer(renderer);
    }

    public void setVariableColumn(JTable table, TableColumn column) {
        variableCombo = new JComboBox();
        column.setCellEditor(new DefaultCellEditor(variableCombo));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        column.setCellRenderer(renderer);
    }


    class ConditionTableModel extends AbstractTableModel {
        public List<Variable> variables = new ArrayList<>();

        private final String[] columnNames = new String[]{"Variable", "And/or", "value"};

        private final Class[] columnClass = new Class[]{String.class, String.class, String.class};

        public ConditionTableModel() {
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
            return columnIndex == 0 ? false : true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
           /* Variable variable = variables.get(rowIndex);
            EdgeCellVariable value = selectedKripkeStructure.getValues().stream().filter(v -> v.getVariableId() == variable.getId()).findFirst().orElse(null);
            if (0 == columnIndex) {
                return variable.getName();
            } else if (1 == columnIndex) {
                return value == null ? "==" : value.getCondition();
            } else if (2 == columnIndex) {
                return value == null ? "" : value.getValue();
            }*/
            return null;
        }


        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
           /* Variable variable = variables.get(rowIndex);
            EdgeCellVariable value = selectedKripkeStructure.getValues().stream().filter(v -> v.getVariableId() == variable.getId()).findFirst().orElse(null);
            if (value == null) {
                value = new EdgeCellVariable(variable.getId(), "", "");
                selectedKripkeStructure.getValues().add(value);
            }
            if (1 == columnIndex) {
                value.setCondition((String) aValue);
            } else if (2 == columnIndex) {
                value.setValue((String) aValue);
            }
            EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_VALUE_CHANGE, selectedKripkeStructure.getId());*/
        }
    }
}
