package nl.rug.ds.bpm.editor.core.jaxb.xpdlEx;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
@XmlRootElement
public class Transition {
    private String id;
    private List<VariableValue> variables = new ArrayList<>();

    public Transition() {
    }

    public Transition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }


    @XmlElementWrapper(name = "variableValues")
    @XmlElement(name = "variableValue")
    public List<VariableValue> getVariableValues() {
        return variables;
    }

    public void addVariableValue(int variableId, String condition, String variableValue) {
        this.variables.add(new VariableValue(variableId, condition, variableValue));
    }


}
