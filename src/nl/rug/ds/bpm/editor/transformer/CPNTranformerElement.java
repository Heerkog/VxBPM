package nl.rug.ds.bpm.editor.transformer;


import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import nl.rug.ds.bpm.verification.models.cpn.Variable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Kloosterhuis.
 */
public class CPNTranformerElement extends CPNTransformerNode implements java.io.Serializable {
    private String id;
    private List<CPNTransformerNode> nodes;
    private List<CPNTransformerNode> incomingElements;
    private List<CPNTransformerNode> outgoingElements;
    private String incomingMessageElementId;
    private String outgoingMessageElementId;
    private String eventElementId;
    private List<CPNTranformerElement> subElements;
    private List<Variable> variables;
    private TArc outgoingConditionArc = null;

    public CPNTranformerElement(Node elemNode) {
        super(XMLHelper.getNodeAttr("id", elemNode, ""), 0, 0, false, "");
        this.nodes = new ArrayList<>();
        this.incomingElements = new ArrayList<>();
        this.outgoingElements = new ArrayList<>();
        this.subElements = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.id = XMLHelper.getNodeAttr("id", elemNode, "");
        NodeList rootChildNodes = elemNode.getChildNodes();
        List<Node> placeNodes = XMLHelper.getChildElements(XMLHelper.getNode("places", rootChildNodes));
        this.incomingMessageElementId = XMLHelper.getNodeValue("incomingMessageElement", elemNode);
        this.outgoingMessageElementId = XMLHelper.getNodeValue("outgoingMessageElement", elemNode);
        this.eventElementId = XMLHelper.getNodeValue("eventElementId", elemNode);

        for (Node placeNode : placeNodes) {
            String id = XMLHelper.getNodeAttr("id", placeNode, "");
            float x = XMLHelper.getNodeFloatAttr("x", placeNode, 0);
            float y = XMLHelper.getNodeFloatAttr("y", placeNode, 0);
            boolean token = XMLHelper.getNodeAttr("token", placeNode, false);
            String name = XMLHelper.getNodeAttr("name", placeNode, "p{x}");
            this.nodes.add(new TPlace(id, x, y, token, name));
        }
        List<Node> transitionNodes = XMLHelper.getChildElements(XMLHelper.getNode("transitions", rootChildNodes));
        for (Node node : transitionNodes) {
            String id = XMLHelper.getNodeAttr("id", node, "");
            float x = XMLHelper.getNodeFloatAttr("x", node, 0);
            float y = XMLHelper.getNodeFloatAttr("y", node, 0);
            boolean token = XMLHelper.getNodeBooleanValue("token", node, false);
            String name = XMLHelper.getNodeAttr("name", node, "t{x}");

            this.nodes.add(new TTransition(id, x, y, token, name));
        }
        List<Node> arcNodes = XMLHelper.getChildElements(XMLHelper.getNode("arcs", rootChildNodes));
        for (Node node : arcNodes) {
            String id = XMLHelper.getNodeAttr("id", node, "");
            String from = XMLHelper.getNodeAttr("from", node, "");
            String to = XMLHelper.getNodeAttr("to", node, "");
            String condition = XMLHelper.getNodeAttr("condition", node, "");
            float x = XMLHelper.getNodeFloatAttr("x", node, 0);
            float y = XMLHelper.getNodeFloatAttr("y", node, 0);
            String weight = XMLHelper.getNodeAttr("weight", node, "1");
            boolean hasOutgoingCondition = XMLHelper.getNodeAttr("hasOutgoingCondition", node, false);
            TArc arc = new TArc(id, from, to, condition, weight, x, y, hasOutgoingCondition);
            if (hasOutgoingCondition)
                outgoingConditionArc = arc;
            this.nodes.add(arc);
        }

        if (XMLHelper.getNode("cpnElements", rootChildNodes) != null) {
            List<Node> subElements = XMLHelper.getChildElements(XMLHelper.getNode("cpnElements", rootChildNodes));
            for (Node node : subElements) {
                this.nodes.add(new CPNTranformerElement(node));
            }
        }


        Node incomingElementsContainer = XMLHelper.getNode("incomingElements", rootChildNodes);
        List<String> incomingElementIds = new ArrayList<>();
        List<String> outgoingElementIds = new ArrayList<>();
        if (incomingElementsContainer != null) {
            incomingElementIds = XMLHelper.getChildElements(incomingElementsContainer)
                    .stream()
                    .map(p -> XMLHelper.getNodeValue(p))
                    .collect(Collectors.toList());
        }

        Node outgoingElementsContainer = XMLHelper.getNode("outgoingElements", rootChildNodes);
        if (incomingElementsContainer != null) {
            outgoingElementIds = XMLHelper.getChildElements(outgoingElementsContainer)
                    .stream()
                    .map(p -> XMLHelper.getNodeValue(p))
                    .collect(Collectors.toList());
        }


        incomingElementIds.forEach(p -> {
            CPNTransformerNode tNode = this.nodes.stream().filter(e -> e.getId().equals(p)).findFirst().orElse(null);
            if (tNode == null)
                Console.error(String.format("Incoming node %s not found in %s", p, this.id));
            else
                incomingElements.add(tNode);
        });

        outgoingElementIds.forEach(p -> {
            CPNTransformerNode tNode = this.nodes.stream().filter(e -> e.getId().equals(p)).findFirst().orElse(null);
            if (tNode == null)
                Console.error(String.format("Outgoing node (%s) not found in (%s)", p, this.id));
            else
                outgoingElements.add(tNode);
        });
        parseVariables(elemNode);
    }

    public List<Variable> getVariables() {
        return variables;
    }

    private void parseVariables(Node elemNode) {
        NodeList rootChildNodes = elemNode.getChildNodes();
        List<Node> variableNodes = XMLHelper.getChildElements(XMLHelper.getNode("variables", rootChildNodes));
        for (Node variableNode : variableNodes) {
            String name = XMLHelper.getNodeAttr("name", variableNode, "");

            List<String> values = new ArrayList<>();
            values = XMLHelper.getChildElements(variableNode)
                    .stream()
                    .map(p -> XMLHelper.getNodeValue(p))
                    .collect(Collectors.toList());


            int id = AppCore.app.getVariables().stream().map(c -> c.getId())
                    .sorted((v1, v2) -> Integer.compare((int) v1, (int) v2)).sorted(Collections.reverseOrder()).findFirst().orElse(0) + 1;
            Variable variable = new Variable(id, name);
            variable.setDefaultCpnName(this.getId());
            for (String value : values) {
                variable.addDefaultValue(value);
            }


            AppCore.app.getVariables().add(variable);
            this.variables.add(variable);


        }
    }


    public String getId() {
        return this.id;
    }

    public CPNTransformerNode getById(String id) {
        for (CPNTransformerNode node : nodes) {
            if (node.getId().equals(id))
                return node;
        }
        return null;
    }

    public List<CPNTransformerNode> getNodes() {
        return nodes;
    }

    public List<CPNTransformerNode> getIncomingElements() {
        return incomingElements;
    }

    public List<CPNTransformerNode> getOutgoingElements() {
        return outgoingElements;
    }


    public TArc getOutgoingConditionArc() {
        return outgoingConditionArc;
    }

    public CPNTransformerNode getIncomingMessageElement() {
        return nodes.stream().filter(n -> n.getId().equals(incomingMessageElementId)).findFirst().orElse(null);
    }

    public CPNTransformerNode getOutgoingMessageElement() {

        return nodes.stream().filter(n -> n.getId().equals(outgoingMessageElementId)).findFirst().orElse(null);
    }

    public CPNTransformerNode getEventElement() {
        return nodes.stream().filter(n -> n.getId().equals(eventElementId)).findFirst().orElse(null);
    }
}
