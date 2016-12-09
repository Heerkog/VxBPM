package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty;

/**
 * Created by Mark Kloosterhuis.
 */


import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.Constraint;
import nl.rug.ds.bpm.editor.models.ModelChecker;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;

import javax.swing.*;
import java.awt.*;

public class ConstrainSelector {
    protected JList m_list;
    private JScrollPane panel;
    private DefaultListModel listModel;
    private SuperCell selectedCell;


    private class CheckBoxListItem {
        private boolean isHeader;
        private String label;
        private Constraint constrain;

        public CheckBoxListItem(Boolean isHeader, String label, Constraint constrain) {
            this.isHeader = isHeader;
            this.label = label;
            this.constrain = constrain;
        }

        public Boolean isHeader() {
            return isHeader;
        }

        public String getLabel() {
            return this.label;
        }

        public Constraint getConstrain() {
            return constrain;
        }
    }


    public ConstrainSelector() {
        listModel = new DefaultListModel();

        m_list = new JList(listModel);
        m_list.setCellRenderer(new VersionCellRenderer());
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                CheckBoxListItem item = (CheckBoxListItem) listModel.get(index0);
                if (item.isHeader)
                    return;
                super.setSelectionInterval(index0, index1);
                selectedCell.setConstraint(item.getConstrain());
                EventSource.fireEvent(EventType.CONSTRAINT_SELECT_CHANGE, "");
            }
        };

        m_list.setSelectionModel(selectionModel);


        panel = new JScrollPane();
        panel.getViewport().add(m_list);
        panel.setPreferredSize(new Dimension(640, 480));
    }

    private int selectedindex = -1;
    private int index = 0;

    public void setItems(SuperCell cell) {
        this.selectedCell = cell;


        listModel.removeAllElements();
        selectedindex = -0;
        index = 0;
        ModelChecker modelChecker = AppCore.app.selectedModelChecker();
        modelChecker.getSpecificationLanguages().forEach(specificationLanguages -> {
            if (specificationLanguages == null || specificationLanguages.getConstrains().size() == 0) return;
            listModel.addElement(new CheckBoxListItem(true, specificationLanguages.getName(), null));
            index++;
            java.util.List<Constraint> constraints = specificationLanguages.getConstrains();
            if (cell instanceof InputCell)
                constraints = specificationLanguages.getObjectConstrains();


            constraints.forEach(constrain -> {
                if (constrain.getId().equals(selectedCell.getConstraint().getId()) || selectedindex == -1) {
                    selectedindex = index;
                }
                listModel.addElement(new CheckBoxListItem(false, constrain.getArrow().getName(), constrain));
                index++;
            });

        });
        m_list.setSelectedIndex(selectedindex);

    }

    public JScrollPane getPanel() {
        return this.panel;
    }

    private static class VersionCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            //JPanel jpCell = new JPanel();
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Font font = new Font(list.getFont().getName(), list.getFont().getStyle(), 10);
            CheckBoxListItem item = (CheckBoxListItem) value;
            if (item.isHeader()) {
                //jpCell.setLayout(new GridLayout(0, 1));
                //jpCell.add(label);
                label.setText(item.getLabel());
                label.setFont(font.deriveFont(Font.BOLD));
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                label.setEnabled(false);
            } else {

                //jpCell.setLayout(new GridLayout(0, 1));
                //jpCell.add(label);
                label.setText(item.getLabel());
                label.setFont(font);
                //checkBox.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
            return label;
            //return jpCell;
        }
    }

}

