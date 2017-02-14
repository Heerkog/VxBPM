package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.core.enums.ConstraintType;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import nl.rug.ds.bpm.verification.constraints.Formula;
import nl.rug.ds.bpm.verification.models.kripke.Kripke;
import nl.rug.ds.bpm.verification.models.kripke.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mark on 4-1-2016.
 */
public class ConstraintResult {
    //public ConstrainEdgeCell edge;
    public Formula formula;
    public String name;
    public String formulaInput;
    private ArrayList<String> errors;
    private ConstraintStatus status = ConstraintStatus.None;
    private IConstraintHolder constraintHolder;
    private List<String> sourceIds, targetIds;


    public ConstraintResult(IConstraintHolder constraintHolder, Formula formula, String name) {
        this.constraintHolder = constraintHolder;
        this.formula = formula;
        this.name = name;
        errors = new ArrayList<>();
        sourceIds = new ArrayList<>();
        targetIds = new ArrayList<>();
    }

    public String getConverterInput() {
        return formulaInput;
    }

    public void setFormulaInput(String formula) {
        this.formulaInput = formula;
    }

    public void setStatus(ConstraintStatus status) {
        this.status = status;
        constraintHolder.setStatus(status);
    }

    public boolean hasStatus(ConstraintStatus status) {
        return this.status == status;
    }

    public ConstraintStatus getStatus() {
        return this.status;
    }


    public void addError(String error) {
        errors.add(error);
    }

    public String getCheckerInput(Kripke kripke) {
        String ret = "";
        String source = "";
        String target = "";
        
        //if imported the formula is already parsed
        if(constraintHolder.getConstraint().getConstraintType() == ConstraintType.Import) {
            ret = formula.getFormula();
            
            //add silent as AP if used
            if(ret.toLowerCase().contains("silent")) {
                for (State s : kripke.getInitialArray())
                    s.getAtomicPropositions().add("silent");
                kripke.addAtomicProposition("silent");
            }
            
            //add used sourceIds
            for(SuperCell cell: ((ImportConstraint) constraintHolder).getCells())
                sourceIds.addAll(getAvailableAtomicPropositions(
                    cell.getCpnTransitionIds(),
                    Arrays.asList(kripke.getAtomicPropositionsArray())));
        } //else parse the formula
        else {
            if (formula.getSourceCell() != null) {
                sourceIds = getAvailableAtomicPropositions(
                        formula.getSourceCell().getCpnTransitionIds(),
                        Arrays.asList(kripke.getAtomicPropositionsArray())
                );
            }
            source = combineElements(sourceIds);
            if (sourceIds.isEmpty()) {
                addError("Source element not found");
                setStatus(ConstraintStatus.Unavailable);
            }
    
            if (formula.isEdge() && formula.getTargetCell() != null) {
                targetIds = getAvailableAtomicPropositions(
                        formula.getTargetCell().getCpnTransitionIds(),
                        Arrays.asList(kripke.getAtomicPropositionsArray())
                );
                target = combineElements(targetIds);
                if (targetIds.isEmpty()) {
                    addError("Target element not found");
                    setStatus(ConstraintStatus.Unavailable);
                }
            }
            ret = formula.getFormula().replace("$p", source).replace("$q", target);
        }
        return ret;

    }

    private List<String> getAvailableAtomicPropositions(String[] cpnIds, List<String> atomicPropositions) {
        List<String> availablIds = new ArrayList<String>();
        for (String sourceId : cpnIds) {
            if (atomicPropositions.stream().anyMatch(a -> a.equalsIgnoreCase(sourceId))) {
                availablIds.add(sourceId);
            }
        }
        return availablIds;
    }

    private String combineElements(List<String> ids) {
        return ids.size() == 1 ? ids.get(0) : "(" + String.join("|", ids) + ")";
    }

    public void select() {
        constraintHolder.select();
    }


    public List<String> getSourceIds() {
        return sourceIds;
    }

    public List<String> getTargetIds() {
        return targetIds;
    }

    public String getNodeId() {
        if (formula.getSourceCell() != null && formula.getTargetCell() != null)
            return formula.getSourceCell().getVisibleId() + " -> " + formula.getTargetCell().getVisibleId();

        return formula.getCell().getVisibleId();
    }
}
