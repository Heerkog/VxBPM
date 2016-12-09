package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields;

/**
 * Created by Mark Kloosterhuis.
 */
public interface IPropertyField {
    void addToParent();

    void setVisible(Boolean visible);

    void setEditable(Boolean enabled);

    void setValue(Object value);

    Object getValue();

    CellProperty getCellProperty();
}
