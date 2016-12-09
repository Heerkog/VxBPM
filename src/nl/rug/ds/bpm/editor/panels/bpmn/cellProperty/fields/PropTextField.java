package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields;


import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.CellPropertyPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by Mark on 9-7-2015.
 */
public class PropTextField implements IPropertyField {
    CellProperty cellProperty;
    private JLabel label;
    private JTextField textField;
    private IFormContainer parent;
    private GridBagConstraints cons;

    public PropTextField(CellProperty cellProperty, IFormContainer parent, ChangeListener changeListener) {
        this.parent = parent;
        this.cellProperty = cellProperty;
        cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        //cons.weightx = 1;
        //cons.gridx = 0;

        label = new JLabel(cellProperty.getLabel());
        label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, 10));
        textField = new JTextField();
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        CellPropertyPanel.addChangeListener(textField, changeListener);
        addToParent();
    }

    @Override
    public CellProperty getCellProperty() {
        return this.cellProperty;
    }

    @Override
    public void addToParent() {

        parent.addComponent(label);
        parent.addComponent(textField);
    }

    public void setVisible(Boolean visible) {
        label.setVisible(visible);
        textField.setVisible(visible);
    }

    public void setEditable(Boolean enabled) {

        textField.setEditable(enabled);
    }

    public void setValue(Object value) {
        if (value == null)
            textField.setText("");
        else
            textField.setText(value.toString());
    }

    public Object getValue() {
        return textField.getText();
    }
}
