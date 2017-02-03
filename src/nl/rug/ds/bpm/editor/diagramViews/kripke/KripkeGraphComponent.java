package nl.rug.ds.bpm.editor.diagramViews.kripke;

import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.Main;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(Main.class.getResourceAsStream("/resources/cpn-style.xml"));
            codec.decode(doc.getDocumentElement(), graph.getStylesheet());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Sets the background to white
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.white);
    }
}
