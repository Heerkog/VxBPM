package nl.rug.ds.bpm.jaxb.xpdlEx;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mark Kloosterhuis.
 */
@XmlRootElement
public class VariableValue {
    private int variableId;
    private String condition;
    private String value;

    public VariableValue() {
    }

    public VariableValue(int variableId, String condition, String value) {
        this.variableId = variableId;
        this.condition = condition;
        this.value = value;
    }

    public int getVariableId() {
        return variableId;
    }

    @XmlAttribute
    public void setVariableId(int variableId) {
        this.variableId = variableId;
    }

    @XmlAttribute
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
