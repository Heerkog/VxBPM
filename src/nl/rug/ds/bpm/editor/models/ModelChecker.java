package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.configloader.XMLHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark on 10-9-2015.
 */
public class ModelChecker {
    String id = "";
    String name = "";
    String location = "";
    boolean enabled = false;
    HashMap<String, SpecificationLanguage> specificationLanguages;
    HashMap<String, String> specificationFormats;

    public ModelChecker(Node elemNode, HashMap<String, SpecificationLanguage> specificationLanguages) {
        this.specificationLanguages = new HashMap<>();
        this.specificationFormats = new HashMap<>();

        id = XMLHelper.getNodeValue("id", elemNode);
        name = XMLHelper.getNodeValue("name", elemNode);
	    location = XMLHelper.getNodeValue("location", elemNode);
	    enabled = ((Element)elemNode).getAttribute("enabled").equals("true");

        for (Node node : XMLHelper.getChildElements(elemNode, "specificationLanguages")) {
            String specificationLanguageId = XMLHelper.getNodeValue(node);
            String format = XMLHelper.getNodeAttr("format", node, "");

            if (!specificationLanguages.containsKey(specificationLanguageId))
                Console.error("SpecificationLanguages '" + specificationLanguageId + "' not found");
            SpecificationLanguage specificationLanguage = specificationLanguages.get(specificationLanguageId);
            this.specificationLanguages.put(specificationLanguageId, specificationLanguage);

            this.specificationFormats.put(specificationLanguageId, format);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

	public String getLocation() { return location; }

	public void setLocation(String location) { this.location = location; }

	public boolean isEnabled() { return enabled; }

	public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<SpecificationLanguage> getSpecificationLanguages() {
        return new ArrayList<>(this.specificationLanguages.values().stream().sorted((p, o) -> p.getName().compareTo(o.getName())).collect(Collectors.toList()));
    }


    public String parseFormula(ConstraintResult constraint) {
        String typeId = constraint.formula.getTypeName();
        if (specificationFormats.containsKey(typeId)) {
            String format = specificationFormats.get(typeId);
            return format.replace("{c}", constraint.getConverterInput()) + "\n";
        } else {
            return typeId + " no available in " + this.getName();
        }
    }
    
    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t<modelChecker enabled=\"" + enabled + "\">\n");
        sb.append("\t\t<id>" + id + "</id>\n");
        sb.append("\t\t<name>" + name + "</name>\n");
        sb.append("\t\t<location>" + location + "</location>\n");
        sb.append("\t\t<specificationLanguages>\n");
        
        for(String key: specificationLanguages.keySet()) {
            if(specificationFormats.containsKey(key))
                sb.append("\t\t\t<specificationLanguage format=\"" + specificationFormats.get(key) + "\">" + specificationLanguages.get(key).getName() + "</specificationLanguage>\n");
        }
        
        sb.append("\t\t</specificationLanguages>\n");
        sb.append("\t</modelChecker>\n");
        
        return sb.toString();
    }
}
