package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields;


import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.CellPropertyPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Created by Mark on 9-7-2015.
 */
public class CheckField implements IPropertyField {
    CellProperty cellProperty;
    private JCheckBox textField;
    IFormContainer parent;

    public CheckField(CellProperty cellProperty, IFormContainer parent, ChangeListener changeListener) {
        this.cellProperty = cellProperty;
        this.parent = parent;
        textField = new JCheckBox(cellProperty.getLabel());
        CellPropertyPanel.addChangeListener(textField, changeListener);

    }

    @Override
    public CellProperty getCellProperty() {
        return this.cellProperty;
    }


    public void setVisible(Boolean visible) {
        textField.setVisible(visible);
    }

    public void setEditable(Boolean enabled) {
        textField.setEnabled(enabled);
    }

    @Override
    public void addToParent() {

        parent.addComponent(textField);
    }

    @Override
    public void setValue(Object value) {
        if (value == null)
            textField.setSelected(false);
        else
            textField.setSelected((Boolean) value);
    }

    @Override
    public Object getValue() {
        return textField.isSelected();
    }
}
