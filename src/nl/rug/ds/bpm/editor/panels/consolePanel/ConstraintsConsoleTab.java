package nl.rug.ds.bpm.editor.panels.consolePanel;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.ConstraintResult;
import nl.rug.ds.bpm.editor.models.KripkeStructure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Mark Kloosterhuis.
 */
public class ConstraintsConsoleTab extends AbstractKripkeConsoleTab<ConstraintsConsoleTab.StructureConsoleTab> {
    public ConstraintsConsoleTab() {
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
        ConstraintTableModel source;
        int structureId;

        public StructureConsoleTab(int structureId) {
            panel = new JPanel();
            panel.setLayout(new GridLayout(0, 1));
            panel.setBorder(new EmptyBorder(0, 0, 0, 0));

            this.structureId = structureId;
            source = new ConstraintTableModel(new ArrayList<>());
            tabbedPanel.addChangeListener(e -> {
                EventSource.fireEvent(EventType.KRIPKE_CONSOLE_TABVIEW_CHANGED, tabbedPanel.getSelectedIndex());
            });

            table = new JTable(source) {

                DefaultTableCellRenderer colortext = new DefaultTableCellRenderer();

                {
                    Font font = colortext.getFont();
                    Map attributes = (new Font("Serif", Font.PLAIN, 12)).getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    //colortext.setForeground(Color.RED);
                    colortext.setFont(new Font(attributes));
                }

                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component comp = super.prepareRenderer(renderer, row, column);
                    ConstraintResult result = source.constraints.get(row);
                    Map attributes = comp.getFont().getAttributes();
                    comp.setForeground(Color.BLACK);

                    if (column == 3 && result.hasStatus(ConstraintStatus.Unavailable)) {
                        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    } else if (column == 4) {
                        comp.setForeground(result.getStatus().getColor());
                        /*if (result.hasStatus(ConstraintStatus.Unavailable))
                            comp.setForeground(Color.GRAY);
                        else if (result.hasStatus(ConstraintStatus.Valid)) {
                            comp.setForeground(Color.GREEN);
                        } else if (result.hasStatus(ConstraintStatus.Invalid)) {
                            comp.setForeground(Color.RED);
                        }*/
                    }
                    comp.setFont(new Font(attributes));
                    return comp;
                }
            };
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (table.getSelectedRow() > -1) {
                        ConstraintResult row = source.constraints.get(table.getSelectedRow());
                        row.select();
                    }
                }
            });
            table.setBackground(Color.WHITE);
            scrollPanel = new JScrollPane(table);
            panel.add(scrollPanel);

            EventSource.addListener(EventType.KRIPKE_CONSTRAINT_CHANGE, (e) -> {
                if ((int) e == this.structureId) {
                    getKripkeModelString();
                }
            });
        }

        private void getKripkeModelString() {
            KripkeStructure structure = AppCore.app.getKripkeStructures().get(this.structureId);
            source.constraints = structure.getConstraintResults();
            source.fireTableDataChanged();


        }

        public JPanel getPanel() {
            return panel;
        }
    }


    public class ConstraintTableModel extends AbstractTableModel {
        public java.util.List<ConstraintResult> constraints;

        private final String[] columnNames = new String[]{"Edge/Element Id", "XPDLConstraint name", "Type", "Formula", "Status"};

        private final Class[] columnClass = new Class[]{String.class, String.class, String.class, String.class, String.class};

        public ConstraintTableModel(java.util.List<ConstraintResult> constraints) {

            this.constraints = constraints;
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
            return constraints.size();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ConstraintResult row = constraints.get(rowIndex);
            if (0 == columnIndex) {
                // return row.edge.getId();
                return row.getNodeId();
            } else if (1 == columnIndex) {
                return row.formula.getArrow().getName();
            } else if (2 == columnIndex) {
                return row.formula.getTypeName();
            } else if (3 == columnIndex) {
                return row.getConverterInput();
            } else if (4 == columnIndex) {
                return row.getStatus().getText();
                /*if (row.hasElementsError())
                    return "Could not test";
                else if (row.IsSuccess()) {
                    return "PASSED";
                } else if (row.IsFailed()) {
                    return "FAILED";
                } else {
                    return "";
                }*/
            }
            return null;
        }

    }
}
