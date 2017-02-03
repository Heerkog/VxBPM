package nl.rug.ds.bpm.editor.core.configloader;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 29-6-2015.
 */
public class XMLHelper {
    public static Node getNode(String tagName, NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }

        return null;
    }

    public static String getNodeValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++) {
            Node data = childNodes.item(x);
            if (data.getNodeType() == Node.TEXT_NODE)
                return data.getNodeValue();
        }
        return "";
    }

    public static String getNodeValue(String tagName, NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.TEXT_NODE)
                        return data.getNodeValue();
                }
            }
        }
        return "";
    }

    public static Boolean getNodeBooleanValue(String tagName, Node node, Boolean def) {
        String value = getNodeValue(tagName, node, "");
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.valueOf(value);
        } else {
            return def;
        }
    }

    public static int getNodeValue(String tagName, Node node, int def) {
        String value = getNodeValue(tagName, node, null);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();

            }
        }
        return def;
    }

    public static String getNodeValue(String tagName, Node node) {
        return getNodeValue(tagName, node, null);
    }

    public static String getNodeValue(String tagName, Node node, String defaultStr) {
        Element eElement = (Element) node;
        NodeList nodeList = eElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return defaultStr;
    }

    public static String getNodeAttr(String attrName, String tagName, Node node, String defaultStr) {
        Element eElement = (Element) node;
        NodeList nodeList = eElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return getNodeAttr(attrName, nodeList.item(0), defaultStr);
        }
        return defaultStr;
    }

    public static Boolean getNodeAttr(String attrName, String tagName, Node node, Boolean defaultStr) {
        Element eElement = (Element) node;
        NodeList nodeList = eElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return getNodeAttr(attrName, nodeList.item(0), defaultStr);
        }
        return defaultStr;
    }

    public static int getNodeAttr(String attrName, String tagName, Node node, int defaultInt) {
        Element eElement = (Element) node;
        NodeList nodeList = eElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return getNodeIntAttr(attrName, nodeList.item(0), defaultInt);
        }
        return defaultInt;
    }

    public static Boolean getNodeAttr(String attrName, Node node, Boolean defaultStr) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                String value = attr.getNodeValue();
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return Boolean.valueOf(value);
                } else {
                    return defaultStr;
                }
            }
        }
        return defaultStr;
    }

    public static String getNodeAttr(String attrName, Node node, String defaultStr) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return defaultStr;
    }

    public static Integer getNodeIntAttr(String attrName, Node node, Integer defaultInt) {
        String strValue = getNodeAttr(attrName, node, "");
        if (strValue != "")
            return Integer.valueOf(strValue);

        return defaultInt;
    }

    public static float getNodeFloatAttr(String attrName, Node node, float defaultInt) {
        String strValue = getNodeAttr(attrName, node, "");
        if (strValue != "")
            return Float.valueOf(strValue);

        return defaultInt;
    }

    public static String getNodeAttr(String tagName, String attrName, NodeList nodes) {
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.ATTRIBUTE_NODE) {
                        if (data.getNodeName().equalsIgnoreCase(attrName))
                            return data.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public static Document LoadXMLFromFile(File xmlFile) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document LoadXMLFromFile(String pathname) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(pathname);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document LoadXMLFromFile(InputStream inputStream) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Node getRootNode(Document document) {
        if (document.hasChildNodes()) {
            NodeList nodeList = document.getChildNodes();
            for (int count = 0; count < document.getChildNodes().getLength(); count++) {
                Node elemNode = nodeList.item(count);
                if (elemNode.getNodeType() == Node.ELEMENT_NODE) {
                    return elemNode;
                }
            }
        }
        return null;
    }

    public static List<Node> getChildElements(Node parent, String parentName) {
        return XMLHelper.getChildElements(XMLHelper.getNode(parentName, parent.getChildNodes()));
    }

    public static List<Node> getChildElements(Node parent) {
        List<Node> elements = new ArrayList<>();
        if (parent != null) {
            NodeList nodeList = parent.getChildNodes();
            for (int count = 0; count < nodeList.getLength(); count++) {
                Node elemNode = nodeList.item(count);
                if (elemNode.getNodeType() == Node.ELEMENT_NODE) {
                    elements.add(elemNode);

                }
            }
        }
        return elements;
    }
}
