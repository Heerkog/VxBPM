package nl.rug.ds.bpm.editor.diagramViews.cpn;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.GUIApplication;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.Converter;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.transformer.CPNGroup;
import nl.rug.ds.bpm.verification.models.cpn.Arc;
import nl.rug.ds.bpm.verification.models.cpn.ElementGeometry;
import nl.rug.ds.bpm.verification.models.cpn.Place;
import nl.rug.ds.bpm.verification.models.cpn.Transition;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mark Kloosterhuis.
 */
public class CPNview {
    CPNGraph graph;
    mxGraphOutline graphOutline;
    CPNGraphComponent graphComponent;
    GUIApplication guiApplication;
    HashMap<UUID, Object> vertexObjects = new HashMap<>();
    int currentTab = 0;

    public CPNview() {
        this.guiApplication = AppCore.gui;
        graph = new CPNGraph();

        graph.setEnabled(true);
        graph.setHtmlLabels(true);
        graph.setGridEnabled(false);


        mxGraphModel model = (mxGraphModel) graph.getModel();
        graphComponent = new CPNGraphComponent(graph);

        graphOutline = new mxGraphOutline(graphComponent);
        //graphComponent.setConnectable(false);
        //graphComponent.setLocation(new Point(50, 50));

        EventSource.addListener(EventType.EDITOR_TABVIEW_CHANGED, e -> {
            currentTab = (int) e;
            if (currentTab == 1) {
                renderGraph();
            }
        });
        EventSource.addListener(EventType.CPN_CONVERTED, e -> {
            if (currentTab == 1)
                renderGraph();
        });
        CPNview view = this;
        MouseWheelListener wheelTracker = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline
                        || e.isControlDown()) {
                    view.mouseWheelMoved(e);
                }
            }

        };

        // Handles mouse wheel events in the outline and graph component
        graphOutline.addMouseWheelListener(wheelTracker);
        graphComponent.addMouseWheelListener(wheelTracker);

    }

    private HashMap<String, mxCell> groupObjects;
    private boolean showBpmnBorder = false;
    private mxHierarchicalLayout layout;
    private int vSpacing = 40, hSpacing = 50;

    public void showBpmnBorder(boolean show) {
        showBpmnBorder = show;
        renderGraph();
    }

    public void setIntraCellSpacing(int spacing) {
        vSpacing = spacing;
        renderGraph();
    }

    public void setInterRankCellSpacing(int spacing) {
        hSpacing = spacing;
        renderGraph();
    }


    private void renderGraph() {
        try {
            Converter converter = AppCore.app.converter;


            vertexObjects = new HashMap<>();

            groupObjects = new HashMap<>();
            ArrayList<mxCell> edgeObjects = new ArrayList<>();

            if (converter.inputCells.size() > 0) {

                try {
                    graph.getModel().beginUpdate();
                    ((mxGraphModel) graph.getModel()).clear();
                    Object parent = graph.getDefaultParent();


                    for (CPNGroup p : converter.groups) {
                        if (p.elements.size() > 0) {
                            Object v = graph.insertVertex(parent, null, p.originCell.getValue(), (int) p.left, (int) p.top, p.right - p.left, p.bottom - p.top, "foldable=0;");
                            groupObjects.put(p.originCell.getId(), (mxCell) v);
                            graph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM, new Object[]{v});
                            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, showBpmnBorder ? "#000" : "#FFF", new Object[]{v});
                            graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, showBpmnBorder ? "#000000" : "#FFFFFF", new Object[]{v});

                        }

                    }


                    for (Arc t : converter.cpn.getArcs()) {
                        String bpmnSourceId = t.getSource().getConvertSourceId();
                        String bpmnTargetId = t.getTarget().getConvertSourceId();
                        if (bpmnSourceId != bpmnTargetId && groupObjects.containsKey(bpmnSourceId) && groupObjects.containsKey(bpmnTargetId)) {
                            mxCell mxEdge = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, "", groupObjects.get(bpmnSourceId), groupObjects.get(bpmnTargetId), "fontSize=8");
                            edgeObjects.add(mxEdge);
                        }
                    }
                    // Object cell = graph.getDefaultParent();
                    // new mxStackLayout(graph).execute(cell);


                    //    mxCoordinateAssignment.test = true;
                    layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
                    layout.setIntraCellSpacing(vSpacing);
                    layout.setInterRankCellSpacing(hSpacing);
                    Object cell = graph.getDefaultParent();

                    layout.execute(cell);

          /*  } finally {
                graph.getModel().endUpdate();
                graph.refresh();
            }

            try {*/
                    //Object parent = graph.getDefaultParent();
                    for (mxCell edge : edgeObjects) {
                        edge.removeFromParent();
                    }

                    for (CPNGroup g : converter.groups) {
                        Object parentGroup = groupObjects.get(g.originCell.getId());
                        drawElement(parentGroup, g);
                        for (CPNGroup sg : g.incomingSubGroups) {
                            drawElement(parentGroup, sg);
                        }
                        for (CPNGroup sg : g.outgoingSubGroups) {
                            drawElement(parentGroup, sg);
                        }
                    }

                    for (Arc t : converter.cpn.getArcs()) {
                        mxCell source = (mxCell) vertexObjects.get(t.getSource().getuUId());
                        mxCell target = (mxCell) vertexObjects.get(t.getTarget().getuUId());
                        // mxCell egde = (mxCell) graph.insertEdge(parent, null, "", source, target,
                        //      (t.getX() == null ? "noEdgeStyle=true" : "") + ";fontSize=8;");//,"edgeStyle=entityRelationEdgeStyle" +

                        mxCell egde = (mxCell) graph.insertEdge(parent, null, "", source, target, "fontSize=8;" + (t.getX() == null ? "noEdgeStyle=true" : ""));


                        String edgeLabel = t.getCondition().replace("&&", "&&\n");
                        if (t.getWeight() != 1)
                            edgeLabel += " (" + t.getWeight() + ")";
                        egde.setValue(edgeLabel);
                        if (t.getX() != null) {
                            List<mxPoint> points = new ArrayList<mxPoint>();
                            points.add(new mxPoint((double) t.getX(), (double) t.getY() - 15));
                            mxGeometry geo = egde.getGeometry();
                            geo.setPoints(points);
                            egde.setGeometry(geo);
                        }
                    }
                } finally {
                    graph.getModel().endUpdate();
                    graph.refresh();
                    graph.repaint();
                }
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }

    private void drawElement(Object parentGroup, CPNGroup g) {
        for (ElementGeometry e : g.elements) {
            if (e instanceof Place) {

                Place p = (Place) e;
                Object v = graph.insertVertex(parentGroup, null, p.getId(), (int) p.getX() + 5, (int) p.getY() + g.top + 5, p.getWidth(), p.getHeight(), "shape=ellipse;fontSize=8");
                vertexObjects.put(p.getuUId(), v);
                mxCell cell = (mxCell) v;
                mxGeometry geo = ((mxCell) v).getGeometry();
                //   geo.setRelative(true);
                cell.setGeometry(geo);
            }
            if (e instanceof Transition) {
                Transition t = (Transition) e;
                Object v = graph.insertVertex(parentGroup, null, t.getId(), (int) t.getX() + 5, (int) t.getY() + g.top + 5, t.getWidth(), t.getHeight(), "fontSize=8.5;foldable=0");
                vertexObjects.put(t.getuUId(), v);
                mxCell cell = (mxCell) v;
                mxGeometry geo = ((mxCell) v).getGeometry();
                // geo.setRelative(true);
                cell.setGeometry(geo);

                if (t.getParentIds().size() > 0) {
                    mxCell targetLabel = new mxCell(cell, new mxGeometry(0, 1, 30, 30), "shape=;verticalAlign=top;fontSize=8;");
                    targetLabel.setValue(String.join("\n", t.getParentIds()));
                    targetLabel.getGeometry().setRelative(true);
                    targetLabel.setConnectable(false);
                    targetLabel.setVertex(true);
                    cell.insert(targetLabel);
                }


            }
        }
    }

    public mxGraphComponent getView() {
        return graphComponent;
    }

    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphComponent.zoomIn();
        } else {
            graphComponent.zoomOut();
        }
    }
}
