package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mark on 10-9-2015.
 */
public class Arrow implements java.io.Serializable {
    String id;
    String name;
    Boolean dashed;
    List<ConstrainShape> sourceShapes;
    List<ConstrainShape> centerShapes;
    List<ConstrainShape> targetShapes;

    public Arrow(Node elemNode) {
        sourceShapes = new ArrayList<>();
        centerShapes = new ArrayList<>();
        targetShapes = new ArrayList<>();
        id = XMLHelper.getNodeAttr("id", elemNode, "");
        name = XMLHelper.getNodeAttr("name", elemNode, "");
        dashed = XMLHelper.getNodeAttr("dashed", elemNode, false);
        sourceShapes = parseShapes(XMLHelper.getChildElements(elemNode, "sourceShapes"));
        centerShapes = parseShapes(XMLHelper.getChildElements(elemNode, "centerShapes"));
        targetShapes = parseShapes(XMLHelper.getChildElements(elemNode, "targetShapes"));
        Collections.reverse(targetShapes);
    }

    private List<ConstrainShape> parseShapes(List<Node> xmlShapes) {
        List<ConstrainShape> shapes = new ArrayList<>();
        for (Node node : xmlShapes) {
            shapes.add(ConstrainShape.get(XMLHelper.getNodeValue(node)));
        }
        return shapes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ConstrainShape> getSourceShapes() {
        return sourceShapes;
    }

    public List<ConstrainShape> getCenterShapes() {
        return centerShapes;
    }

    public List<ConstrainShape> getTargetShapes() {
        return targetShapes;
    }

    public boolean isDashed() {
        return dashed;

    }
}
