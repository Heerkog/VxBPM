package nl.rug.ds.bpm.editor.core.configloader;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.Main;
import nl.rug.ds.bpm.editor.models.*;
import nl.rug.ds.bpm.editor.transformer.CPNTranformerElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 29-6-2015.
 */
public class Configloader {
    public final static String resourcePath = Main.class.getResource("../../../../../resources/").getPath();//"./src/resources/";
    private HashMap<String, InputElement> inputElements;
    private HashMap<String, CPNTranformerElement> cpnTranformerElements;
    private HashMap<String, Arrow> arrows;
    private HashMap<String, SpecificationLanguage> specificationLanguages;
    private List<ModelChecker> modelCheckers;
    private List<PaletElement> paletElements;

    public Configloader() {
        System.out.println("RESOURCE PATH " + Configloader.resourcePath);
        loadCPNElements();
        loadInputElement();
        loadPaletElements();
        loadArrows();
        loadSpecificationLanguages();
        loadModelCheckers();
    }

    private void loadInputElement() {
        inputElements = new HashMap<>();
        Document document = XMLHelper.LoadXMLFromFile(resourcePath + "input-elements.xml");
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
        Document document = XMLHelper.LoadXMLFromFile(resourcePath + "palet-elements.xml");
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
        try {
            File folder = new File(resourcePath + "cpnElements");
            for (final File fileEntry : folder.listFiles()) {
                Document document = XMLHelper.LoadXMLFromFile(fileEntry);
                Node rootNode = XMLHelper.getRootNode(document);
                CPNTranformerElement element = new CPNTranformerElement(rootNode);
                cpnTranformerElements.put(element.getId(), element);
            }
        } catch (Exception e) {
            Console.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadArrows() {
        arrows = new HashMap<>();
        Document document = XMLHelper.LoadXMLFromFile(resourcePath + "arrows.xml");
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
        Document document = XMLHelper.LoadXMLFromFile(resourcePath + "specification-languages.xml");
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
        Document document = XMLHelper.LoadXMLFromFile(resourcePath + "model-checkers.xml");
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
