package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.models.IConstraintHolder;
import nl.rug.ds.bpm.verification.constraints.CTLFormula;
import nl.rug.ds.bpm.verification.constraints.Formula;
import nl.rug.ds.bpm.verification.constraints.JusticeFormula;
import nl.rug.ds.bpm.verification.constraints.LTLFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class ConstrainEdgeCell extends EdgeCell implements java.io.Serializable, IConstraintHolder
{
    ConstraintStatus status;

    public ConstrainEdgeCell(Object var1, mxGeometry var2, String var3) {
        super("", var2, var3);
        setConstraint(AppCore.app.getDefaultConstraint());
        setDashed(true);

        getGraph().setCellStyles(mxConstants.STYLE_STARTARROW, "ConstrainMarker", new Object[]{this});
        getGraph().setCellStyles(mxConstants.STYLE_ENDARROW, "ConstrainMarker", new Object[]{this});
        getGraph().setCellStyles(mxConstants.STYLE_SHADOW, "50", new Object[]{this});
        getGraph().setCellStyles(mxConstants.STYLE_STARTSIZE, "10", new Object[]{this});
        getGraph().setCellStyles(mxConstants.STYLE_ENDSIZE, "10", new Object[]{this});
        status = ConstraintStatus.None;

    }

    @Override
    public void updateLayout() {
        super.updateLayout();

    }

    @Override
    public List<ConstrainShape> startShapes() {
        List<ConstrainShape> shapes = new ArrayList<>();
        if (this.constrain != null) {
            shapes = this.constrain.getArrow().getSourceShapes();
        }
        return shapes;
    }

    @Override
    public List<ConstrainShape> endShapes() {
        getGraph().setCellStyles(mxConstants.STYLE_DASHED, constrain.getArrow().isDashed() ? "true" : "false", new Object[]{this});
        List<ConstrainShape> shapes = new ArrayList<>();
        if (this.constrain != null) {
            shapes = this.constrain.getArrow().getTargetShapes();
        }
        return shapes;
    }

    public void setStatus(ConstraintStatus status) {
        this.status = status;
        getGraph().setCellStyles(mxConstants.STYLE_STROKECOLOR, status.getColorCode(), new Object[]{this});
    }

    public ConstraintStatus getStatus() {
        return status;
    }

    public List<Formula> getFormula() {
        InputCell source = (InputCell) this.getSource();
        InputCell target = (InputCell) this.getTarget();

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

        /*AppCore.app.selectedModelChecker().getSpecificationLanguages().forEach(s -> {
            s.getConstrains().stream().filter(c -> c.getArrow().getId().equals(constrain.getArrow().getId())).forEach(c -> {
                c.getFormulas().forEach(f -> {
                    String formula = f;
                    Arrow arrow = c.getArrow();
                    if (s.getId().equals("CTL")) {
                        formulas.add(new CTLFormula(arrow, formula, this));
                    } else if (s.getId().equals("LTL")) {
                        formulas.add(new LTLFormula(arrow, formula, this));
                    } else if (s.getId().equals("justice")) {
                        formulas.add(new JusticeFormula(arrow, formula, this));
                    }
                });
            });
        });*/
        return formulas;
    }

    public void select() {
        AppCore.gui.getGraphComponent().selectCellForEvent(this, null);
        // graphComponent.selectCellForEvent(mxCell, mouseEvent)
    }

    @Override
    public String toString() {
        InputCell source = (InputCell) this.getSource();
        InputCell target = (InputCell) this.getTarget();
        StringBuilder st = new StringBuilder("");
        st.append(this.constrain.getId());
        if (source != null)
            st.append(source.getId());
        if (target != null)
            st.append(target.getId());
        st.append(variablesValues.toString());

        return st.toString();
    }
}
