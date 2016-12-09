package nl.rug.ds.bpm.verification.models.cpn;

import nl.rug.ds.bpm.editor.Console;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Arc extends ElementGeometry {
    private int weight;
    private String condition;
    private CPNElement source, target;
    private Serializable compiled;
    private ArrayList<Variable> variables;


    public Arc(CPNElement source, CPNElement target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.condition = "";
        uUId = UUID.randomUUID();
    }

    public Arc(CPNElement source, CPNElement target, int weight, String condition) {
        this(source, target, weight);
        this.condition = condition;
    }

    public Arc(CPNElement source, CPNElement target, int weight, String condition, List<Variable> variables) {
        this(source, target, weight);
        this.condition = condition;
        this.setVariables(variables);
    }


    public CPNElement getSource() {
        return source;
    }

    public CPNElement getTarget() {
        return target;
    }

    public boolean isConditional() {
        return !condition.isEmpty();
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
        try {
            compiled = MVEL.compileExpression(condition);
        } catch (Exception e) {
            Console.error("Failed to compile expression: " + condition);
        }
    }

    public Serializable getCompiled() {
        return compiled;
    }

    public BindingElement getFirePair() {
        int id = 0;
        try {
            id = (source instanceof Place ? Integer.parseInt(source.getId().substring(1)) : Integer.parseInt(target.getId().substring(1)));
        } catch (Exception e) {
            e = e;
        }
        BindingElement p = new BindingElement(id, weight);
        if (!condition.isEmpty())
            p.setCompiled(compiled);
        return p;
    }

}
