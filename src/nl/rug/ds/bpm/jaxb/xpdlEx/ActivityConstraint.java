package nl.rug.ds.bpm.jaxb.xpdlEx;

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
public class ActivityConstraint {
    private String activityId;
    private String constraintId;
    private List<VariableValue> variables = new ArrayList<>();

    public ActivityConstraint() {
    }

    public ActivityConstraint(String activityId, String constraintId) {
        this.activityId = activityId;
        this.constraintId = constraintId;
    }



    @XmlAttribute
    public void setActivityId(String value) {
        this.activityId = value;
    }

    public String getActivityId() {
        return activityId;
    }

    @XmlAttribute
    public void setConstraintId(String constraintId) {
        this.constraintId = constraintId;
    }

    public String getConstraintId() {
        return constraintId;
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
