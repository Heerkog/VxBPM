package nl.rug.ds.bpm.editor.diagramViews.bpmn;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.Main;
import nl.rug.ds.bpm.editor.core.configloader.Configloader;
import nl.rug.ds.bpm.editor.models.graphModels.InputLabelCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Created by Mark Kloosterhuis.
 */
public class BPMNGraphComponent extends mxGraphComponent {

    /**
     *
     */
    private static final long serialVersionUID = -6833603133512882012L;

    /**
     * @param graph
     */
    public BPMNGraphComponent(mxGraph graph) {
        super(graph);

        // Sets switches typically used in an editor
        //setPageVisible(true);
        setGridVisible(true);
        setToolTips(true);
        //getConnectionHandler().setCreateTarget(true);


        mxCodec codec = new mxCodec();
        Document doc = mxUtils.loadDocument(Configloader.resourcePath + "/bpmn-style.xml");

        codec.decode(doc.getDocumentElement(), graph.getStylesheet());

        URL panningUrl = Main.class.getResource("/resources/images/green-dot.png");
        ImageIcon panningIcon = new ImageIcon(panningUrl);

        //this.getConnectionHandler().setConnectIcon(panningIcon);

        // Sets the background to white
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_LEFT:
                        translateSelectedCells(keyCode);
                        break;
                    case KeyEvent.VK_DELETE:
                        deleteSelectedCells();
                        break;
                }
            }
        });
    }

    @Override
    public void selectCellForEvent(Object cell, MouseEvent e) {
        if (cell instanceof InputLabelCell) {
            cell = (((InputLabelCell) cell).getInputCell());
        }
        super.selectCellForEvent(cell, e);
    }

    private void translateSelectedCells(int keyCode) {
        Object[] cells = this.getGraph().getSelectionCells();

        double dx = 0;
        double dy = 0;
        double gridSize = this.getGraph().getGridSize();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                dx = 0;
                dy = -gridSize;
                break;
            case KeyEvent.VK_DOWN:
                dx = 0;
                dy = gridSize;
                break;
            case KeyEvent.VK_LEFT:
                dx = -gridSize;
                dy = 0;
                break;
            case KeyEvent.VK_RIGHT:
                dx = gridSize;
                dy = 0;
                break;
        }

        this.graph.moveCells(cells, dx, dy);
    }

    private void deleteSelectedCells() {
        Object[] cells = this.getGraph().getSelectionCells();
        for(Object cell : cells) {
            if(SuperCell.class.isInstance(cell)) {
                SuperCell sCell = (SuperCell)cell;
                sCell.deleted = true;
            }
        }
        this.graph.removeCells(cells);
        if(cells.length>0)
        this.graph.getModel().remove(cells[0]);


    }

}
