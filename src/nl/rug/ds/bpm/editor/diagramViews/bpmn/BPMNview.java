package nl.rug.ds.bpm.editor.diagramViews.bpmn;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.*;
import nl.rug.ds.bpm.editor.GUIApplication;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.InputElement;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class BPMNview {
    BPMNGraph graph;
    mxGraphOutline graphOutline;
    public BPMNGraphComponent graphComponent;
    GUIApplication guiApplication;
    protected mxUndoManager undoManager;
    int i = 0;
    boolean constraintResultsSelected = false;
    int selectedKripkeIndex;

    public BPMNview(GUIApplication guiApplication) {
        this.guiApplication = guiApplication;
        undoManager = new mxUndoManager();

        graph = new BPMNGraph();
        graph.setGridEnabled(true);
        graph.setDisconnectOnMove(false);
        graph.getSelectionModel().addListener(mxEvent.CHANGE, (s, e) ->
                EventSource.fireEvent(EventType.SELECTION_CHANGED, (Object) null)
        );
        graph.addListener(mxEvent.CELLS_ADDED, (s, e) ->
                EventSource.fireEvent(EventType.BPMN_CHANGED, (Object) null)
        );
        graph.addListener(mxEvent.CELLS_REMOVED, (s, e) ->
                EventSource.fireEvent(EventType.BPMN_CHANGED, (Object) null)
        );
        graph.addListener(mxEvent.CELL_CONNECTED, (s, e) ->
                EventSource.fireEvent(EventType.BPMN_CHANGED, (Object) null)
        );

        EventSource.addListener(EventType.BPMN_REDRAW, e -> {
                    graph.getAllSuperCells().forEach(cell -> {
                        cell.updateLayout();
                    });
                    graph.refresh();
                }
        );


        /*EventSource.addListener(EventType.KRIPKE_CONSOLE_TABVIEW_CHANGED, e -> {
            selectedKripkeIndex = (Integer) e;
            //drawValidation();
        });*/
        EventSource.addListener(EventType.SELECTION_CHANGED, e -> {
            setConstraintLabelSelected(prevSelectedId, false);
            try {
                BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);

//                ImageIO.write(image, "PNG", new File("C:\\Temp\\BPMN.png"));
            }
            catch(Exception ex) {

            }
        });


        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);


        graphComponent = new BPMNGraphComponent(graph);

        graphComponent.setConnectable(false);
        graphOutline = new mxGraphOutline(graphComponent);

        // Keeps the selection in sync with the command history
        mxEventSource.mxIEventListener undoHandler = (s, e) -> {
            List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) e.getProperty("edit")).getChanges();
            graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
        };
        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);


        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                    /*if (cell == null) {

                        try {
                            new mxHierarchicalLayout(graph, SwingConstants.WEST).execute(graph.getDefaultParent());
                        } finally {
                            graph.getModel().endUpdate();
                            graph.refresh();
                        }
                    }*/

                    if (cell instanceof InputCell) {
                        InputCell inputCell = (InputCell) cell;
                        JPopupMenu menu = new JPopupMenu();
                        for (InputElement inputElement : inputCell.getInputElement().getPaletElement().getInputElements()) {
                            ImageIcon image = new ImageIcon(this.getClass().getResource("/nl/rug/editor/resources/inputElements/" + inputElement.getPaletIconPath()));

                            JMenuItem item = new JMenuItem(inputElement.getName(), image);
                            if (inputElement.getId().equals(inputCell.getInputElement().getId()))
                                item.setEnabled(false);
                            menu.add(item);
                            item.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    inputCell.setInputElement(inputElement);
                                    EventSource.fireEvent(EventType.SELECTION_CHANGED, (Object) null);
                                }
                            });
                        }
                        menu.show(graphComponent, e.getX(), e.getY());
                    }

                }
            }

        });
        BPMNview view = this;
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

    String prevSelectedId;

    public void setConstraintLabelSelected(String id, boolean selected) {
        if (prevSelectedId != null) {
            mxCell prevCell = (mxCell) ((mxGraphModel) graph.getModel()).getCell(prevSelectedId);
            if (prevCell != null)
                graph.setCellStyles(mxConstants.STYLE_FONTSTYLE, "0", new Object[]{prevCell});
        }

        if (id != null) {
            mxCell cell = (mxCell) ((mxGraphModel) graph.getModel()).getCell(id);
            if (cell != null)
                graph.setCellStyles(mxConstants.STYLE_FONTSTYLE, selected ? "1" : "0", new Object[]{cell});
        }
        prevSelectedId = id;

    }


    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphComponent.zoomIn();
        } else {
            graphComponent.zoomOut();
        }
    }

    protected mxEventSource.mxIEventListener undoHandler = new mxEventSource.mxIEventListener() {
        public void invoke(Object source, mxEventObject evt) {
            undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        }
    };

    public BPMNGraphComponent getView() {
        return graphComponent;
    }

    public BPMNGraph getGraph() {
        return graph;
    }

    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    public mxUndoManager getUndoManager() {
        return undoManager;
    }
}
