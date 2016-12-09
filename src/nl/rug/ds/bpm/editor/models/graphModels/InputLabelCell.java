package nl.rug.ds.bpm.editor.models.graphModels;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;

/**
 * Created by Mark on 22-1-2016.
 */
public class InputLabelCell extends mxCell implements java.io.Serializable {
    private String inputCellConstraintId;
    private String inputCellId;
    private mxCell shapeCell;

    public InputLabelCell(Object var1, mxGeometry var2, String var3, InputCellConstraint inputCellConstraint) {
        super(var1, var2, var3);
        inputCellConstraintId = inputCellConstraint.getId();
        inputCellId = inputCellConstraint.getInputCellId();



        /*shapeCell = (mxCell) AppCore.gui.getGraph().insertVertex(this, "shapeCell", "", 20, 20, 10, 10, "shape=TESTSHAPE;");
        shapeCell.setConnectable(false);
        shapeCell.setVertex(true);
        shapeCell.setGeometry(new mxGeometry(-30, 0, 30, 8));
        shapeCell.getGeometry().setRelative(true);*/


    }

    public void setBold(boolean bold) {
        AppCore.gui.getGraph().setCellStyles(mxConstants.STYLE_FONTSTYLE, bold ? "1" : "0", new Object[]{this});
    }

    public InputCell getInputCell() {
        return AppCore.gui.getCellService().getCell(InputCell.class, this.inputCellId);
    }
}
