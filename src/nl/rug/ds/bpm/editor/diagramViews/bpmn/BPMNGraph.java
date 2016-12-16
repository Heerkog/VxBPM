package nl.rug.ds.bpm.editor.diagramViews.bpmn;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxConnectionConstraint;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.models.InputElement;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark on 30-6-2015.
 */
public class BPMNGraph extends mxGraph {
    private List<InputElement> inputElements;
    private mxGraphModel model;
    public BPMNService bpmnService;

    public BPMNGraph() {
        super();
        model = (mxGraphModel) this.getModel();
        bpmnService = new BPMNService(this, model);
        setKeepEdgesInForeground(true);
    }

    @Override
    public boolean isValidDropTarget(Object cell, Object[] cells) {
        if (cell instanceof InputCell) {
            InputCell inputCell = (InputCell) cell;
            return inputCell.getInputElement().IsGroup();
        }
        return false;
    }

    public void reset() {
        this.removeCells(this.getChildVertices(this.getDefaultParent()));
        model.clear();
        bpmnService = new BPMNService(this, model);
    }

    @Override
    public String getToolTipForCell(Object cell) {
        if (cell instanceof InputCell) {
            InputCell sCell = (InputCell) cell;
            return sCell.getInputElement().getName() + " - " + convertValueToString(cell);
        }
        return convertValueToString(cell);
    }

    @Override
    public String getEdgeValidationError(Object edge, Object source, Object target) {

        try {
            if (target != null && !((mxCell) target).isConnectable())//MARK FIX isConnectable
                return "";
            if (source != null && !((mxCell) source).isConnectable())//MARK FIX  isConnectable
                return "";

            if (target != null && (target instanceof SuperCell) && !((SuperCell) target).isValidTarget(edge, source)) {
                return "";
            }
            if (source != null && (source instanceof SuperCell) && !((SuperCell) source).isValidSource(edge, target)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getEdgeValidationError(edge, source, target);
    }

    @Override
    public void cellConnected(Object edge, Object terminal, boolean source,
                              mxConnectionConstraint constraint) {
        if (edge != null && !source) {
            ((SuperCell) edge).isValidSource = false;
        }
        super.cellConnected(edge, terminal, source, constraint);
    }

  /*  @Override
    public boolean isValidSource(Object cell) {

        if (cell != null && (cell instanceof InputCell) && ((SuperCell) cell).isValidSource(null, cell)) {
            Console.log("TRUE" + cell.toString());
            return true;
        }
        if (cell != null)
            Console.log("false" + ((cell instanceof SuperCell) ? "SUPER" : " NULL") + cell.toString());
        return false;

    }

    @Override
    public boolean isValidTarget(Object cell) {
        if (cell != null && (cell instanceof InputCell) && ((SuperCell) cell).isValidTarget(null, cell)) {
            return true;
        }
        return false;
    }*/

    @Override
    public Object createEdge(Object parent, String id, Object value,
                             Object source, Object target, String style) {
        EdgeCell edge = new EdgeCell(value, new mxGeometry(), "entity;edgeStyle=elbowEdgeStyle;orthogonal=false;fontSize=8;");

        edge.setId(id);
        edge.setEdge(true);
        edge.getGeometry().setRelative(true);
        edge.isValidSource = true;

        return edge;

    }

    @Override
    public void cellsResized(Object[] cells, mxRectangle[] bounds) {
        super.cellsResized(cells, bounds);
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] instanceof SuperCell) {
                SuperCell cell = (SuperCell) cells[i];
                cell.resized();
            }
        }
    }

    public List<SuperCell> getAllSuperCells() {
        return model.getCells().entrySet().stream()
                .filter(p -> SuperCell.class.isInstance(p.getValue()))
                .map(c -> (SuperCell) c.getValue())
                .collect(Collectors.toList());
    }


    public List<EdgeCell> getAllEdgeCells() {
        return model.getCells().entrySet().stream()
                .filter(p -> EdgeCell.class.isInstance(p.getValue()) && !ConstrainEdgeCell.class.isInstance(p.getValue()))
                .map(c -> (EdgeCell) c.getValue())
                .collect(Collectors.toList());
    }


    @Override
    public boolean isCellSelectable(Object cell) {
        if (cell instanceof SuperCell && ((SuperCell) cell).isCellSelectable())
            return true;
        return false;
    }

    public void setInputElements(List<InputElement> inputElements) {
        this.inputElements = inputElements;
    }

    public List<InputElement> getInputElements() {
        return inputElements;
    }

    private InputElement getInputElementByName(String id) {
        InputElement element = null;
        if (inputElements != null) {
            element = inputElements.stream().filter(p -> p.getId().equals(id)).findFirst().get();
        }
        return element;
    }

    public List<InputCell> getByGenName(String genName) {
        return model.getCells().entrySet().stream()
                .filter(p -> InputCell.class.isInstance(p.getValue()))
                .map(c -> (InputCell) c.getValue())
                .filter(c -> c.getInputElement().getName().equals(genName))
                .collect(Collectors.toList());

    }

    public List<InputCell> getByIdName(String genId) {
        return model.getCells().entrySet().stream()
                .filter(p -> InputCell.class.isInstance(p.getValue()))
                .map(c -> (InputCell) c.getValue())
                .filter(c -> c.getInputElement().getGenId().equals(genId))
                .collect(Collectors.toList());

    }

    public SuperCell getById(String genId) {
        return model.getCells().entrySet().stream()
                .filter(p -> SuperCell.class.isInstance(p.getValue()))
                .map(c -> (SuperCell) c.getValue())
                .filter(c -> c.getId().equals(genId))
                .findFirst().orElse(null);
    }

    public SuperCell getByVisibleId(String genId) {
        return model.getCells().entrySet().stream()
                .filter(p -> SuperCell.class.isInstance(p.getValue()))
                .map(c -> (SuperCell) c.getValue())
                .filter(c -> c.getVisibleId().equals(genId))
                .findFirst().orElse(null);
    }

}
