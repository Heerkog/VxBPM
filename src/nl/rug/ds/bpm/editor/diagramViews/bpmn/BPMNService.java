package nl.rug.ds.bpm.editor.diagramViews.bpmn;

import com.mxgraph.model.mxGraphModel;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCellEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Kloosterhuis.
 */
public class BPMNService {
    BPMNGraph graph;
    mxGraphModel model;

    public static final String StartEvent = "startEvent";
    public static final String EndEvent = "endEvent";
    public static final String Activity = "activity";
    public static final String ActivityLoopWhile = "loopWhile";
    public static final String ActivityLoopRepeat = "loopRepeat";
    public static final String ActivityMiVariant = "miVariant";
    public static final String ActivityEventError = "activityError";
    public static final String ActivityEventCompensation = "activityIntermediateEventCompensation";


    public static final String Exclusive = "gateExclusive";
    public static final String Parallel = "gateParallel";
    public static final String Deferred = "gateDeferred";
    public static final String Complex = "gateComplex";
    public static final String SwimLane = "swimlane";
    public static final String Group = "group";


    public BPMNService(BPMNGraph graph, mxGraphModel model) {
        this.graph = graph;
        this.model = model;
    }

    public List<InputCell> getStartElements() {
        return getByName(StartEvent);
    }

    public List<InputCell> getEndElements() {
        return getByName(EndEvent);
    }

    //Activities
    public List<InputCell> getActvitiyElements() {
        return getByName(Activity);
    }

    public List<InputCell> getActivityLoopWhileElements() {
        return getByName(Activity);
    }


    //Gates
    public List<InputCell> getExclusiveElements() {
        return getByName(Exclusive);
    }

    public List<InputCell> getParallelElements() {
        return getByName(Parallel);
    }

    public List<InputCell> getComplexElements() {
        return getByName(Complex);
    }

    public List<InputCell> getDeferredElements() {
        return getByName(Deferred);
    }


    public List<EdgeCell> getEdges() {
        return getEdgeCells();
    }

    public List<ConstrainEdgeCell> getRelations() {
        return getRelationCells();
    }

    public List<InputCell> getCells() {
        return model.getCells().entrySet().stream().filter(p -> InputCell.class.isInstance(p.getValue()))
                .filter(p -> !InputCellEvent.class.isInstance(p.getValue()))
                .map(c -> (InputCell) c.getValue())
                .filter(c->!c.deleted)
                .collect(Collectors.toList());
    }
    public List<InputCell> getByName(String name) {
        return model.getCells().entrySet().stream()
                .filter(p -> InputCell.class.isInstance(p.getValue()))
                .filter(p -> !InputCellEvent.class.isInstance(p.getValue()))
                .map(c -> (InputCell) c.getValue())
                .filter(c -> c.getInputElement().getBpmnElementName().equalsIgnoreCase(name))
                .filter(c->!c.deleted)
                .collect(Collectors.toList());
    }

    private List<EdgeCell> getEdgeCells() {
        return model.getCells().entrySet().stream()
                .filter(p -> EdgeCell.class.isInstance(p.getValue()))
                .map(c -> (EdgeCell) c.getValue())
                .collect(Collectors.toList());
    }

    private List<ConstrainEdgeCell> getRelationCells() {
        return model.getCells().entrySet().stream()
                .filter(p -> ConstrainEdgeCell.class.isInstance(p.getValue()))
                .map(c -> (ConstrainEdgeCell) c.getValue())
                .collect(Collectors.toList());
    }
}
