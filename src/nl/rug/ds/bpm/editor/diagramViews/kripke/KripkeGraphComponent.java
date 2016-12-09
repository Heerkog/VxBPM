package nl.rug.ds.bpm.editor.diagramViews.kripke;

import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.core.configloader.Configloader;
import org.w3c.dom.Document;

import java.awt.*;

/**
 * Created by Mark Kloosterhuis.
 */
public class KripkeGraphComponent extends mxGraphComponent {

    /**
     *
     */
    private static final long serialVersionUID = -6833603133512882012L;

    /**
     * @param graph
     */
    public KripkeGraphComponent(mxGraph graph) {
        super(graph);

        // Sets switches typically used in an editor
        //setPageVisible(true);
        setGridVisible(false);
        setToolTips(true);
        getConnectionHandler().setCreateTarget(false);


        mxCodec codec = new mxCodec();
        Document doc = mxUtils.loadDocument(Configloader.resourcePath + "/cpn-style.xml");

        codec.decode(doc.getDocumentElement(), graph.getStylesheet());


        // Sets the background to white
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.white);
    }
}
