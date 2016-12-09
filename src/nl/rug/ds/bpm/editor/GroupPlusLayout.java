package nl.rug.ds.bpm.editor;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;

/**
 * Created by Mark Kloosterhuis.
 */
public class GroupPlusLayout extends JFrame {

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;

    private Object vAB, vCD, vA, vB, vC, vD;

    public GroupPlusLayout() {
        super("GroupPlusLayout");
        graph = new mxGraph();

        graph.getModel().beginUpdate();
        try {
            //mkGraphWithoutGroups();
            mkGraphWithGroups1();
        } finally {
            graph.getModel().endUpdate();
        }
        graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
        layoutGraph();
    }

    private void mkGraphWithoutGroups() {
        addVerticesWithoutGrouping();
        addEdges();
    }

    private void mkGraphWithGroups1() {
        addVerticesWithGrouping();
        addEdges();
        //graph.foldCells(true, true, new Object[]{vAB, vCD});
    }

    private void mkGraphWithGroups2() {
        addVerticesWithoutGrouping();
        addEdges();
        graph.groupCells(vAB, 1, new Object[]{vA, vB});
        graph.groupCells(vCD, 1, new Object[]{vC, vD});
        graph.foldCells(true, true, new Object[]{vAB, vCD});
    }

    private void addVerticesWithoutGrouping() {
        vAB = graph.insertVertex(graph.getDefaultParent(), "vAB", "vAB", 20, 20, 80, 300);
        vA = graph.insertVertex(graph.getDefaultParent(), "vA", "vA", 20, 120, 80, 30);
        vB = graph.insertVertex(graph.getDefaultParent(), "vB", "vB", 120, 120, 80, 30);
        vCD = graph.insertVertex(graph.getDefaultParent(), "vCD", "vCD", 220, 20, 80, 30);
        vC = graph.insertVertex(graph.getDefaultParent(), "vC", "vC", 220, 120, 80, 30);
        vD = graph.insertVertex(graph.getDefaultParent(), "vD", "vD", 320, 120, 80, 30);
    }

    private void addVerticesWithGrouping() {
        vAB = graph.insertVertex(graph.getDefaultParent(), "vAB", "vAB", 20, 20, 80, 30);
        vA = graph.insertVertex(vAB, "vA", "vA", 20, 120, 80, 30);
        vB = graph.insertVertex(vAB, "vB", "vB", 120, 120, 80, 30);
        vCD = graph.insertVertex(graph.getDefaultParent(), "vCD", "vCD", 220, 20, 80, 30);
        vC = graph.insertVertex(vCD, "vC", "vC", 220, 120, 80, 30);
        vD = graph.insertVertex(vCD, "vD", "vD", 320, 120, 80, 30);
    }

    private void addEdges() {
        graph.insertEdge(graph.getDefaultParent(), null, "", vAB, vA);
        graph.insertEdge(graph.getDefaultParent(), null, "", vAB, vB);
        graph.insertEdge(graph.getDefaultParent(), null, "", vCD, vC);
        graph.insertEdge(graph.getDefaultParent(), null, "", vCD, vD);
        graph.insertEdge(graph.getDefaultParent(), null, "", vB, vC);
    }

    private void layoutGraph() {
        mxGraphLayout layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
        Object cell = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            layout.execute(cell);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public static void main(String[] args) {
        GroupPlusLayout frame = new GroupPlusLayout();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }
}
