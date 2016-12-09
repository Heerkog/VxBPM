package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class EdgeCellVariable implements java.io.Serializable {
    int variableId;
    String condition;
    String value;


    public EdgeCellVariable(int variableId, String condition, String value) {
        this.variableId = variableId;
        this.condition = condition;
        this.value = value;
    }

    public Variable getVariable() {
        return AppCore.app.getVariables().stream().filter(v -> v.getId() == this.variableId).findFirst().orElse(null);
    }

    public String getName() {
        Variable variable = getVariable();
        return variable != null ? variable.getName() : "NOT FOUND";
    }

    public int getVariableId() {
        return variableId;
    }

    public String getCondition() {
        if (condition == null || condition.isEmpty())
            condition = "==";
        return condition;
    }

    public String getValue() {
        return value;
    }

    public void setVariableId(int variableId) {
        this.variableId = variableId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getValues() {
        Variable variable = getVariable();
        return variable.getValues(condition, value);
    }
}
