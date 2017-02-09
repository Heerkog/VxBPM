package nl.rug.ds.bpm.editor.core.jaxb.xpdlEx;

import org.wfmc._2008.xpdl2.ConnectorGraphicsInfos;

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
public class XPDLConstraint {

    private String id;
    private String from;
    private String to;
    private String constraintId;
    private ConnectorGraphicsInfos connectorGraphicsInfos;
    private List<VariableValue> variables = new ArrayList<>();

    public XPDLConstraint() {
    }

    public XPDLConstraint(String id, String from, String to, String constraintId) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.constraintId = constraintId;
    }

    @XmlAttribute
    public void setId(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }


    @XmlAttribute
    public void setFrom(String value) {
        this.from = value;
    }

    public String getFrom() {
        return from;
    }


    @XmlAttribute
    public void setTo(String value) {
        this.to = value;
    }

    public String getTo() {
        return to;
    }


    @XmlElement
    public void setConstraintId(String constraintId) {
        this.constraintId = constraintId;
    }

    public String getConstraintId() {
        return constraintId;
    }


    @XmlElement(name = "ConnectorGraphicsInfos")
    public void setConnectorGraphicsInfos(ConnectorGraphicsInfos value) {
        this.connectorGraphicsInfos = value;
    }

    public ConnectorGraphicsInfos getConnectorGraphicsInfos() {
        return connectorGraphicsInfos;
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
