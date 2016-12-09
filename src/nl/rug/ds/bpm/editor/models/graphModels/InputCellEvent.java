package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import nl.rug.ds.bpm.editor.models.InputElement;

/**
 * Created by Mark Kloosterhuis.
 */


public class InputCellEvent extends InputCell implements java.io.Serializable {


    public InputCellEvent(InputElement inputElement, Object var1, mxGeometry var2, String var3) {
        super(inputElement, var1, var2, null);

        setStyle(mxConstants.STYLE_SHAPE, "startEvent");
    }

    public boolean isCellSelectable() {
        return false;
    }

    @Override
    public boolean isValidTarget(Object edge, Object source) {
        return false;
    }

    @Override
    public boolean isValidSource(Object edge, Object target) {
        return true;
    }
}
