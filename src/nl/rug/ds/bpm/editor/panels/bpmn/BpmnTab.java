package nl.rug.ds.bpm.editor.panels.bpmn;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxUtils;
import nl.rug.ds.bpm.editor.Main;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.configloader.Configloader;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNview;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.EditorPalette;
import nl.rug.ds.bpm.editor.models.PaletElement;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.CellPropertyPanel;
import nl.rug.ds.bpm.editor.panels.genericViews.GenericTab;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by Mark on 28-12-2015.
 */
public class BpmnTab extends GenericTab {
    BPMNview BPMNview;
    BPMNGraph graph;
    public EditorPalette editorPallete;
    CellPropertyPanel inputCellPopertyPanel;
    VariableTable variableTable;


    public BpmnTab() {

        BPMNview = new BPMNview(AppCore.gui);
        graph = BPMNview.getGraph();

        editorPallete = new EditorPalette(graph);
        this.variableTable = new VariableTable();
        createViews();
    }

    public void createPalet() {
        graph.setInputElements(AppCore.app.config.getInputElements());
        editorPallete.createInputShapes(graph.getInputElements());
        for (PaletElement element : AppCore.app.config.getPaletElements()) {
            editorPallete.addInputElement(element);
        }

        try {
            String nodeXml = mxUtils.readInputStream(Main.class.getResourceAsStream("/resources/inputElements/Start-Event-Link.shape"));
            mxStencilShape newShape = new mxStencilShape(nodeXml);
            mxGraphics2DCanvas.putShape("TESTLINK", newShape);
            editorPallete.addEdgeTemplate("Connector", new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/images/connect.png"))), "entity", 100, 100, "connector");
            editorPallete.addRelationEdgeTemplate("Constraint", new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/images/connect.png"))), "entity", 100, 100, "connector");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BPMNGraph getGraph() {
        return graph;
    }

    public BPMNview getBPMNView() {
        return BPMNview;
    }

    public VariableTable getVariableTable() {
        return variableTable;
    }

    public void fillLeftPanel() {
        JPanel libraryPanel = new JPanel();
        libraryPanel.setLayout(new GridLayout(0, 1));
        JSplitPane bpmnSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPanel, BPMNview.getGraphOutline());
        bpmnSplitPanel.setResizeWeight(.6d);
        libraryPanel.add(editorPallete);
        leftContainer.add(bpmnSplitPanel);
    }

    public void fillRightPanel() {
        inputCellPopertyPanel = new CellPropertyPanel();

        JTabbedPane Properties2 = new JTabbedPane();
        this.variableTable = new VariableTable();
        Properties2.addTab("Variables", variableTable.getPanel());

        EventSource.addListener(EventType.SELECTION_CHANGED, e -> {
            java.util.List<SuperCell> inputCells = AppCore.gui.getCellService().getSelectedCells();
            if (inputCells.size() == 0) {
                Properties2.remove(inputCellPopertyPanel.getPanel());
            } else if (inputCells.size() >= 1) {

                if (Properties2.getTabCount() == 1)
                    Properties2.addTab("Properties", inputCellPopertyPanel.getPanel());
                Properties2.setSelectedIndex(1);
            }
        });
        rightContainer.add(Properties2);
    }


    public void fillCenterPanel() {
        centerContainer.add(BPMNview.getView());
    }
}