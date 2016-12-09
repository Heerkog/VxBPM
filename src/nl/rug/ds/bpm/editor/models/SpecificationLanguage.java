package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import nl.rug.ds.bpm.editor.core.enums.ConstraintType;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class SpecificationLanguage implements java.io.Serializable {
    String id;
    String name;
    List<Constraint> constrains;
    List<Constraint> objectConstrains;

    public SpecificationLanguage(Node elemNode, HashMap<String, Arrow> arrows) {
        constrains = new ArrayList<>();
        objectConstrains = new ArrayList<>();

        id = XMLHelper.getNodeValue("id", elemNode);
        name = XMLHelper.getNodeValue("name", elemNode);
        for (Node node : XMLHelper.getChildElements(elemNode, "contrains")) {
            constrains.add(new Constraint(node, arrows, this, ConstraintType.Edge));
        }
        for (Node node : XMLHelper.getChildElements(elemNode, "objectConstraints")) {
            objectConstrains.add(new Constraint(node, arrows, this, ConstraintType.Object));
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public List<Constraint> getConstrains() {
        return constrains;
    }

    public List<Constraint> getObjectConstrains() {
        return objectConstrains;
    }


}
