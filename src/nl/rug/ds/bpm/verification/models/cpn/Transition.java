package nl.rug.ds.bpm.verification.models.cpn;

import nl.rug.ds.bpm.editor.Console;
import org.mvel2.MVEL;

import java.util.*;


public class Transition extends CPNElement {
    private List<HashMap<String, String>> variableValues;
    private List<HashMap<String, String>> incomingVariableValues;
    private List<BindingElement> in, out, conditional;
    private List<String> parentIds;

    public Transition(String id) {
        this(id, "");
    }


    public Transition(String id, String name) {
        this.id = id;
        this.name = name;
        variableValues = new ArrayList<HashMap<String, String>>();
        parentIds = new ArrayList<>();
        in = new ArrayList<BindingElement>();
        out = new ArrayList<BindingElement>();
        conditional = new ArrayList<BindingElement>();
        this.width = 26;
        this.height = 26;
    }


    public boolean isEnabled(int[] marking) {
        try {
            return in.stream().allMatch(p -> p.getWeight() <= marking[p.getId()]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


       /* boolean enabled = true;
        Iterator<BindingElement> i = in.iterator();

        while (i.hasNext() && enabled) {
            BindingElement p = i.next();
            enabled = p.getWeight() <= marking[p.getId()];
        }

        return enabled;*/
    }

    public List<int[]> fire(int[] marking) {
        fireIn(marking);
        return fireOut(marking);
    }

    public void fireIn(int[] m) {
        for (BindingElement p : in) {
            m[p.getId()] = m[p.getId()] - p.getWeight();
        }
    }


    public List<int[]> fireOut(int[] marking) {
        ArrayList<int[]> markings = new ArrayList<int[]>();

        for (BindingElement p : out)
            if(marking[p.getId()] < 20)                                             //Safety catch
                marking[p.getId()] = marking[p.getId()] + p.getWeight();

        for (HashMap<String, String> values : variableValues) {
            int[] m = Arrays.copyOf(marking, marking.length);

            for (BindingElement p : conditional) {
                try {
                    if ((Boolean) MVEL.executeExpression(p.getCompiled(), values))//isValidValues(values) &&
	                    if(marking[p.getId()] < 20)                                             //Safety catch
	                        m[p.getId()] = m[p.getId()] + p.getWeight();
                } catch (Exception e) {
                    Console.error("Failed to compile expession: " + p.getCompiled());
                }
            }
            markings.add(m);
        }


        return markings;
    }

    public void init() {
        for (Arc a : getInwardArcs())
            in.add(a.getFirePair());

        for (Arc a : getOutwardArcs()) {
            BindingElement p = a.getFirePair();
            if (p.isConditional())
                conditional.add(p);
            else
                out.add(p);
        }
        variableValues = getVariableValues(getOutwardArcs());
        incomingVariableValues = getVariableValues(getInwardArcs());
    }

    private List<HashMap<String, String>> getVariableValues(List<Arc> arcs) {
        List<HashMap<String, String>> values = new ArrayList<>();
        Set<Variable> vars = new HashSet<Variable>();
        for (Arc a : arcs)
            if (a.isConditional())
                vars.addAll(a.getVariables());

        HashMap<String, String> nv = new HashMap<String, String>();
        initMap(vars, nv, values);

        if (values.isEmpty())
            values.add(nv);

        return values;
    }

    private void initMap(Set<Variable> vars, HashMap<String, String> values, List<HashMap<String, String>> values2) {
        if (vars.isEmpty())
            values2.add(values);
        else {
            Variable var = vars.iterator().next();
            Set<Variable> vars2 = new HashSet<Variable>(vars);
            vars2.remove(var);
            if (var != null) {
                for (String val : var.getValues()) {
                    HashMap<String, String> nv = new HashMap<String, String>(values);
                    nv.put(var.getName(), val);
                    initMap(vars2, nv, values2);
                }
            }
        }
    }

    public List<BindingElement> getInPairs() {
        return in;
    }

    @Override
    public void setX(Integer x) {
        this.x = x;
    }

    @Override
    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public Integer getX() {
        return x;
    }

    @Override
    public Integer getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }
}
