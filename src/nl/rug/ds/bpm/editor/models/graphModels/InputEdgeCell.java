package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.InputElement;

/**
 * Created by Mark Kloosterhuis.
 */


public class InputEdgeCell extends InputCell implements java.io.Serializable {

    private Boolean dashed = false;
    private mxCell labelCell;
    private Object labelCellObject;


    public InputEdgeCell(InputElement inputElement, Object var1, mxGeometry var2, String var3) {
        super(inputElement, var1, var2, null);
        this.setEdge(true);
        setStyle(mxConstants.STYLE_SHAPE, "");
        setValue("");
    }


    public void setDashed(Boolean dashed) {
        this.dashed = !this.dashed;

    }

    public static InputEdgeCell generateCell(BPMNGraph graph, InputElement inputElement) {
        String genId = inputElement.getGenId();
        genId = generateVisibleId(graph,genId);
        mxGeometry geometry = new mxGeometry(0, 0, 100, 100);
        geometry.setTerminalPoint(new mxPoint(0, 100), true);
        geometry.setTerminalPoint(new mxPoint(100, 0), false);
        geometry.setRelative(true);
        InputEdgeCell edge =  new InputEdgeCell(inputElement, "", geometry, "entity;edgeStyle=elbowEdgeStyle;orthogonal=false;fontSize=8;");
        edge.setVisibleId(genId);
        return edge;

    }


    public void setInputElement(InputElement inputElement) {
        //   this.inputElement = inputElement;
        //   setStyle(mxConstants.STYLE_RESIZABLE, inputElement.getResizable().toString());
        //   setStyle(mxConstants.STYLE_SHAPE, inputElement.getId());
        inputElement.getCustomStyleProperties().forEach((key, value) ->
                setStyle(key, value)
        );
        cellProperties.removeCustomProperties();
        this.setEdge(true);
        setStyle(mxConstants.STYLE_SHAPE, "");


    }


}
