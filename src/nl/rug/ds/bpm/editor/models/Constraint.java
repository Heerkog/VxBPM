package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import nl.rug.ds.bpm.editor.core.enums.ConstraintType;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class Constraint implements java.io.Serializable {
    private SpecificationLanguage specificationLanguage;
    private Arrow arrow;
    private List<String> formulas;
    private String id;
    private ConstraintType constraintType;

    public Constraint(Node elemNode, HashMap<String, Arrow> arrows, SpecificationLanguage specificationLanguage, ConstraintType constraintType) {
        this.specificationLanguage = specificationLanguage;
        this.constraintType = constraintType;
        formulas = new ArrayList<>();
        id = XMLHelper.getNodeAttr("id", elemNode, "");
        if (AppCore.app.getConstraints().containsKey(id)) {
            System.out.println("Duplicate constraintId! :" + id);
            Console.error("Duplicate constraintId! :" + id);
        }
        AppCore.app.getConstraints().put(id, this);


        String arrowId = XMLHelper.getNodeValue("arrowId", elemNode);
        if (!arrows.containsKey(arrowId))
            Console.error("Arrow " + arrowId + " not found");
        arrow = arrows.get(arrowId);
        for (Node node : XMLHelper.getChildElements(elemNode, "formulas")) {
            formulas.add(XMLHelper.getNodeValue(node));
        }
    }
    public String getId() {
        return id;
    }
    public List<String> getFormulas() {
        return formulas;
    }

    public Arrow getArrow() {
        if (arrow == null) {
            Console.error("arrow missing:" + formulas.toString());
        }
        return arrow;
    }
    public ConstraintType getConstraintType() {
        return constraintType;
    }
    public SpecificationLanguage getSpecificationLanguage() {
        return specificationLanguage;
    }
}
