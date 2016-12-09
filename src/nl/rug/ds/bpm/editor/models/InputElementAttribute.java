package nl.rug.ds.bpm.editor.models;

/**
 * Created by Mark on 29-6-2015.
 */
public class InputElementAttribute implements java.io.Serializable {
    private String label;


    private String name;

    public InputElementAttribute(String label, String name) {
        this.label = label;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label + "(" + this.name + ")";
    }
}
