package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import nl.rug.ds.bpm.editor.transformer.CPNTranformerElement;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 29-6-2015.
 */
public class InputElement implements java.io.Serializable {
    private String id;
    private String cpnElementName;
    private int width = 50;
    private int height = 50;
    private Boolean resizable;
    private Boolean isGroupable = false;
    private Boolean constraints = true;
    private int maxIncoming;
    private int maxOutgoing;
    private int minIncoming;
    private int minOutgoing;
    private String paletIconPath;
    private String shapePath;
    private String name;
    private Boolean isNameVisible = true;
    private Boolean isNameEditable = true;
    private String genId;
    private Boolean isGenIdVisible = true;
    private CPNTranformerElement cpnTranformerElement;
    private boolean isEdge = false;
    private Boolean isInclusiveElement = false;
    private List<InputElementAttribute> attributes;
    private HashMap<String, String> customStyleProperties;
    private PaletElement paletElement;
    private String eventInputElementId;
    private String bpmnElementName;


    public InputElement(Node elemNode) {
        customStyleProperties = new HashMap<>();
        this.id = XMLHelper.getNodeAttr("id", elemNode, "");

        name = XMLHelper.getNodeValue("name", elemNode, null);
        cpnElementName = XMLHelper.getNodeValue("CPNElement", elemNode);
        width = XMLHelper.getNodeValue("width", elemNode, width);
        height = XMLHelper.getNodeValue("height", elemNode, height);
        resizable = XMLHelper.getNodeBooleanValue("resizable", elemNode, false);
        isGroupable = XMLHelper.getNodeBooleanValue("groupable", elemNode, false);
        constraints = XMLHelper.getNodeBooleanValue("constraints", elemNode, true);
        maxIncoming = XMLHelper.getNodeAttr("maxIncoming", "connections", elemNode, 99);
        maxOutgoing = XMLHelper.getNodeAttr("maxOutgoing", "connections", elemNode, 99);
        minIncoming = XMLHelper.getNodeAttr("minIncoming", "connections", elemNode, 99);
        minOutgoing = XMLHelper.getNodeAttr("minOutgoing", "connections", elemNode, 99);
        paletIconPath = XMLHelper.getNodeValue("paletIconPath", elemNode, null);
        shapePath = XMLHelper.getNodeValue("shapePath", elemNode, null);

        isNameVisible = XMLHelper.getNodeAttr("visible", "name", elemNode, false);
        isNameEditable = XMLHelper.getNodeAttr("editable", "name", elemNode, false);
        setGenId(XMLHelper.getNodeValue("genId", elemNode, null));
        isGenIdVisible = XMLHelper.getNodeAttr("visible", "name", elemNode, true);
        isInclusiveElement = XMLHelper.getNodeBooleanValue("isInclusiveElement", elemNode, false);
        isEdge = XMLHelper.getNodeBooleanValue("isEdge", elemNode, false);
        eventInputElementId = XMLHelper.getNodeValue("eventInputElementId", elemNode, null);
        bpmnElementName = XMLHelper.getNodeValue("BPMNName", elemNode, "");

        List<Node> stylesNodes = XMLHelper.getChildElements(XMLHelper.getNode("styleProperties", elemNode.getChildNodes()));
        for (Node node : stylesNodes) {
            String key = XMLHelper.getNodeAttr("as", node, "");
            String value = XMLHelper.getNodeAttr("value", node, "");
            if (key != null) {
                customStyleProperties.put(key, value);
            }
        }
        attributes = new ArrayList<>();
        List<Node> attributeNodes = XMLHelper.getChildElements(XMLHelper.getNode("attributes", elemNode.getChildNodes()));
        for (Node node : attributeNodes) {
            String label = XMLHelper.getNodeAttr("label", node, "");
            String name = XMLHelper.getNodeValue(node);
            attributes.add(new InputElementAttribute(label, name));
        }


    }

    public boolean hasTranformationElements() {
        return !(cpnTranformerElement == null || cpnTranformerElement.getNodes().size() == 0);
    }

    public String getId() {
        return id;
    }

    public String getCpnElementName() {
        return cpnElementName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Boolean getResizable() {
        return resizable;
    }


    public int getMaxIncoming() {
        return maxIncoming;
    }


    public int getMaxOutgoing() {
        return maxOutgoing;
    }


    public int getMinIncoming() {
        return minIncoming;
    }


    public int getMinOutgoing() {
        return minOutgoing;
    }


    public String getPaletIconPath() {
        return paletIconPath;
    }

    public boolean isEdge() {
        return isEdge;
    }

    public String getShapePath() {
        return shapePath;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenId() {
        if (genId == null || genId.isEmpty())
            return "n{x}";
        return genId;
    }

    public void setGenId(String genId) {
        this.genId = genId;
    }

    public Boolean isNameVisible() {
        return isNameVisible;
    }

    public Boolean isNameEditable() {
        return isNameEditable;
    }

    public Boolean isGenIdVisible() {
        return isGenIdVisible;
    }

    public HashMap<String, String> getCustomStyleProperties() {
        return customStyleProperties;
    }

    public CPNTranformerElement getCpnTranformerElement() {
        return cpnTranformerElement;
    }

    public void setCpnTranformerElement(CPNTranformerElement cpnTranformerElement) {
        this.cpnTranformerElement = cpnTranformerElement;
    }

    public Boolean IsGroup() {
        return isGroupable;
    }

    public Boolean canHaveConstraints() {
        return constraints;
    }

    public List<InputElementAttribute> getAttributes() {
        return attributes;
    }

    public Boolean isInclusiveElement() {
        return isInclusiveElement;
    }

    public PaletElement getPaletElement() {
        return paletElement;
    }

    public void setPaletElement(PaletElement paletElement) {
        this.paletElement = paletElement;
    }

    public String getEventInputElementId() {
        return eventInputElementId;
    }

    public String getBpmnElementName() {
        return bpmnElementName;
    }
}
