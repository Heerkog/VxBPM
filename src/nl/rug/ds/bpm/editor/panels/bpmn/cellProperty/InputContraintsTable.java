package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty;

import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by Mark Kloosterhuis.
 */
public class InputContraintsTable implements ActionListener {
    JPanel panel;
    CellPropertyPanel parent;
    ConditionTableModel source;
    JTable table;
    JScrollPane scrollPane;
    InputCell inputCell;
    InputConstraintEdit editPanel;
    InputCellConstraint prevConstraint;
    JPopupMenu popupMenu;
    JMenuItem menuItemRemove;

    public InputContraintsTable(CellPropertyPanel parent, InputConstraintEdit editPanel) {
        this.parent = parent;
        this.editPanel = editPanel;
        source = new ConditionTableModel(new ArrayList<>());
        table = new JTable(source);
        table.setTableHeader(null);
        table.setRowHeight(20);
        EventSource.addListener(EventType.BPMN_REDRAW, e -> {
            source.fireTableDataChanged();
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRow() > -1) {
                    InputCellConstraint cellCondition = source.conditions.get(table.getSelectedRow());
                    if (prevConstraint != null) {
                        prevConstraint.setBold(false);
                    }
                    prevConstraint = cellCondition;
                    cellCondition.setBold(true);
                    parent.setInputCell(cellCondition);
                }
            }
        });


        scrollPane = new JScrollPane(table);

        panel = new JPanel();
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.RED);


        // constructs the popup menu
        popupMenu = new JPopupMenu();
        menuItemRemove = new JMenuItem("Delete");

        menuItemRemove.addActionListener(this);

        popupMenu.add(menuItemRemove);

        // sets the popup menu for the table
        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new TableMouseListener(table));


        JButton addButton = new JButton("Add condition");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputCell.addConstraint();

                source.conditions = inputCell.getConstraints();
                source.fireTableDataChanged();
                setSize();
                EventSource.fireEvent(EventType.BPMN_REDRAW, null);
            }
        });
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);
        setSize();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemRemove) {

            // InputCellConstraint selected =  source.conditions.get(table.getSelectedRow());
            int row = table.getSelectedRow();
            source.RemoveAt(row);


        }
    }

    private void setSize() {
        panel.setPreferredSize(new Dimension(190, 30 + (Math.min(source.conditions.size(), 5) * table.getRowHeight())));
        panel.updateUI();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void addToParent() {
        parent.addComponent(new JLabel("Conditions"));
        parent.addComponent(panel);
    }

    public void setInputCell(InputCell inputCell) {
        this.inputCell = inputCell;
        source.conditions = inputCell.getConstraints();
        source.fireTableDataChanged();
        setSize();
    }
    class TableMouseListener extends MouseAdapter {

        private JTable table;

        public TableMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mousePressed(MouseEvent event) {
            // selects the row at which point the mouse is clicked
            Point point = event.getPoint();
            int currentRow = table.rowAtPoint(point);
            table.setRowSelectionInterval(currentRow, currentRow);
        }
    }

    class ConditionTableModel extends AbstractTableModel {
        public java.util.List<InputCellConstraint> conditions;

        private final String[] columnNames = new String[]{"Variable"};

        private final Class[] columnClass = new Class[]{String.class};

        public ConditionTableModel(java.util.List<InputCellConstraint> conditions) {
            this.conditions = conditions;
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
            return conditions.size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            InputCellConstraint row = conditions.get(rowIndex);
            return row.getTableName();

            /*if (0 == columnIndex) {
                Variable val = getVariableById(row.getVariableId());
                return val == null ? "" : val.getName();
            } else if (1 == columnIndex) {
                return row.getCondition();
            } else if (2 == columnIndex) {
                return row.getValue();
            }
            return null;*/
        }


        public void RemoveAt(int row) {
            conditions.remove(row);
            fireTableRowsDeleted(row, row);
            fireTableDataChanged();
            EventSource.fireEvent(EventType.BPMN_REDRAW, null);
            EventSource.fireEvent(EventType.VARIABLES_CHANGED, null);
        }
    }
}
