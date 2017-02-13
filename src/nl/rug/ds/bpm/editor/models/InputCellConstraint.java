package nl.rug.ds.bpm.editor.models;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.core.enums.ConstraintType;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputLabelCell;
import nl.rug.ds.bpm.verification.constraints.CTLFormula;
import nl.rug.ds.bpm.verification.constraints.Formula;
import nl.rug.ds.bpm.verification.constraints.JusticeFormula;
import nl.rug.ds.bpm.verification.constraints.LTLFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class InputCellConstraint implements java.io.Serializable, IConstraintHolder {
    private String id;

    private String inputCellId;
    private Constraint constrain;
    private List<EdgeCellVariable> variablesValues = new ArrayList<EdgeCellVariable>() {
        @Override
        public String toString() {
            StringBuilder variables = new StringBuilder("");

            for (EdgeCellVariable var : variablesValues) {
                variables.append(var.getVariableId() + ":" + var.getCondition() + ":" + var.getValue());
            }
            return variables.toString();
        }
    };

    private static int cellCount = 0;
    private ConstraintStatus status = ConstraintStatus.None;


    public InputCellConstraint(InputCell inputCell) {
        this.inputCellId = inputCell.getId();
        constrain = AppCore.app.getConstraints(ConstraintType.Object).get(0);
    }


    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return constrain.getArrow().getName();
    }
    
    public String getTableName() {
        return getGraphName();
    }

    public String getInputCellId() {
        return inputCellId;
    }

    public String getGraphName() {
        List<String> conditions = new ArrayList<>();
        for (EdgeCellVariable variable : variablesValues) {
            conditions.add(String.format("%s %s %s", variable.getName(), variable.condition, variable.value));
        }
        return String.format("%s [%s]", constrain.getArrow().name, String.join(" & ", conditions));
    }

    public Constraint getConstraint() {
        return constrain;
    }

    public List<EdgeCellVariable> getVariablesValues() {
        return variablesValues;
    }

    public void setVariablesValues(List<EdgeCellVariable> variablesValues) {
        this.variablesValues = variablesValues;
        updateGraphView();
    }

    public void setConstraint(Constraint constrain) {
        this.constrain = constrain;
        updateGraphView();
    }

    public List<Formula> getFormula() {
        List<Formula> formulas = new ArrayList<>();
        constrain.getFormulas().forEach(f -> {
            String formula = f;
            if (constrain.getSpecificationLanguage().getId().equals("CTL")) {
                formulas.add(new CTLFormula(constrain, formula, this));
            } else if (constrain.getSpecificationLanguage().getId().equals("LTL")) {
                formulas.add(new LTLFormula(constrain, formula, this));
            } else if (constrain.getSpecificationLanguage().getId().equals("justice")) {
                formulas.add(new JusticeFormula(constrain, formula, this));
            }
        });


        return formulas;
    }

    public void updateGraphView() {
        try {
            AppCore.gui.getGraph().getModel().beginUpdate();
            getLabelCell().setValue(getGraphName());
        } finally {
            AppCore.gui.getGraph().getModel().endUpdate();
            AppCore.gui.getGraph().refresh();
        }
    }

    public mxCell createLabel() {
        id = "Constraint_" + cellCount++;
        mxGeometry geometry = new mxGeometry(0, 0, 10, 0);
        geometry.setRelative(false);
        //mxConstants.SHAPE_ARROW
        //geometry.setRelative(true);
        //geometry.setOffset(new mxPoint(-1, -17 - (constraintLabels.size() * 10)));
        InputLabelCell labelCell = new InputLabelCell("", geometry, "shape=;fontSize=8;resizable=0;align=left;editable=false;foldable=false", this);
        labelCell.setId(id);
        labelCell.setVertex(true);
        labelCell.setValue(getGraphName());
        labelCell.setConnectable(false);
        return labelCell;
    }

    public mxCell getLabelCell() {
        return AppCore.gui.getConstraintService().getLabelCell(this);
    }

    public InputCell getInputCell() {
        return AppCore.gui.getCellService().getCell(InputCell.class, this.inputCellId);
    }

    public void setBold(boolean bold) {
        AppCore.gui.getConstraintService().setBold(this, bold);

    }

    public void setStatus(ConstraintStatus status) {
        this.status = status;
        AppCore.gui.getGraph().setCellStyles(mxConstants.STYLE_FONTCOLOR, status.getColorCode(), new Object[]{getLabelCell()});
    }

    public void select() {

    }

    public List<ConstrainShape> centerShapes() {
        List<ConstrainShape> shapes = new ArrayList<>();
        if (this.constrain != null) {
            shapes = this.constrain.getArrow().getCenterShapes();
        }
        return shapes;
    }

    @Override
    public String toString() {

        StringBuilder st = new StringBuilder("");
        st.append(this.getInputCellId());
        st.append(this.getConstraint().getId());
        st.append(variablesValues.toString());

        return st.toString();
    }

}
