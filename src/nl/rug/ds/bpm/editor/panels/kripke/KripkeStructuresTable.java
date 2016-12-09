package nl.rug.ds.bpm.editor.panels.kripke;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.KripkeStructure;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * Created by Mark Kloosterhuis.
 */
public class KripkeStructuresTable implements ActionListener {
    public KripkeVariableTableModel source;
    JTable table;
    JPopupMenu popupMenu;
    JMenuItem menuItemAdd;
    JScrollPane scrollPane;
    JPanel panel;

    public KripkeStructuresTable() {
        panel = new JPanel(new BorderLayout());

        /*
        source = new KripkeVariableTableModel();

        table = new JTable(source);
        table.setTableHeader(null);


        // constructs the popup menu
        popupMenu = new JPopupMenu();
        menuItemAdd = new JMenuItem("Delete");

        menuItemAdd.addActionListener(this);

        popupMenu.add(menuItemAdd);

        // sets the popup menu for the table
        table.setComponentPopupMenu(popupMenu);

        table.addMouseListener(new TableMouseListener(table));


        scrollPane = new JScrollPane(table);

        //panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        //scrollPane.setBorder(UIManager.getBorder("Button.border"));
        JButton addButton = new JButton("Add Kripke structure");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KripkeStructure structure = new KripkeStructure("Kripke" + getKripkeStructures().size());
                getKripkeStructures().put(structure.getId(),structure);
                source.fireTableDataChanged();
                EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, null);
            }
        });

        panel.add(addButton, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        EventSource.addListener(EventType.FILE_OPENED, e -> {
            source.fireTableDataChanged();
            EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, null);
        });*/


    }

    public HashMap<Integer, KripkeStructure> getKripkeStructures() {
        return AppCore.app.getKripkeStructures();
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemAdd) {

        }
    }


    private class KripkeVariableTableModel extends AbstractTableModel {

        private final String[] columnNames = new String[]{"Name"};

        private final Class[] columnClass = new Class[]{
                String.class, String.class, Double.class, Boolean.class
        };

        public KripkeVariableTableModel() {

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
            return getKripkeStructures().size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return getKripkeStructures().get(rowIndex).getId() != 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KripkeStructure row = getKripkeStructures().get(rowIndex);
            if (0 == columnIndex) {
                return row.getName();
            }
            return null;
        }

        public void RemoveAt(int row) {
            getKripkeStructures().remove(row);
            fireTableRowsDeleted(row, row);
            fireTableDataChanged();
            EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, null);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            KripkeStructure row = getKripkeStructures().get(rowIndex);
            //row.setName((String) aValue);
            EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, null);
        }
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
}
