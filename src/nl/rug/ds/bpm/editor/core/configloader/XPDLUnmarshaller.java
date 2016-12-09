package nl.rug.ds.bpm.editor.core.configloader;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.InputElement;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import org.wfmc._2009.xpdl2.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mark Kloosterhuis.
 */
public class XPDLUnmarshaller {
    private JAXBElement<PackageType> xmlRoot;
    BPMNGraph graph;
    Activities activities = null;
    List<Transition> transitions = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public XPDLUnmarshaller(File file) {

        graph = AppCore.app.gui.getGraph();
        graph.reset();
        String ext = null;
        int i = file.getName().lastIndexOf('.');

        if (i > 0 && i < file.getName().length() - 1)
            ext = file.getName().substring(i + 1).toLowerCase();


        try {
            EventSource.Enabled = false;
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setProperty("com.sun.xml.internal.bind.ObjectFactory", new ObjectFactory());

            xmlRoot = (JAXBElement<PackageType>) unmarshaller.unmarshal(file);
            importXPDL();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            EventSource.Enabled = true;
        }

    }

    public void importXPDL() {
        List<ProcessType> wfp = xmlRoot.getValue().getWorkflowProcesses().getWorkflowProcess();
        ProcessType pt = wfp.get(0);
        PackageType type = xmlRoot.getValue();
        type.getPools().getPool().forEach(p -> {
            p.getLanes().getLane().forEach(l -> importLanes(l));
        });
        type.getArtifacts().getArtifactAndAny().forEach(a -> {
            importGroup((Artifact) a);
        });

        xmlRoot.getValue().getWorkflowProcesses().getWorkflowProcess().forEach(wp -> {
            transitions.addAll(wp.getTransitions().getTransition());
        });

        xmlRoot.getValue().getWorkflowProcesses().getWorkflowProcess().forEach(wp -> {
            wp.getActivities().getActivity().forEach(a -> importActivitie(a));
        });
        transitions.forEach(t -> importTransition(t));

    }

    private void importGroup(Artifact artifact) {
        List<NodeGraphicsInfo> gfxInfo = artifact.getNodeGraphicsInfos().getNodeGraphicsInfo();

        double xPos = gfxInfo.get(0).getCoordinates().getXCoordinate();
        double yPos = gfxInfo.get(0).getCoordinates().getYCoordinate();
        xPos = Math.max(0, xPos - XPDLMarshaller.PoolOffsetLeft);

        double width = gfxInfo.get(0).getWidth();
        double height = gfxInfo.get(0).getHeight();
        InputElement element = AppCore.app.config.getInputElement("group");

        InputCell parent = (InputCell) AppCore.app.gui.getGraphComponent().getCellAt((int) xPos, (int) yPos, true);
        if (parent != null) {
            yPos -= parent.getGeoAbsoluteY();
            xPos -= parent.getGeoAbsoluteX();
        }

        String id = artifact.getId();
        InputCell cell = InputCell.generateCell(graph, element, id, (int) xPos, (int) yPos);
        cell.setName(element.getName());
        if (id.length() > 10)
            cell.generateVisibleId();
        else
            cell.setVisibleId(id);


        cell.getGeometry().setWidth(width);
        cell.getGeometry().setHeight(height);

        graph.addCell(cell);

        if (parent != null)
            parent.insert(cell);
    }

    private void importLanes(Lane lane) {
        List<NodeGraphicsInfo> gfxInfo = lane.getNodeGraphicsInfos().getNodeGraphicsInfo();

        double xPos = gfxInfo.get(0).getCoordinates().getXCoordinate();
        xPos = Math.max(0, xPos - XPDLMarshaller.PoolOffsetLeft);
        double yPos = gfxInfo.get(0).getCoordinates().getYCoordinate();
        double width = gfxInfo.get(0).getWidth();
        double height = gfxInfo.get(0).getHeight();
        InputElement element = AppCore.app.config.getInputElement("_swimlane");


        String id = element.getId();
        InputCell cell = InputCell.generateCell(graph, element, id, (int) xPos, (int) yPos);
        cell.setName(element.getName());
        cell.setId(lane.getId());
        cell.generateVisibleId();

        cell.getGeometry().setWidth(width);
        cell.getGeometry().setHeight(height);
        graph.addCell(cell);


    }

    private void importActivitie(Activity actvity) {

        List<NodeGraphicsInfo> gfxInfo = actvity.getNodeGraphicsInfos().getNodeGraphicsInfo();

        double xPos = gfxInfo.get(0).getCoordinates().getXCoordinate();
        xPos = Math.max(0, xPos - XPDLMarshaller.PoolOffsetLeft);
        double yPos = gfxInfo.get(0).getCoordinates().getYCoordinate();
        InputElement element = null;
        InputCell parent = (InputCell) AppCore.app.gui.getGraphComponent().getCellAt((int) xPos, (int) yPos, true);
        if (parent != null) {
            yPos -= parent.getGeoAbsoluteY();
            xPos -= parent.getGeoAbsoluteX();
        }


        int incomingCount = (int) transitions.stream().filter(t -> t.getTo().equals(actvity.getId())).count();
        int outcoingCount = (int) transitions.stream().filter(t -> t.getFrom().equals(actvity.getId())).count();
        if (actvity.getRoute() != null && actvity.getRoute().getGatewayType().equalsIgnoreCase("Parallel")) {
            if (incomingCount == 1)
                element = AppCore.app.config.getInputElement("ParallelFork");
            else
                element = AppCore.app.config.getInputElement("ParallelMerge");
        }
        //Inclusive
        else if (actvity.getRoute() != null && actvity.getRoute().getGatewayType().equalsIgnoreCase("Inclusive")) {
            element = AppCore.app.config.getInputElement("InclusiveMerge");
        }
        //Exclusive
        else if (actvity.getRoute() != null && actvity.getRoute().getGatewayType().equalsIgnoreCase("Exclusive")) {
            if (incomingCount == 1)
                element = AppCore.app.config.getInputElement("ExclusiveFork");
            else
                element = AppCore.app.config.getInputElement("ExclusiveMerge");
        } else if (actvity.getRoute() != null && actvity.getRoute().getGatewayType().equalsIgnoreCase("Complex")) {
            if (incomingCount == 1)
                element = AppCore.app.config.getInputElement("ComplexFork");
            else
                element = AppCore.app.config.getInputElement("ComplexMerge");
        } else if (actvity.getEvent() != null && actvity.getEvent().getStartEvent() != null) {
            element = AppCore.app.config.getInputElement("startEvent");
        } else if (actvity.getEvent() != null && actvity.getEvent().getEndEvent() != null) {
            element = AppCore.app.config.getInputElement("endEvent");
        } else if (actvity.getLoop() != null && actvity.getLoop().getLoopMultiInstance() != null) {
            element = AppCore.app.config.getInputElement("miVariant");
        } else if (actvity.getLoop() != null && actvity.getLoop().getLoopStandard() != null && !actvity.getLoop().getLoopType().equalsIgnoreCase("None")) {
            element = AppCore.app.config.getInputElement("loopRepeat");
        } else if (actvity.getLoop() != null && !actvity.getLoop().getLoopType().equalsIgnoreCase("None")) {
            element = AppCore.app.config.getInputElement("loopWhile");
        } else if (actvity.getEvent() != null && actvity.getEvent().getIntermediateEvent() != null && actvity.getEvent().getIntermediateEvent().getTrigger().equalsIgnoreCase("error")) {
            element = AppCore.app.config.getInputElement("activityError");
        } else if (actvity.getEvent() != null && actvity.getEvent().getIntermediateEvent() != null && actvity.getEvent().getIntermediateEvent().getTrigger().equalsIgnoreCase("Compensation")) {
            element = AppCore.app.config.getInputElement("activityIntermediateEventCompensation");
        } else {
            element = AppCore.app.config.getInputElement("activity");
        }


        if (element != null) {
            String id = actvity.getId();
            InputCell cell = InputCell.generateCell(graph, element, id, (int) xPos, (int) yPos);
            cell.setName(actvity.getName());


            if (id.length() > 10)
                cell.generateVisibleId();
            else
                cell.setVisibleId(id);

            graph.addCell(cell);

            if (parent != null)
                parent.insert(cell);

            cell.setId(id);
        } else {
            element = element;
        }

    }


    private void importTransition(Transition transition) {
        BPMNGraph graph = AppCore.gui.getGraph();

        InputCell sourceCell = (InputCell) graph.getById(transition.getFrom());
        InputCell targetCell = (InputCell) graph.getById(transition.getTo());


        if (sourceCell != null && targetCell != null) {
            EdgeCell cell = new EdgeCell(UUID.randomUUID().toString(), new mxGeometry(), "entity;edgeStyle=elbowEdgeStyle;orthogonal=false;fontSize=8;;editable=false");
            cell.setId(transition.getId());
            cell.setVisibleId(transition.getId());
            cell.setEdge(true);
            mxCell edge = (mxCell) graph.addEdge(cell, graph.getDefaultParent(), sourceCell, targetCell, 0);
            edge.setId(transition.getId());
            List<ConnectorGraphicsInfo> xInfo = transition.getConnectorGraphicsInfos().getConnectorGraphicsInfo();
            List<mxPoint> points = new ArrayList<>();
            cell.setVariableValues(new ArrayList<>());
            for (ConnectorGraphicsInfo info : xInfo) {
                for (Coordinates c : info.getCoordinates()) {
                    double x = c.getXCoordinate();
                    double y = c.getYCoordinate();
                    points.add(new mxPoint(x, y));
                }
            }


            edge.getGeometry().setPoints(points);
        }
    }
}
