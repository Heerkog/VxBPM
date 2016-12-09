package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import nl.rug.ds.bpm.editor.models.EdgeCellVariable;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */


public class EdgeCell extends SuperCell implements java.io.Serializable {


    private Boolean dashed = false;
    private mxCell labelCell;
    private Object labelCellObject;

    protected List<EdgeCellVariable> variablesValues = new ArrayList<EdgeCellVariable>() {
        @Override
        public String toString() {
            StringBuilder variables = new StringBuilder("");

            for (EdgeCellVariable var : variablesValues) {
                variables.append(var.getVariableId() + ":" + var.getCondition() + ":" + var.getValue());
            }
            return variables.toString();
        }
    };

    public EdgeCell(Object var1, mxGeometry var2, String var3) {
        super("", var2, var3);

        mxGeometry geometry = new mxGeometry(0, 0, 30, 8);
        geometry.setRelative(true);
        geometry.setOffset(new mxPoint(-10, -10));

        mxCell labelCell = new mxCell("", geometry, "shape=TESTSHAPE;fillColor=#FF0000;fontSize=8;labelBackgroundColor=#FFFFFF");
        labelCell.setVertex(true);
        labelCell.setConnectable(false);
        labelCell.setId(id);
        this.setValue("");
        this.setId((String) var1);

        this.insert(labelCell);

        getGraph().setCellStyles(mxConstants.STYLE_SPACING, "0", new Object[]{labelCell});
        setShape();
    }


    public void setDashed(Boolean dashed) {
        this.dashed = !this.dashed;


    }

    public void setShape() {
        //getApplication().graph.setCellStyles(mxConstants.STYLE_SHAPE, "TESTLINK", new Object[]{labelCellObject});
        //getGraph().setCellStyles(mxConstants.STYLE_STARTARROW, "ConstrainMarker", new Object[]{this});
        //getGraph().setCellStyles(mxConstants.STYLE_ENDARROW, "ConstrainMarker", new Object[]{this});
    }


    public void setVariableValues(List<EdgeCellVariable> variablesValues) {
        this.variablesValues = variablesValues;
    }

    public List<EdgeCellVariable> getVariablesValues() {
        return this.variablesValues;
    }


    @Override
    public void updateLayout() {
        String condition = "";
        mxCell cell = (mxCell) this.getChildAt(0);
        for (int i = 0; i < variablesValues.size(); i++) {
            EdgeCellVariable edgeValue = variablesValues.get(i);
            Variable var = AppCore.app.getVariables().stream().filter(v -> v.getId() == edgeValue.getVariableId()).findFirst().orElse(null);

            if (var != null) {
                if (i != 0)
                    condition += "\n";
                condition += String.format("%s %s %s", var.getName(), edgeValue.getCondition(), edgeValue.getValue());
            }
        }


        if (variablesValues.size() > 0) {

        }
        cell.setValue(condition);
    }

    public List<ConstrainShape> startShapes() {
        return new ArrayList<>();
    }

    public List<ConstrainShape> endShapes() {
        return new ArrayList<>();
    }


}
