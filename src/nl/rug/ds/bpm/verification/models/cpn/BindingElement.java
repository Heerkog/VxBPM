package nl.rug.ds.bpm.verification.models.cpn;

import java.io.Serializable;

public class BindingElement {
    private int id;
    private int weight;
    private boolean conditional;
    private Serializable c;

    public BindingElement(int id, int weight) {
        this.id = id;
        this.weight = weight;
        conditional = false;
        c = null;
    }

    public BindingElement(int id, int weight, Serializable c) {
        this(id, weight);
        conditional = true;
        this.c = c;
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public Serializable getCompiled() {
        return c;
    }

    public void setCompiled(Serializable c) {
        this.c = c;
        conditional = true;
    }

    public boolean isConditional() {
        return conditional;
    }
}
