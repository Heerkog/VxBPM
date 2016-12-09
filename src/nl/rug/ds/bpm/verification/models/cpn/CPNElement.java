package nl.rug.ds.bpm.verification.models.cpn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CPNElement extends ElementGeometry implements Comparable<CPNElement> {
    protected String id;
    protected String name;
    protected String label;
    protected String convertSourceId;

    private List<Arc> inwardArcs, outwardArcs;

    public CPNElement() {
        inwardArcs = new ArrayList<Arc>();
        outwardArcs = new ArrayList<Arc>();
        uUId = UUID.randomUUID();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void addArc(Arc a) {
        if (a.getSource() == this)
            outwardArcs.add(a);
        else
            inwardArcs.add(a);
    }

    public void removeArc(Arc a) {
        if (a.getSource() == this)
            outwardArcs.remove(a);
        else
            inwardArcs.remove(a);
    }

    public List<Arc> getInwardArcs() {
        return inwardArcs;
    }

    public List<Arc> getOutwardArcs() {
        return outwardArcs;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(CPNElement o) {

        return (o == null ? 0 : id.compareTo(o.getId()));
    }

    @Override
    public boolean equals(Object arg0) {

        return (arg0 == null ? false : id.equals(((CPNElement) arg0).getId()));
    }
    public String getConvertSourceId() {
        return convertSourceId;
    }

    public void setConvertSourceId(String convertSourceId) {
        this.convertSourceId = convertSourceId;
    }
}
