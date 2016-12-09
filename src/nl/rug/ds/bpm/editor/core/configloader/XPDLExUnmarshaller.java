package nl.rug.ds.bpm.editor.core.configloader;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.jaxb.xpdlEx.*;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.Constraint;
import nl.rug.ds.bpm.editor.models.EdgeCellVariable;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import nl.rug.ds.bpm.verification.models.cpn.Variable;
import org.wfmc._2008.xpdl2.ConnectorGraphicsInfo;
import org.wfmc._2008.xpdl2.Coordinates;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class XPDLExUnmarshaller {
    private XpdlEx xpdlEx;
    BPMNGraph graph;

    @SuppressWarnings("unchecked")
    public XPDLExUnmarshaller(File file) {
        graph = AppCore.app.gui.getGraph();
        try {
            JAXBContext context = JAXBContext.newInstance(XpdlEx.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            xpdlEx = (XpdlEx) unmarshaller.unmarshal(file);
            try {
                importVariables();
                importTransitions();
                importConstraints();
                importAcitivityConstraints();
            } finally {
                graph.getModel().endUpdate();
                graph.refresh();
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void importVariables() {
        AppCore.app.getVariables().removeIf(v -> !v.isDefault());
        for (nl.rug.ds.bpm.editor.core.jaxb.xpdlEx.Variable element : xpdlEx.getVariables()) {
            AppCore.app.getVariables().add(new Variable(element.getId(), element.getName()));
        }
    }

    private void importTransitions() {
        try {
            List<EdgeCell> edgeCells = graph.getAllEdgeCells();
            for (Transition element : xpdlEx.getTransitions()) {
                SuperCell cell = graph.getById(element.getId());
                if (cell != null && EdgeCell.class.isInstance(cell)) {
                    EdgeCell edge = (EdgeCell) cell;
                    List<EdgeCellVariable> variablesValues = new ArrayList<>();
                    for (VariableValue value : element.getVariableValues()) {

                        variablesValues.add(new EdgeCellVariable(value.getVariableId(), value.getCondition(), value.getValue()));
                    }
                    edge.setVariableValues(variablesValues);
                }
            }
        }
        catch (Exception e) {
            e= e;
        }
    }

    private void importConstraints() {
        for (XPDLConstraint XPDLConstraint : xpdlEx.getXPDLConstraints()) {
            InputCell sourceCell = (InputCell) graph.getById(XPDLConstraint.getFrom());
            InputCell targetCell = (InputCell) graph.getById(XPDLConstraint.getTo());

            if (sourceCell != null && targetCell != null && AppCore.app.getConstraints().containsKey(XPDLConstraint.getConstraintId())) {
                Constraint constraint = AppCore.app.getConstraints().get(XPDLConstraint.getConstraintId());
                ConstrainEdgeCell cell = new ConstrainEdgeCell("arrow", new mxGeometry(), "entity;edgeStyle=elbowEdgeStyle;orthogonal=true");
                cell.setConstraint(constraint);

                cell.setEdge(true);
                mxCell edge = (mxCell) graph.addEdge(cell, graph.getDefaultParent(), sourceCell, targetCell, 0);
                List<ConnectorGraphicsInfo> xInfo = XPDLConstraint.getConnectorGraphicsInfos().getConnectorGraphicsInfo();
                List<mxPoint> points = new ArrayList<>();

                for (ConnectorGraphicsInfo info : xInfo) {
                    for (Coordinates c : info.getCoordinates()) {
                        double x = c.getXCoordinate();
                        double y = c.getYCoordinate();
                        points.add(new mxPoint(x, y));
                    }
                }
                edge.getGeometry().setPoints(points);

                List<EdgeCellVariable> variablesValues = new ArrayList<>();
                for (VariableValue value : XPDLConstraint.getVariableValues()) {
                    variablesValues.add(new EdgeCellVariable(value.getVariableId(), value.getCondition(), value.getValue()));
                }
                cell.setVariableValues(variablesValues);
            }
        }
    }

    private void importAcitivityConstraints() {
        for (ActivityConstraint activityConstraint : xpdlEx.getActivityConstraints()) {
            InputCell cell = (InputCell) graph.getById(activityConstraint.getActivityId());
            if (cell != null) {
                Constraint constraint = AppCore.app.getConstraints().get(activityConstraint.getConstraintId());
                if (constraint != null) {

                    InputCellConstraint constraintCell = cell.addConstraint();
                    constraintCell.setConstraint(constraint);

                    List<EdgeCellVariable> variablesValues = new ArrayList<>();
                    for (VariableValue value : activityConstraint.getVariableValues()) {
                        variablesValues.add(new EdgeCellVariable(value.getVariableId(), value.getCondition(), value.getValue()));
                    }
                    constraintCell.setVariablesValues(variablesValues);
                    cell.renderConstraints();

                }
            }
        }
    }

}
