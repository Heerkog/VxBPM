package nl.rug.ds.bpm.editor.services;

import com.mxgraph.model.mxGraphModel;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.IConstraintHolder;
import nl.rug.ds.bpm.editor.models.ImportConstraint;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputLabelCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 22-1-2016.
 */
public class ConstraintService {
    BPMNGraph graph;
    mxGraphModel model;

    public ConstraintService(BPMNGraph graph) {
        this.graph = graph;
    }
    private CellService getCellService(){
        return AppCore.gui.getCellService();
    }

    public InputLabelCell getLabelCell(InputCellConstraint constraint){
        InputCell inputCell = getCellService().getCell(InputCell.class,constraint.getInputCellId());
        if(inputCell != null){
            return inputCell.getChild(InputLabelCell.class,constraint.getId());
        }
        return null;
    }
    public void setBold(InputCellConstraint constraint,boolean bold){
        InputLabelCell label = getLabelCell(constraint);
        if(label != null)
            label.setBold(bold);
    }
    public List<InputCellConstraint> getAllInputCellConstraint(){
        List<InputCellConstraint> constraints = new ArrayList<>();
        for(InputCell cell :getCellService().getCells(InputCell.class)){
            constraints.addAll(cell.getConstraints());
        }
        return constraints;
    }
    public List<ConstrainEdgeCell> getAllEdgeConstraint(){
        return getCellService().getCells(ConstrainEdgeCell.class);
    }
    public List<ImportConstraint> getAllImportConstraint() { return AppCore.gui.importService.getImportConstraints(); }

    public List<IConstraintHolder> getAllConstraintHolders(){
        List<IConstraintHolder> constraints = new ArrayList<>();
        constraints.addAll(this.getAllInputCellConstraint());
        constraints.addAll(getAllEdgeConstraint());
        constraints.addAll(getAllImportConstraint());
        return constraints;
    }


}
