package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty;


import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields.CellProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 9-7-2015.
 */

public class CellProperties implements java.io.Serializable {
    List<CellProperty> properties;

    public CellProperties() {
        properties = new ArrayList<>();

    }

    public void addProperty(CellProperty cellProperty) {
        properties.add(cellProperty);
    }

    public CellProperty getCellProperty(String name) {
        for (CellProperty p : properties) {
            if (p.getName().equals(name))
                return p;
        }
        return null;
    }

    public String getId() {

        return this.getCellProperty("Id").getValue().toString();
    }

    public String getName() {
        return this.getCellProperty("Name").getValue().toString();
    }

    public String getRelationType() {
        return "TEST";
    }

    public void removeCustomProperties() {
        for (int i = 0; i < properties.size(); i++) {
            CellProperty p = properties.get(i);
            if (p.isCustomAttribute())
                properties.remove(i);
        }
    }

}