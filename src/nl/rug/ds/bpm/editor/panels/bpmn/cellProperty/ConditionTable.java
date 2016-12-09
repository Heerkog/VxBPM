package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.EdgeCellVariable;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mark Kloosterhuis.
 */
public class ConditionTable implements ActionListener {
    private JTable table;
    private JScrollPane scrollPane;
    private JPanel panel;
    private CellPropertyPanel parent;
    private ConditionTableModel source;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemAdd;
    private JComboBox variableCombo;
    List<Variable> variables;

    public ConditionTable(CellPropertyPanel parent) {
        this.parent = parent;
        List<EdgeCellVariable> variablesValues = new ArrayList<>();
        source = new ConditionTableModel(this, variablesValues);

        table = new JTable(source);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());

                    if (!source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRow() > -1) {
                    fillComboList(table.getSelectedRow());
                }
            }
        });


        table.setTableHeader(null);
        table.setRowHeight(20);

        popupMenu = new JPopupMenu();
        menuItemAdd = new JMenuItem("Delete");
        menuItemAdd.addActionListener(this);
        popupMenu.add(menuItemAdd);
        table.setComponentPopupMenu(popupMenu);


        scrollPane = new JScrollPane(table);
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.RED);

        JButton addButton = new JButton("Add Variable");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillComboList(-1);
                if (variableCombo.getItemCount() > 0) {

                    source.variables.add(new EdgeCellVariable(getVariableByName((String) variableCombo.getItemAt(0)).getId(), "==", ""));
                    source.fireTableDataChanged();
                    setSize();
                    variableChanged();
                }
            }
        });


        panel.add(addButton, BorderLayout.SOUTH);


        panel.add(scrollPane, BorderLayout.CENTER);
        setSize();
        table.getColumnModel().getColumn(1).setMaxWidth(25);

        setVariableColumn(table, table.getColumnModel().getColumn(0));
        setOperatorColumn(table, table.getColumnModel().getColumn(1));

        EventSource.addListener(EventType.VARIABLES_CHANGED, e -> {
            variables = AppCore.app.getVariables();
            if (variables != null) {
                fillComboList();
            }
        });
    }

    InputCellConstraint inputCellConstraint;

    public void setCellConstraint(InputCellConstraint inputCellConstraint) {
        this.inputCellConstraint = inputCellConstraint;

    }

    public void variableChanged() {
        if (this.inputCellConstraint != null) {
            this.inputCellConstraint.updateGraphView();

        }
        EventSource.fireEvent(EventType.BPMN_REDRAW, "");
        fillComboList();


    }

    private void fillComboList() {
        fillComboList(-1);
    }

    private void fillComboList(int row) {
        int currentVariableId = -1;
        if (row >= 0) {
            EdgeCellVariable edgeCell = source.variables.get(row);
            if (edgeCell != null)
                currentVariableId = edgeCell.getVariableId();
        }

        variableCombo.removeAllItems();
        int selectedIndex = -1;
        for (Variable variable : variables) {
            final int currentId = currentVariableId;
            if (source.variables.stream().filter(v -> currentId != variable.getId() && v.getVariableId() == variable.getId()).count() == 0) {
                variableCombo.addItem(variable.getName());
                if (currentId == variable.getId())
                    selectedIndex = variableCombo.getItemCount() - 1;
            }
        }
        if (selectedIndex != -1)
            variableCombo.setSelectedIndex(selectedIndex);
        variableCombo.updateUI();

    }

    public JPanel getPanel() {
        return this.panel;
    }

    private void setSize() {
        panel.setPreferredSize(new Dimension(190, 30 + (Math.min(source.variables.size(), 5) * table.getRowHeight())));
        panel.updateUI();
    }

    private Variable getVariableById(int id) {
        return variables.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }

    private Variable getVariableByName(String name) {
        return variables.stream().filter(v -> v.getName() == name).findFirst().orElse(null);
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
        variables = AppCore.app.getVariables();
        if (variables != null) {
            variableCombo.removeAllItems();
            for (String variableStr : variables.stream().map(v -> v.getName()).toArray(String[]::new)) {
                variableCombo.addItem(variableStr);
            }
        }
        column.setCellEditor(new DefaultCellEditor(variableCombo));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        column.setCellRenderer(renderer);
    }

    public void addToParent() {
        parent.addComponent(new JLabel("Variables"));
        parent.addComponent(panel);
    }


    public List<EdgeCellVariable> getVariables() {
        return source.variables;
    }

    public void setVariables(List<EdgeCellVariable> variables) {
        source.variables = variables;
        source.fireTableDataChanged();
        setSize();
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemAdd) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                source.RemoveAt(0);
            }
        }
    }

    class ConditionTableModel extends AbstractTableModel {
        private ConditionTable conditionTable;
        public List<EdgeCellVariable> variables;

        private final String[] columnNames = new String[]{"Variable", "And/or", "value"};

        private final Class[] columnClass = new Class[]{String.class, String.class, String.class};

        public ConditionTableModel(ConditionTable conditionTable, List<EdgeCellVariable> variables) {
            this.variables = variables;
            this.conditionTable = conditionTable;
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
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            EdgeCellVariable row = variables.get(rowIndex);


            if (0 == columnIndex) {
                Variable val = getVariableById(row.getVariableId());
                return val == null ? "" : val.getName();
            } else if (1 == columnIndex) {
                return row.getCondition();
            } else if (2 == columnIndex) {
                return row.getValue();
            }
            return null;
        }

        public void RemoveAt(int row) {
            variables.remove(row);
            fireTableRowsDeleted(row, row);
            fireTableDataChanged();
            conditionTable.variableChanged();
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex >= variables.size())
                return;

            EdgeCellVariable row = variables.get(rowIndex);
            if (0 == columnIndex && aValue != null) {
                row.setVariableId(getVariableByName((String) aValue).getId());
            } else if (1 == columnIndex) {
                row.setCondition((String) aValue);
            } else if (2 == columnIndex) {
                row.setValue((String) aValue);
            }
            conditionTable.variableChanged();

        }
    }

}
