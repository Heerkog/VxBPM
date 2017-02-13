package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.verification.constraints.Formula;

import java.util.List;

/**
 * Created by Mark on 23-1-2016.
 */
public interface IConstraintHolder {
    String getId();
    String getName();
    List<EdgeCellVariable> getVariablesValues();
    Constraint getConstraint();
    List<Formula> getFormula();
    void setStatus(ConstraintStatus status);
    void select();

}
