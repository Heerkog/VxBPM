package nl.rug.ds.bpm.editor.diagramViews.cpn;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.models.InputElement;

import java.util.List;

/**
 * Created by Mark on 30-6-2015.
 */
public class CPNGraph extends mxGraph {
    private List<InputElement> inputElements;
    private mxGraphModel model;

    public CPNGraph() {
        super();
        model = (mxGraphModel) this.getModel();
    }

    @Override
    public boolean isCellSelectable(Object cell) {
        mxCell selectedCell = (mxCell)cell;
        if(!selectedCell.isEdge() && selectedCell.getParent().getId().equals("1"))
            return true;
        return false;
    }

}
