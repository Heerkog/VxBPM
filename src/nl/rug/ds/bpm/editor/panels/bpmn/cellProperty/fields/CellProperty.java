package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields;

import nl.rug.ds.bpm.editor.core.enums.PropertyFieldType;

/**
 * Created by Mark Kloosterhuis.
 */
public class CellProperty implements java.io.Serializable {
    PropertyFieldType type;
    String name;
    String label;
    Object value;


    boolean isCustomAttribute = false;

    public CellProperty(PropertyFieldType type, String name, String label, Object value) {
        this.type = type;
        this.name = name;
        this.label = label;
        this.value = value;
    }

    public CellProperty(PropertyFieldType type, String name, String label, Object value, boolean isCustomAttribute) {
        this(type, name, label, value);
        this.isCustomAttribute = isCustomAttribute;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return this.value;
    }

    public PropertyFieldType getFieldType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isCustomAttribute() {
        return isCustomAttribute;
    }
}
