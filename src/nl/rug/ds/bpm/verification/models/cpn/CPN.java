package nl.rug.ds.bpm.verification.models.cpn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CPN implements Cloneable {
    private List<Variable> variables;
    private List<Place> places;
    private List<Transition> transitions;
    private List<Arc> arcs;
    private List<Place> tokens;
    private int[] marking;

    public CPN() {
        variables = new ArrayList<Variable>();
        places = new ArrayList<Place>();
        transitions = new ArrayList<Transition>();
        arcs = new ArrayList<Arc>();
        tokens = new ArrayList<Place>();
    }

    public void init() {
        int id = 0;
        for (Place p : places) {
            p.setId(id);
            id++;
        }

        for (Transition t : transitions)
            t.init();

        marking = new int[places.size()];

        for (Place t : tokens)
            marking[Integer.parseInt(t.getId().substring(1))]++;
    }

    public void addToken(Place p) {
        tokens.add(p);
    }

    public boolean removeToken(Place p) {
        return tokens.remove(p);
    }

    public int[] getInitialMarking() {
        return marking;
    }

    public void addPlace(Place p) {
        places.add(p);
    }

    public boolean removePlace(Place p) {
        return places.remove(p);
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void addTransition(Transition t) {
        transitions.add(t);
    }

    public boolean removeTransition(Transition t) {
        return transitions.remove(t);
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void addArc(Arc a) {
        arcs.add(a);
        a.getSource().addArc(a);
        a.getTarget().addArc(a);
    }

    public boolean removeArc(Arc a) {
        a.getSource().removeArc(a);
        a.getTarget().removeArc(a);
        return arcs.remove(a);
    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public void addVariable(Variable v) {
        variables.add(v);
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public Transition getTransition(String id) {
        Transition t = null;
        Iterator<Transition> i = transitions.iterator();
        boolean found = false;
        while (i.hasNext() && !found) {
            t = i.next();
            found = t.getId().equals(id);
        }

        return (found ? t : null);
    }

    public Place getPlace(String id) {
        Place p = null;
        Iterator<Place> i = places.iterator();
        boolean found = false;
        while (i.hasNext() && !found) {
            p = i.next();
            found = p.getId().equals(id);
        }

        return (found ? p : null);
    }

    public Arc getArc(CPNElement source, CPNElement target) {
        Arc a = null;
        boolean found = false;
        Iterator<Arc> i = arcs.iterator();
        while (i.hasNext() && !found) {
            a = i.next();
            found = a.getSource() == source && a.getTarget() == target;
        }

        return (found ? a : null);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean fullOutput) {
        StringBuilder count = new StringBuilder("Number of places: " + this.places.size() + "\n");
        count.append("Number of transitions: " + this.transitions.size() + "\n");
        count.append("Initial marking: " + tokens.toString() + "\n");

        if (fullOutput) {
            StringBuilder places = new StringBuilder("Places: {");
            Iterator<Place> i = getPlaces().iterator();
            while (i.hasNext()) {
                places.append(i.next().getId());
                if (i.hasNext()) places.append(", ");
            }
            places.append("}");

            StringBuilder transitions = new StringBuilder("Transitions: {");
            Iterator<Transition> j = getTransitions().iterator();
            while (j.hasNext()) {
                transitions.append(j.next().getId());
                if (j.hasNext()) transitions.append(", ");
            }
            transitions.append("}");

            StringBuilder arcs = new StringBuilder("Arcs:\n");

            for (Arc c : getArcs()) {
                if (c.getSource() instanceof Place) arcs.append("(" + c.getSource().getId() + ")");
                else if (c.getSource() instanceof Transition) arcs.append("[" + c.getSource().getId() + "]");

                if (!c.isConditional()) arcs.append(" ----- " + c.getWeight() + " -----> ");
                else arcs.append(" --- " + c.getCondition() + " - " + c.getWeight() + " ---> ");

                if (c.getTarget() instanceof Place) arcs.append("(" + c.getTarget().getId() + ")\n");
                else if (c.getTarget() instanceof Transition) arcs.append("[" + c.getTarget().getId() + "]\n");
            }

            StringBuilder vars = new StringBuilder("Variables: {");
            Iterator<Variable> k = variables.iterator();
            while (k.hasNext()) {
                vars.append(k.next().toString());
                if (k.hasNext()) vars.append(", ");
            }
            vars.append("}");


            return places.toString() + "\n\n" + transitions.toString() + "\n\n" + vars.toString() + "\n\n" + "\n\n" + arcs.toString() + "\n" + count.toString() + "\n";
        } else {
            return count.toString();
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}