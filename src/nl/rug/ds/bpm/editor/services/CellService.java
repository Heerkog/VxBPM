package nl.rug.ds.bpm.editor.services;

import com.mxgraph.model.mxGraphModel;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Kloosterhuis.
 */
public class CellService {
    BPMNGraph graph;
    mxGraphModel model;

    public CellService(BPMNGraph graph) {
        this.graph = graph;
        model = (mxGraphModel) graph.getModel();
    }

    public List<SuperCell> getSelectedCells() {
        List<SuperCell> cells = Arrays.stream(graph.getSelectionCells())
                .filter(p -> SuperCell.class.isInstance(p))
                .map(p -> (SuperCell) p)
                .filter(c->!c.deleted)
                .collect(Collectors.toList());
        return cells;
    }

    public <T extends SuperCell> List<T> getCells(Class<T> type) {
        return model.getCells().entrySet().stream()

                .filter(p -> type.getName().equals(p.getValue().getClass().getName()))
                .map(c -> (T) c.getValue())
                .filter(c->!c.deleted)
                .filter(c -> c.getParent() != null)
                .collect(Collectors.toList());
    }

    public <T extends SuperCell> T getCell(Class<T> type, String id) {
        return model.getCells().entrySet().stream()
                .filter(p -> type.isInstance(p.getValue()))
                .map(c -> (T) c.getValue())
                .filter(c->!c.deleted)
                .filter(c -> c.getId().equals(id))

                .findFirst().orElse(null);
    }

   /* public InputCell getInputCell(String genId) {
        return model.getCells().entrySet().stream()
                .filter(p -> InputCell.class.isInstance(p.getValue()))
                .map(c -> (InputCell) c.getValue())
                .filter(c -> c.getId().equals(genId))
                .findFirst().orElse(null);
    }*/


}
