package nl.rug.ds.bpm.editor.diagramViews.kripke;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.models.InputElement;

import java.util.List;

/**
 * Created by Mark on 30-6-2015.
 */
public class KripkeGraph extends mxGraph {
    private List<InputElement> inputElements;
    private mxGraphModel model;

    public KripkeGraph() {
        super();
        model = (mxGraphModel) this.getModel();
    }

   /* @Override
    public boolean isCellSelectable(Object cell) {
        return false;
    }*/

}
