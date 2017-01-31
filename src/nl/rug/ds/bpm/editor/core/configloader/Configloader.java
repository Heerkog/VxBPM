package nl.rug.ds.bpm.editor.core.configloader;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.Main;
import nl.rug.ds.bpm.editor.models.*;
import nl.rug.ds.bpm.editor.transformer.CPNTranformerElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 29-6-2015.
 */
public class Configloader {
    private HashMap<String, InputElement> inputElements;
    private HashMap<String, CPNTranformerElement> cpnTranformerElements;
    private HashMap<String, Arrow> arrows;
    private HashMap<String, SpecificationLanguage> specificationLanguages;
    private List<ModelChecker> modelCheckers;
    private List<PaletElement> paletElements;

    public Configloader() {
        loadCPNElements();
        loadInputElement();
        loadPaletElements();
        loadArrows();
        loadSpecificationLanguages();
        loadModelCheckers();
    }

    private void loadInputElement() {
        inputElements = new HashMap<>();
        Document document = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/input-elements.xml"));
        if (document != null) {
            Node rootNode = XMLHelper.getRootNode(document);
            for (Node node : XMLHelper.getChildElements(rootNode)) {
                InputElement inputElement = new InputElement(node);

                if (cpnTranformerElements.containsKey(inputElement.getCpnElementName())) {
                    inputElement.setCpnTranformerElement(cpnTranformerElements.get(inputElement.getCpnElementName()));
                } else
                    Console.log("Transformer element " + inputElement.getCpnElementName() + " not found");

                inputElements.put(inputElement.getId(), inputElement);
            }
        }
    }

    private void loadPaletElements() {
        paletElements = new ArrayList<>();
        Document document = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/palet-elements.xml"));
        if (document != null) {
            Node rootNode = XMLHelper.getRootNode(document);
            for (Node node : XMLHelper.getChildElements(rootNode)) {
                PaletElement paletElement = new PaletElement(node, inputElements);
                paletElements.add(paletElement);
            }
        }
    }

    private void loadCPNElements() {
        cpnTranformerElements = new HashMap<>();
        Document document = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/cpn-elements.xml"));
        if (document != null) {
            Node rootNode = XMLHelper.getRootNode(document);
            for (Node node : XMLHelper.getChildElements(rootNode)) {
                Document doc = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/CPNElements/" + XMLHelper.getNodeValue(node) + ".xml"));
                Node r = XMLHelper.getRootNode(doc);
                CPNTranformerElement element = new CPNTranformerElement(r);
                cpnTranformerElements.put(element.getId(), element);
            }
        }
    }

    private void loadArrows() {
        arrows = new HashMap<>();
        Document document = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/arrows.xml"));
        if (document != null) {
            Node rootNode = XMLHelper.getRootNode(document);
            for (Node node : XMLHelper.getChildElements(rootNode)) {
                Arrow arrow = new Arrow(node);
                arrows.put(arrow.getId(), arrow);
            }
        }
    }

    private void loadSpecificationLanguages() {
        specificationLanguages = new HashMap<>();
        Document document = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/specification-languages.xml"));
        if (document != null) {
            Node rootNode = XMLHelper.getRootNode(document);
            for (Node node : XMLHelper.getChildElements(rootNode)) {
                SpecificationLanguage specificationLanguage = new SpecificationLanguage(node, arrows);
                specificationLanguages.put(specificationLanguage.getId(), specificationLanguage);
            }
        }
    }

    private void loadModelCheckers() {
        modelCheckers = new ArrayList<>();
        Document document = XMLHelper.LoadXMLFromFile(Main.class.getResourceAsStream("/resources/model-checkers.xml"));
        if (document != null) {
            Node rootNode = XMLHelper.getRootNode(document);
            for (Node node : XMLHelper.getChildElements(rootNode)) {
                if(((Element)node).getAttribute("enabled").equals("true"))
                {
                    ModelChecker modelChecker = new ModelChecker(node, specificationLanguages);
                    modelCheckers.add(modelChecker);
                }
            }
        }
    }

    public List<Arrow> getArrows() {
        return new ArrayList<Arrow>(arrows.values());
    }

    public List<InputElement> getInputElements() {
        return new ArrayList<>(inputElements.values());
    }

    public List<ModelChecker> getModelCheckers() {
        return modelCheckers;
    }

    public List<SpecificationLanguage> getSpecificationLanguages() {
        return new ArrayList<SpecificationLanguage>(specificationLanguages.values());
    }

    public InputElement getInputElement(String name) {
        InputElement element = inputElements.values().stream().filter(e -> e.getId().toLowerCase().equals(name.toLowerCase())).findAny().orElse(null);
        return element;
    }

    public List<PaletElement> getPaletElements() {
        return paletElements;
    }
}
