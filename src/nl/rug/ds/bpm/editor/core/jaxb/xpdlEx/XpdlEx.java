package nl.rug.ds.bpm.editor.core.jaxb.xpdlEx;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mark Kloosterhuis.
 */
@XmlRootElement(name = "XpdlEx")
public class XpdlEx {
    private List<Variable> variables = new ArrayList<>();
    private List<Transition> transitions = new ArrayList<>();

    private List<XPDLConstraint> XPDLConstraints = new ArrayList<>();
    private List<ActivityConstraint> activityConstraints = new ArrayList<>();


    @XmlElementWrapper(name = "variables")
    @XmlElement(name = "variable")
    public List<Variable> getVariables() {
        return variables;
    }

    public void AddVariable(int id, String name) {
        this.variables.add(new Variable(id, name));
    }

    @XmlElementWrapper(name = "transitions")
    @XmlElement(name = "transition")
    public List<Transition> getTransitions() {
        return transitions;
    }

    public Transition AddTransition(String id) {
        Transition transition = new Transition(id);
        this.transitions.add(transition);
        return transition;
    }


    @XmlElementWrapper(name = "XPDLConstraints")
    @XmlElement(name = "XPDLConstraint")
    public List<XPDLConstraint> getXPDLConstraints() {
        return XPDLConstraints;
    }

    public void AddConstraint(String id, String from, String to, String constrainName) {
        this.XPDLConstraints.add(new XPDLConstraint(id, from, to, constrainName));
    }

    public void AddConstraint(XPDLConstraint XPDLConstraint) {
        this.XPDLConstraints.add(XPDLConstraint);
    }


    @XmlElementWrapper(name = "ActivityConstraints")
    @XmlElement(name = "ActivityConstraint")
    public List<ActivityConstraint> getActivityConstraints() {
        return activityConstraints;
    }


    public void AddActivityConstraint(ActivityConstraint activityConstraint) {
        this.activityConstraints.add(activityConstraint);
    }



}
