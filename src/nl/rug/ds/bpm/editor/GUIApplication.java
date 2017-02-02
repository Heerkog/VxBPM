package nl.rug.ds.bpm.editor;

import com.mxgraph.swing.mxGraphComponent;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNview;
import nl.rug.ds.bpm.editor.panels.bpmn.BpmnTab;
import nl.rug.ds.bpm.editor.panels.consolePanel.*;
import nl.rug.ds.bpm.editor.panels.cpn.CpnTab;
import nl.rug.ds.bpm.editor.panels.kripke.KripkeTab;
import nl.rug.ds.bpm.editor.services.CellService;
import nl.rug.ds.bpm.editor.services.ConstraintService;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mark Kloosterhuis.
 */
public class GUIApplication {
    public JFrame frame;
    public ToolBar toolbar;
    public ConsoleTab consoleTab;
    public BpmnTab bpmnTab;
    public CpnTab cpnTab;
    public KripkeTab kripkeTab;
    public CellService cellService;
    private ConstraintService constraintService;

    public GUIApplication() {
        AppCore.gui = this;
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        //UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        frame = new JFrame();
        JTabbedPane tabbedPane = new JTabbedPane();


        bpmnTab = new BpmnTab();

        cellService = new CellService(bpmnTab.getGraph());
        constraintService = new ConstraintService(bpmnTab.getGraph());


        cpnTab = new CpnTab();
        kripkeTab = new KripkeTab();
        JTabbedPane consoleTabPanel = createStatusBar();
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, consoleTabPanel);

        verticalSplit.setResizeWeight(.85d);
        verticalSplit.setDividerSize(3);
        toolbar = new ToolBar(this);
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(verticalSplit, BorderLayout.CENTER);


        tabbedPane.add("BPMN", bpmnTab.getPanel());
        tabbedPane.add("CPN", cpnTab.getPanel());
        tabbedPane.add("Kripke", kripkeTab.getPanel());
        bpmnTab.createPalet();
        tabbedPane.addChangeListener(e -> {
            EventSource.fireEvent(EventType.EDITOR_TABVIEW_CHANGED, tabbedPane.getSelectedIndex());
        });

    }

    public BPMNGraph getGraph() {
        return bpmnTab.getGraph();
    }

    public BPMNview getBPMNView() {
        return bpmnTab.getBPMNView();
    }

    public mxGraphComponent getGraphComponent() {
        return bpmnTab.getBPMNView().graphComponent;
    }

    public CellService getCellService() {
        return cellService;
    }

    public ConstraintService getConstraintService() {
        return constraintService;
    }

    public JFrame getFrame() {
        return frame;
    }


    protected JTabbedPane createStatusBar() {
        JTabbedPane tabbedPane = new JTabbedPane();
        consoleTab = new ConsoleTab();

        WorkFlowTab workFlowTab = new WorkFlowTab();
        KripkeStructureConsoleTab kripkeTab = new KripkeStructureConsoleTab();
        RawInputConsoleTab rawInputTab = new RawInputConsoleTab();
        VariablesConsoleTab variablesConsoleTab = new VariablesConsoleTab();

        RawOutputConsoleTab rawOutputTab = new RawOutputConsoleTab();
        ConstraintsConsoleTab constraintsConsoleTab = new ConstraintsConsoleTab();
        tabbedPane.addTab("Console", consoleTab.getPanel());
        tabbedPane.addTab("WorkFlow", workFlowTab.getPanel());
        tabbedPane.addTab("Variables", variablesConsoleTab.getPanel());
        tabbedPane.addTab("Kripke Structure", kripkeTab.getPanel());
        tabbedPane.addTab("Raw Input", rawInputTab.getPanel());
        tabbedPane.addTab("Raw Output", rawOutputTab.getPanel());
        tabbedPane.addTab("Verification results", constraintsConsoleTab.getPanel());
        tabbedPane.addChangeListener(e -> {
            EventSource.fireEvent(EventType.CONSOLE_TABVIEW_CHANGED, tabbedPane.getSelectedIndex());
        });

        return tabbedPane;
    }


}
