package nl.rug.ds.bpm.editor.panels.bpmn;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class VariableTable implements ActionListener {
    private JTable table;
    private JScrollPane scrollPane;
    private JPanel panel;
    private VariableTableModel source;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemAdd;

    public VariableTable() {
        source = new VariableTableModel();

        table = new JTable(source);
        table.setTableHeader(null);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);


        // constructs the popup menu
        popupMenu = new JPopupMenu();
        menuItemAdd = new JMenuItem("Delete");

        menuItemAdd.addActionListener(this);

        popupMenu.add(menuItemAdd);

        // sets the popup menu for the table
        table.setComponentPopupMenu(popupMenu);

        table.addMouseListener(new TableMouseListener(table));


        scrollPane = new JScrollPane(table);
        panel = new JPanel(new BorderLayout());

        JButton addButton = new JButton("Add variable");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = getVariables().stream().map(c -> c.getId())
                        .sorted((v1, v2) -> Integer.compare((int) v1, (int) v2)).sorted(Collections.reverseOrder()).findFirst().orElse(0) + 1;

                getVariables().add(new Variable(id, "Variable" + id));
                source.fireTableDataChanged();
                EventSource.fireEvent(EventType.VARIABLES_CHANGED, null);
            }
        });

        panel.add(addButton, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        EventSource.addListener(EventType.FILE_OPENED, e -> {
            source.fireTableDataChanged();
            EventSource.fireEvent(EventType.VARIABLES_CHANGED, null);
        });

    }

    public List<Variable> getVariables() {
        return AppCore.app.getVariables();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemAdd) {
            Variable selected = getVariables().get(table.getSelectedRow());
            if(!selected.isDefault())
                removeCurrentRow();


        }
    }

    private void removeCurrentRow() {
        int row = table.getSelectedRow();
        source.RemoveAt(row);


    }


    public JPanel getPanel() {
        return panel;
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

    class VariableTableModel extends AbstractTableModel {


        private final String[] columnNames = new String[]{"Name"};

        private final Class[] columnClass = new Class[]{
                String.class, String.class, Double.class, Boolean.class
        };

        public VariableTableModel() {

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
            return getVariables().size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            Variable row = getVariables().get(rowIndex);
            return !row.isDefault();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Variable row = getVariables().get(rowIndex);
            if (0 == columnIndex) {
                return row.getTableName();
            }
            return null;
        }

        public void RemoveAt(int row) {
            getVariables().remove(row);
            fireTableRowsDeleted(row, row);
            fireTableDataChanged();
            EventSource.fireEvent(EventType.BPMN_REDRAW, null);
            EventSource.fireEvent(EventType.VARIABLES_CHANGED, null);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Variable row = getVariables().get(rowIndex);
            row.setName((String) aValue);
            EventSource.fireEvent(EventType.BPMN_REDRAW, null);
            EventSource.fireEvent(EventType.VARIABLES_CHANGED, null);
        }
    }
}
