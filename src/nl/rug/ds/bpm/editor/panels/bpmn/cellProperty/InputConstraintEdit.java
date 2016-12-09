package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.Constraint;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.editor.models.ModelChecker;
import nl.rug.ds.bpm.editor.models.SpecificationLanguage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

/**
 * Created by Mark Kloosterhuis.
 */
public class InputConstraintEdit {
    JPanel panel;
    CellPropertyPanel parent;
    InputCellConstraint inputCellConstraint;
    ConditionTable table;
    HashMap<String, Integer> constraintIndex;
    JComboBox combo;

    public InputConstraintEdit(CellPropertyPanel parent) {
        this.parent = parent;
        constraintIndex = new HashMap<>();

        panel = new JPanel();
        panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(190, 100));

        DefaultComboBoxModel listModel = new DefaultComboBoxModel() {
            @Override
            public void setSelectedItem(Object item) {
                CheckBoxListItem checkitem = (CheckBoxListItem) item;
                if (checkitem.isHeader)
                    return;
                super.setSelectedItem(item);
                if (inputCellConstraint != null) {
                    inputCellConstraint.setConstraint(checkitem.getConstrain());
                    EventSource.fireEvent(EventType.CONSTRAINT_SELECT_CHANGE, "");
                }
            }
        };
        combo = new JComboBox();
        EventSource.addListener(EventType.LOADED, e -> {
            ModelChecker modelChecker = AppCore.app.selectedModelChecker();
            AppCore.app.getSpecificationLanguages().forEach(specificationLanguages -> {
                if (specificationLanguages == null) return;
                listModel.addElement(new CheckBoxListItem(true, specificationLanguages, specificationLanguages.getName(), null));

                java.util.List<Constraint> constraints = specificationLanguages.getObjectConstrains();
                constraints.forEach(constraint -> {
                    listModel.addElement(new CheckBoxListItem(false, specificationLanguages, constraint.getArrow().getName(), constraint));
                    constraintIndex.put(constraint.getId(), listModel.getSize() - 1);
                });
            });
            combo.setSelectedIndex(1);
        });


        combo.setModel(listModel);

        ComboBoxRenderer renderer = new ComboBoxRenderer();
        combo.setRenderer(renderer);
        combo.getEditor().getEditorComponent().setBackground(Color.YELLOW);
        combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                //Do Something
            }
        });
        panel.add(combo, BorderLayout.NORTH);

        table = new ConditionTable(null);
        panel.add(table.getPanel(), BorderLayout.CENTER);
    }

    public void setInputCell(InputCellConstraint inputCellConstraint) {
        this.inputCellConstraint = inputCellConstraint;
        table.setVariables(this.inputCellConstraint.getVariablesValues());
        table.setCellConstraint(inputCellConstraint);

        combo.setSelectedIndex(constraintIndex.get(inputCellConstraint.getConstraint().getId()));


    }

    public JPanel getPanel() {
        return panel;
    }

    public void addToParent() {
        parent.addComponent(new JLabel("Edit Condition"));
        parent.addComponent(panel);
    }

    private class CheckBoxListItem {
        private boolean isHeader;
        private SpecificationLanguage specLang;
        private String label;
        private Constraint constrain;

        public CheckBoxListItem(boolean isHeader, SpecificationLanguage specLang, String label, Constraint constrain) {
            this.isHeader = isHeader;
            this.specLang = specLang;
            this.label = label;
            this.constrain = constrain;
        }

        public SpecificationLanguage getSpecLang() {
            return specLang;
        }

        public String getLabel() {
            return this.label;
        }

        public Constraint getConstrain() {
            return constrain;
        }
    }


    class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            CheckBoxListItem item = (CheckBoxListItem) value;
            setEnabled(!item.isHeader);

            setText(item.getLabel());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }

    }
}
