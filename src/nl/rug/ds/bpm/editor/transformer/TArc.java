package nl.rug.ds.bpm.editor.transformer;

import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark Kloosterhuis.
 */
public class TArc extends CPNTransformerNode {

    protected String from;
    protected String to;
    protected String condition;
    protected String weight;
    protected boolean hasOutgoingCondition;

    public TArc(String id, String from, String to, String condition, String weight, float x, float y, boolean hasOutgoingCondition) {
        super(id, x, y, false, "");
        this.from = from;
        this.to = to;
        this.condition = condition;
        this.weight = weight;
        this.hasOutgoingCondition = hasOutgoingCondition;
    }

    public int getWeight(HashMap<String, String> variables) {
        if (weight.equals("1")) {
            return 1;
        }
        try {
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put("weight", 1);

            Object compiled = MVEL.compileExpression("weight = (" + replaceVariables(this.weight, variables) + ")");
            MVEL.executeExpression(compiled, vars);
            Integer test = (Integer) vars.get("weight");

            return test;
        } catch (Exception e) {
            return 1;
        }
    }

    public String getCondition(HashMap<String, String> variables) {

        return this.condition;
    }

    private String replaceVariables(String condition, HashMap<String, String> variables) {
        for (String key : variables.keySet()) {
            String value = variables.get(key);
            condition = condition.replace("{" + key + "}", value);
        }
        return condition;
    }

}
