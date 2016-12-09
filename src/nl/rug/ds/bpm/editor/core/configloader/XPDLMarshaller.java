package nl.rug.ds.bpm.editor.core.configloader;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNService;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import org.wfmc._2009.xpdl2.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Kloosterhuis.
 */
public class XPDLMarshaller {
    private ObjectFactory of;
    private PackageType packageType;
    private ProcessType pt;
    private BPMNService bpmnService;
    private Pool pool;
    public static int PoolOffsetLeft = 60;

    public XPDLMarshaller() {
        of = new ObjectFactory();
        packageType = of.createPackageType();
        bpmnService = AppCore.app.getBpmnService();


        packageType.setArtifacts(new Artifacts());
        pool = createPool();
        List<InputCell> laneCells = bpmnService.getByName(BPMNService.SwimLane);
        laneCells = laneCells.stream().sorted((l2, l1) -> Integer.compare((int) l2.getGeometry().getY(), (int) l1.getGeometry().getY())).collect(Collectors.toList());


        for (InputCell element : laneCells) {
            createLane(element);
        }
        createProcessStructure();
    }

    private void createProcessStructure() {


        WorkflowProcesses wfps = of.createWorkflowProcesses();
        packageType.setWorkflowProcesses(wfps);

        pt = of.createProcessType();
        pt.setId("mainWorkflowProcess");
        wfps.getWorkflowProcess().add(pt);

        createHeader();
        createGroups();
        createActivities();
        createTransitions();
    }

    private void createHeader() {
        packageType.setPackageHeader(of.createPackageHeader());

        XPDLVersion version = of.createXPDLVersion();
        version.setValue("2.2");
        packageType.getPackageHeader().setXPDLVersion(version);
    }

    private void createGroups() {
        for (InputCell element : bpmnService.getByName(BPMNService.Group)) {
            Artifact group = createArtifact(element);
            group.setGroup(of.createGroup());

            group.getGroup().setId(element.getId());
        }
    }

    private void createActivities() {
        pt.setActivities(of.createActivities());


        for (InputCell element : bpmnService.getByName(BPMNService.StartEvent)) {
            Activity activity = createActivity(element);
            Event event = of.createEvent();
            event.setStartEvent(of.createStartEvent());
            activity.setEvent(event);
        }

        for (InputCell element : bpmnService.getByName(BPMNService.EndEvent)) {
            Activity activity = createActivity(element);
            Event event = of.createEvent();
            event.setEndEvent(of.createEndEvent());
            activity.setEvent(event);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.Activity)) {
            Activity activity = createActivity(element);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.ActivityLoopWhile)) {
            Activity activity = createActivity(element);
            activity.setLoop(of.createLoop());
        }
        for (InputCell element : bpmnService.getByName(BPMNService.ActivityLoopRepeat)) {
            Activity activity = createActivity(element);
            Loop loop = of.createLoop();
            loop.setLoopStandard(of.createLoopStandard());
            activity.setLoop(loop);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.ActivityMiVariant)) {
            Activity activity = createActivity(element);
            Loop loop = of.createLoop();
            loop.setLoopMultiInstance(of.createLoopMultiInstance());
            activity.setLoop(loop);
        }

        for (InputCell element : bpmnService.getByName(BPMNService.ActivityEventError)) {
            Activity activity = createActivity(element);
            Event event = of.createEvent();
            IntermediateEvent iEvent = new IntermediateEvent();
            iEvent.setTrigger("error");
            event.setIntermediateEvent(iEvent);
            activity.setEvent(event);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.ActivityEventCompensation)) {
            Activity activity = createActivity(element);
            Event event = of.createEvent();
            IntermediateEvent iEvent = new IntermediateEvent();
            iEvent.setTrigger("Compensation");
            event.setIntermediateEvent(iEvent);
            activity.setEvent(event);
        }


        //Gates
        for (InputCell element : bpmnService.getByName(BPMNService.Exclusive)) {
            Activity activity = createActivity(element);
            ExtendedAttributes attributes = new ExtendedAttributes();
            Route route = of.createRoute();
            route.setGatewayType("Exclusive");
            activity.setRoute(route);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.Parallel)) {
            Activity activity = createActivity(element);
            Route route = of.createRoute();
            route.setGatewayType("Parallel");
            activity.setRoute(route);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.Deferred)) {
            Activity activity = createActivity(element);
            Route route = of.createRoute();
            route.setGatewayType("Deferred");
            activity.setRoute(route);
        }
        for (InputCell element : bpmnService.getByName(BPMNService.Complex)) {
            Activity activity = createActivity(element);
            Route route = of.createRoute();
            route.setGatewayType("Complex");
            activity.setRoute(route);
        }
    }


    private void createTransitions() {
        pt.setTransitions(of.createTransitions());
        for (EdgeCell edge : bpmnService.getEdges()) {
            if (!(edge instanceof ConstrainEdgeCell)) {
                Transition transition = of.createTransition();
                if (edge.getSource() != null)
                    transition.setFrom(((SuperCell) edge.getSource()).getId());
                if (edge.getTarget() != null)
                    transition.setTo(((SuperCell) edge.getTarget()).getId());
                transition.setId(edge.getId());
                transition.setName(edge.getVisibleId());
                transition.setConnectorGraphicsInfos(of.createConnectorGraphicsInfos());
                ConnectorGraphicsInfo gfxInfo = of.createConnectorGraphicsInfo();


                mxGeometry geo = edge.getGeometry();
                if (geo.getPoints() != null) {
                    for (mxPoint point : geo.getPoints()) {
                        Coordinates coords = of.createCoordinates();
                        coords.setXCoordinate(point.getX());
                        coords.setYCoordinate(point.getY());
                        gfxInfo.getCoordinates().add(coords);
                    }
                }

                transition.getConnectorGraphicsInfos().getConnectorGraphicsInfo().add(gfxInfo);
                pt.getTransitions().getTransition().add(transition);
            }
        }
    }

    private Pool createPool() {
        Pools pools = of.createPools();
        Pool pool = of.createPool();

        pool.setNodeGraphicsInfos(of.createNodeGraphicsInfos());
        NodeGraphicsInfo gfxInfo2 = of.createNodeGraphicsInfo();
        Coordinates coords2 = of.createCoordinates();

        coords2.setXCoordinate(0.0);
        coords2.setYCoordinate(0.0);
        gfxInfo2.setCoordinates(coords2);
        pool.getNodeGraphicsInfos().getNodeGraphicsInfo().add(gfxInfo2);


        pool.setBoundaryVisible(true);
        pool.setId("mainPool");
        pool.setProcess("mainWorkflowProcess");
        pools.getPool().add(pool);
        packageType.setPools(pools);
        Lanes lanes = of.createLanes();
        pool.setLanes(lanes);
        return pool;
    }

    private Lane createLane(InputCell element) {
        Lane lane = of.createLane();
        lane.setId(element.getId());
        lane.setName(element.getName());


        lane.setNodeGraphicsInfos(of.createNodeGraphicsInfos());
        NodeGraphicsInfo gfxInfo = of.createNodeGraphicsInfo();
        Coordinates coords = of.createCoordinates();

        mxGeometry geo = element.getGeometry();
        coords.setXCoordinate(geo.getX());
        coords.setYCoordinate(geo.getY());
        gfxInfo.setCoordinates(coords);

        gfxInfo.setWidth(geo.getWidth());
        gfxInfo.setHeight(geo.getHeight());
        lane.getNodeGraphicsInfos().getNodeGraphicsInfo().add(gfxInfo);
        pool.getLanes().getLane().add(lane);
        return lane;
    }

    private Artifact createArtifact(InputCell element) {
        Artifact artifact = of.createArtifact();
        artifact.setArtifactType("Group");
        artifact.setId(element.getId());
        artifact.setName(element.getName());
        double y = element.getGeoAbsoluteY();
        double x = element.getGeoAbsoluteX();
        x += PoolOffsetLeft;

        artifact.setNodeGraphicsInfos(of.createNodeGraphicsInfos());
        NodeGraphicsInfo gfxInfo = of.createNodeGraphicsInfo();
        Coordinates coords = of.createCoordinates();

        mxGeometry geo = element.getGeometry();
        coords.setXCoordinate(x);
        coords.setYCoordinate(y);
        gfxInfo.setCoordinates(coords);

        gfxInfo.setWidth(geo.getWidth());
        gfxInfo.setHeight(geo.getHeight());
        artifact.getNodeGraphicsInfos().getNodeGraphicsInfo().add(gfxInfo);

        packageType.getArtifacts().getArtifactAndAny().add(artifact);

        return artifact;
    }

    private Activity createActivity(InputCell element) {
        Activity activity = of.createActivity();
        activity.setId(element.getId());
       // if (element.getName() == null || element.getName().isEmpty())
       //     activity.setName(element.getVisibleId());
      //  else
            activity.setName(element.getName());


        double y = element.getGeoAbsoluteY();
        double x = element.getGeoAbsoluteX();
        x += PoolOffsetLeft;


        activity.setNodeGraphicsInfos(of.createNodeGraphicsInfos());
        NodeGraphicsInfo gfxInfo = of.createNodeGraphicsInfo();
        Coordinates coords = of.createCoordinates();

        mxGeometry geo = element.getGeometry();
        coords.setXCoordinate(x);
        coords.setYCoordinate(y);
        gfxInfo.setCoordinates(coords);

        gfxInfo.setWidth(geo.getWidth());
        gfxInfo.setHeight(geo.getHeight());
        activity.getNodeGraphicsInfos().getNodeGraphicsInfo().add(gfxInfo);


        pt.getActivities().getActivity().add(activity);
        return activity;
    }

    public void Marshall(File file) {
        try {
            pt.setAccessLevel("PUBLIC");
            pt.setName(file.getName());
            pt.setId("mainWorkflowProcess");

            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Marshaller marshaller = context.createMarshaller();

            JAXBElement<PackageType> xmlRoot = of.createPackage(packageType);
            marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
            marshaller.marshal(xmlRoot, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
