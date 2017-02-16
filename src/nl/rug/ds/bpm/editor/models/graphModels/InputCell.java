package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.PropertyFieldType;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.IConstraintHolder;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.editor.models.InputElement;
import nl.rug.ds.bpm.editor.models.InputElementAttribute;
import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields.CellProperty;
import nl.rug.ds.bpm.editor.utils.StringUtils;
import nl.rug.ds.bpm.verification.models.cpn.CPNElement;
import nl.rug.ds.bpm.verification.models.cpn.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Mark Kloosterhuis.
 */
public class InputCell extends SuperCell implements Cloneable{
    private InputElement inputElement;
    private List<InputCellConstraint> constraints = new ArrayList<>();
    private String labelPostion = "bottom";
    private InputCellEvent cellEvent;

    public InputCell(InputElement inputElement, Object var1, mxGeometry var2, String var3) {
        super(var1, var2, var3);

        this.inputElement = inputElement;
        cellProperties.addProperty(new CellProperty(PropertyFieldType.TextField, "Name", "Name", ""));


        setStyle(mxConstants.STYLE_FOLDABLE, "false");
        // getGraph().setCellStyles(mxConstants.ALIGN_BOTTOM, "true", new Object[]{this});
        setStyle(mxConstants.STYLE_VERTICAL_ALIGN, "left");
        setStyle(mxConstants.STYLE_VERTICAL_LABEL_POSITION, "bottom");
        setStyle(mxConstants.STYLE_ALIGN, "left");
        setStyle(mxConstants.STYLE_STARTSIZE, "20");
        setStyle(mxConstants.STYLE_RESIZABLE, inputElement.getResizable().toString());
        if (!inputElement.isEdge())
            setStyle(mxConstants.STYLE_SHAPE, inputElement.getId());
        setStyle(mxConstants.STYLE_EDITABLE, "false");

        Boolean hideId = !inputElement.isGenIdVisible();


        setInputElement(inputElement);

        labelPostion = "bottom";
        renderConstraints();


        //

    }


    public InputElement getInputElement() {
        return inputElement;
    }

    protected static String generateVisibleId(BPMNGraph graph,String genId) {
        if (!genId.contains("{x}")) {
            genId += "{x}";
        }
        int genIdCount = 0;
        OptionalInt maxElement = graph.getByIdName(genId).stream()
                .map(c -> c.getVisibleId().replaceAll("[^0-9]", ""))
                .filter(c -> !c.isEmpty())
                .map(c -> Integer.parseInt(c)).mapToInt(Integer::intValue).max();
        if (maxElement.isPresent())
            genIdCount = maxElement.getAsInt() + 1;

        genId = genId.replace("{x}", String.valueOf(genIdCount));
        return genId;
    }


    public static InputCell generateCell(BPMNGraph graph, InputElement inputElement) {
        String genId = inputElement.getGenId();
        genId = generateVisibleId(graph,genId);

        String UName = inputElement.getName();
        mxGeometry geo = new mxGeometry(0, 0, inputElement.getWidth(), inputElement.getHeight());
        InputCell cell = new InputCell(inputElement, genId, geo, UName);
        cell.setVisibleId(genId);
        cell.setVertex(true);
        cell.setValue(genId);
        cell.setId(UUID.randomUUID().toString());
        return cell;
    }

    public static InputCell generateCell(BPMNGraph graph, InputElement inputElement, String genId, int x, int y) {
        String UName = inputElement.getName();
        InputCell cell = new InputCell(inputElement, genId, new mxGeometry(x, y, inputElement.getWidth(), inputElement.getHeight()), UName);
        //cell.setEdge(false);
        cell.setVertex(true);
        return cell;
    }

    public void generateVisibleId() {
        String genId = inputElement.getGenId();
        if (!genId.contains("{x}")) {
            genId += "{x}";
        }
        int genIdCount = 0;
        OptionalInt maxElement = getGraph().getByIdName(genId).stream()
                .map(c -> c.getVisibleId().replaceAll("[^0-9]", ""))
                .filter(c -> !c.isEmpty())
                .map(c -> Integer.parseInt(c)).mapToInt(Integer::intValue).max();
        if (maxElement.isPresent())
            genIdCount = maxElement.getAsInt() + 1;

        genId = genId.replace("{x}", String.valueOf(genIdCount));
        this.setVisibleId(genId);
    }

    public void renderConstraints() {

        int index = 0;
        double top = this.getGeometry().getHeight() + 10;
        double left = -1;
        if (!labelPostion.equals("bottom")) {
            top = top - (15 + (constraints.size() * 10));
            left = 30;
        }

        for (IConstraintHolder constraint : constraints) {
            top += 10;

            // top = this.getGeometry().getHeight()-10-(index * 10);

            getChild(InputLabelCell.class, constraint.getId()).getGeometry().setOffset(new mxPoint(left, top));
            index++;
        }
    }


    public List<InputCellConstraint> getConstraints() {
        return constraints;
    }

    public InputCellConstraint addConstraint() {
        InputCellConstraint constraint = new InputCellConstraint(this);
        constraints.add(constraint);
        this.insert(constraint.createLabel());
        renderConstraints();
        return constraint;
    }


    @Override
    public void updateLayout() {
        String label = this.getVisibleId();
        if (!this.getName().isEmpty())
            label += "-" + StringUtils.breakLines(this.getName(),40);
        this.setValue(label);
    }


    @Override
    public void resized() {
        renderConstraints();
    }

    @Override
    public boolean isValidTarget(Object edge, Object source) {
        if (edge instanceof InputEdgeCell) {
            return this.getInputElement().getCpnTranformerElement().getIncomingMessageElement() != null;
        } else if (edge instanceof ConstrainEdgeCell) {
            return this.inputElement.canHaveConstraints();
        } else {
            if (this.inputElement.IsGroup())
                return false;
            int incomingCount = (int) this.getIncomingEdges().stream().filter(e -> edge == null || !e.getId().equals(((EdgeCell) edge).getId())).count();
            return incomingCount + 1 <= this.getInputElement().getMaxIncoming();
        }
    }

    @Override
    public boolean isValidSource(Object edge, Object target) {
        if (edge instanceof InputEdgeCell) {
            return this.getInputElement().getCpnTranformerElement().getOutgoingMessageElement() != null;
        } else if (edge instanceof ConstrainEdgeCell) {
            return this.inputElement.canHaveConstraints();
        } else {
            if (this.inputElement.IsGroup())
                return false;

            //Count all outgoing edges but except the current edge
            int outgoingCount = (int) this.getOutgoingEdges().stream()
                    .filter(e -> !e.isValidSource)
                    .filter(e -> edge == null || !e.getId().equals(((EdgeCell) edge).getId()))
                    .count();
            return outgoingCount + 1 <= this.getInputElement().getMaxOutgoing();
        }
    }


   /* @Override
    public ConstrainShape[] centerShapes() {
        return new ConstrainShape[]{
                ConstrainShape.ARROWEAST,
                ConstrainShape.ECLIPSEFILLED
        };
    }*/

    public List<EdgeCell> getEdges() {
        List<EdgeCell> edges = new ArrayList<>();
        if (this.edges != null) {
            edges = this.edges.stream()
                    .filter(p -> EdgeCell.class.isInstance(p))
                    .map(c -> (EdgeCell) c)
                    .collect(Collectors.toList());
        }
        return edges;
    }

    @Override
    public String[] getCpnTransitionIds() {
        List<String> transitionIds = new ArrayList<>();
        List<SuperCell> bpmnElements = new ArrayList<>();

        /*if (this.inputElement.IsGroup()) {
            bpmnElements = this.children.stream()
                    .filter(c -> InputCell.class.isInstance(c))
                    .map(c -> (InputCell) c)
                    .filter(c -> c.getInputElement().canHaveConstraints())
                    .collect(Collectors.toList());
        } else {
            bpmnElements.add(this);
        }*/
        bpmnElements.add(this);

        for (SuperCell cell : bpmnElements) {
            List<CPNElement> sourceCpnElements = AppCore.app.converter.bpmnToCPNLookup.get(cell.getVisibleId());
            if (sourceCpnElements != null)
                transitionIds.addAll(sourceCpnElements.stream().filter(f -> Transition.class.isInstance(f)).map(i -> i.getId()).collect(Collectors.toList()));
            else
                transitionIds.add(cell.getVisibleId());
        }


        return transitionIds.stream().toArray(String[]::new);
    }

    public List<String> getParentIds() {
        return getParentIds(new ArrayList<>());
    }

    public List<String> getParentIds(List<String> ids) {
        if (this.getParent() instanceof InputCell) {
            InputCell parentCell = (InputCell) this.getParent();
            if (parentCell.getInputElement().IsGroup()) {
                ids.add(parentCell.getVisibleId());
                parentCell.getParentIds(ids);
            }
        }
        return ids;
    }


    public List<EdgeCell> getIncomingEdges() {
        try {
            return this.getEdges().stream()
                    .filter(e -> e.getTarget() != null && e.getTarget().getId().equals(this.getId()) && !ConstrainEdgeCell.class.isInstance(e))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<EdgeCell>();

        }
    }

    public List<EdgeCell> getOutgoingEdges() {
        try {
            return this.getEdges().stream()
                    .filter(e -> e.getSource() != null && e.getSource().getId().equals(this.getId()) && !ConstrainEdgeCell.class.isInstance(e))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<EdgeCell>();
        }
    }

    public InputCellEvent getCellEvent() {
        return cellEvent;
    }

    public void setInputElement(InputElement inputElement) {
        this.inputElement = inputElement;
        setStyle(mxConstants.STYLE_RESIZABLE, inputElement.getResizable().toString());
        setStyle(mxConstants.STYLE_SHAPE, inputElement.getId());
        inputElement.getCustomStyleProperties().forEach((key, value) ->
                setStyle(key, value)
        );
        cellProperties.removeCustomProperties();

        for (InputElementAttribute attribute : inputElement.getAttributes()) {
            cellProperties.addProperty(new CellProperty(PropertyFieldType.TextField, attribute.getName(), attribute.getLabel(), "", true));
        }
        labelPostion = (String) getGraph().getCellStyle(this).get(mxConstants.STYLE_VERTICAL_LABEL_POSITION);

        if (inputElement.getEventInputElementId() == null && cellEvent != null) {
            cellEvent.removeFromParent();
            getGraph().removeCells(new Object[]{cellEvent}, true);
            cellEvent = null;
        }

        if (inputElement.getEventInputElementId() != null) {

            InputElement inputElementEvent = AppCore.app.config.getInputElement(inputElement.getEventInputElementId());
            if (cellEvent == null) {
                mxGeometry geo = new mxGeometry(50, 25, 20, 20);
                geo.setRelative(false);
                cellEvent = new InputCellEvent(inputElementEvent, "", geo, "");
                cellEvent.setId(this.getId() + "_event");
                cellEvent.setVertex(true);
                this.insert(cellEvent);
            }
            cellEvent.setInputElement(inputElementEvent);
        }
    }

    public void clearConstraints(){
        constraints = new ArrayList<>();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        InputCell var1 = (InputCell )super.clone();
        var1.cellProperties.addProperty(new CellProperty(PropertyFieldType.TextField, "Name", "Name", ""));
        var1.generateVisibleId();
        var1.setName(getName());
        var1.setInputElement(inputElement.clone());
        var1.clearConstraints();

        return var1;
    }
}
