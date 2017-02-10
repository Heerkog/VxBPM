package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import nl.rug.ds.bpm.verification.constraints.CTLFormula;
import nl.rug.ds.bpm.verification.constraints.Formula;
import nl.rug.ds.bpm.verification.constraints.JusticeFormula;
import nl.rug.ds.bpm.verification.constraints.LTLFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p256867 on 7-2-2017.
 */
public class ImportConstraint implements java.io.Serializable, IConstraintHolder {
    private static int i = 0;
    private String id;
    private Constraint constraint;
    private ConstraintStatus status = ConstraintStatus.None;
    private SuperCell cell;

    public ImportConstraint(SuperCell cell, Constraint constraint) {
        id = "ic" + i++;
        this.cell = cell;
        this.constraint = constraint;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<EdgeCellVariable> getVariablesValues() {
        return new ArrayList<EdgeCellVariable>();
    }

    @Override
    public Constraint getConstraint() {
        return constraint;
    }

    @Override
    public List<Formula> getFormula() {
        List<Formula> formulas = new ArrayList<>();
        constraint.getFormulas().forEach(f -> {
            String formula = f;
            if (constraint.getSpecificationLanguage().getId().equals("CTL")) {
                formulas.add(new CTLFormula(cell, constraint, formula, false));
            } else if (constraint.getSpecificationLanguage().getId().equals("LTL")) {
                formulas.add(new LTLFormula(cell, constraint, formula, false));
            } else if (constraint.getSpecificationLanguage().getId().equals("justice")) {
                formulas.add(new JusticeFormula(cell, constraint, formula, false));
            }
        });
        return formulas;
    }

    @Override
    public void setStatus(ConstraintStatus status) {
        this.status = status;
    }

    @Override
    public void select() {}
}
