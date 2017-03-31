package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import nl.rug.ds.bpm.editor.core.enums.PropertyFieldType;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.Constraint;
import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.CellProperties;
import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields.CellProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class SuperCell extends mxCell implements Cloneable, java.io.Serializable {
    protected Constraint constrain;
    public CellProperties cellProperties;
    public boolean isValidSource = false;
    public boolean deleted = false;

    public SuperCell(Object value, mxGeometry geometry, String style) {
        super(value, geometry, style + ";editable=false");
        cellProperties = new CellProperties();
        cellProperties.addProperty(new CellProperty(PropertyFieldType.TextField, "Id", "Id", value.toString()));
    }

    public BPMNGraph getGraph() {
        return AppCore.gui.getGraph();
    }

    public CellProperties getCellProperties() {
        return cellProperties;
    }

    public void updateLayout() {

    }

    public void setConstraint(Constraint constrain) {
        this.constrain = constrain;
        getGraph().getView().clear(this, false, false);
        getGraph().getView().validate();
        getGraph().refresh();
    }

    public Constraint getConstraint() {
        return constrain;
    }


    public CellProperty[] getVisiblePropertyFields() {
        return new CellProperty[]{};
    }

    public void resized() {

    }

    public String getVisibleId() {
        return (String) cellProperties.getCellProperty("Id").getValue();
    }

    public void setVisibleId(String id) {
        cellProperties.getCellProperty("Id").setValue(id);
    }

    public List<ConstrainShape> centerShapes() {
        List<ConstrainShape> shapes = new ArrayList<>();
        if (this.constrain != null) {
            shapes = this.constrain.getArrow().getCenterShapes();
        }
        return shapes;
    }

    public mxCell getVertexById(String id) {
        for (Object cell : this.children) {
            if (cell instanceof mxCell && ((mxCell) cell).isVertex() && ((mxCell) cell).getId().equals(id))
                return (mxCell) cell;
        }
        return null;
    }

    public String getName() {
        return (String) cellProperties.getCellProperty("Name").getValue();
    }

    public void setName(String name) {

        cellProperties.getCellProperty("Name").setValue(name);
    }


    public String getConstraintName() {
        return constrain.getArrow().getName();
    }


    public boolean isValidTarget(Object edge, Object source) {
        return true;
    }

    public boolean isValidSource(Object edge, Object target) {
        return true;
    }

    public String[] getCpnTransitionIds() {
        List<String> transitionIds = new ArrayList<>();
        return transitionIds.stream().toArray(String[]::new);
    }


    public List<java.lang.Object> getChildren() {
        return this.children;
    }

    public <T extends mxCell> T getChild(Class<T> type, String id) {
        for (Object object : children) {
            if (type.isInstance(object) && ((mxCell) object).getId().equals(id))
                return (T) object;
        }
        return null;
    }

    public <T extends mxCell> T getChild(Class<T> type) {
        for (Object object : children) {
            if (type.isInstance(object))
                return (T) object;
        }
        return null;
    }

    public void setStyle(String key, String value) {
        getGraph().setCellStyles(key, value, new Object[]{this});
    }

    public boolean isCellSelectable() {
        return true;
    }

    public double getGeoAbsoluteY() {
        double top = this.getGeometry().getY();
        mxICell parent = getParent();
        while (parent != null && parent.getGeometry() != null) {
            top += parent.getGeometry().getY();
            parent = parent.getParent();
        }
        return top;
    }

    public double getGeoAbsoluteX() {
        double left = this.getGeometry().getX();
        mxICell parent = getParent();
        while (parent != null && parent.getGeometry() != null) {
            left += parent.getGeometry().getX();
            parent = parent.getParent();
        }
        return left;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SuperCell var1 = (SuperCell) super.clone();
        var1.cellProperties = new CellProperties();
        var1.cellProperties.addProperty(new CellProperty(PropertyFieldType.TextField, "Id", "Id", value.toString()));
        var1.constrain = this.getConstraint();

        return var1;
    }
}
