package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 29-6-2015.
 */
public class PaletElement implements java.io.Serializable {
    private String id;
    private String paletIconPath;
    private String name;
    private List<InputElement> inputElements;


    public PaletElement(Node elemNode, HashMap<String, InputElement> allInputElements) {
        this.id = XMLHelper.getNodeAttr("id", elemNode, "");
        name = XMLHelper.getNodeValue("name", elemNode, null);
        paletIconPath = XMLHelper.getNodeValue("paletIconPath", elemNode, null);
        inputElements = new ArrayList<>();

        List<Node> inputNodes = XMLHelper.getChildElements(XMLHelper.getNode("inputElements", elemNode.getChildNodes()));
        for (Node node : inputNodes) {
            String inputElementId = XMLHelper.getNodeValue(node);
            if (allInputElements.containsKey(inputElementId)) {
                InputElement inputElement = allInputElements.get(inputElementId);
                inputElement.setPaletElement(this);
                inputElements.add(inputElement);
            } else {
                Console.error(inputElementId + " Not found");
            }
        }
    }

    public String getPaletIconPath() {
        return paletIconPath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<InputElement> getInputElements() {
        return inputElements;
    }
}
