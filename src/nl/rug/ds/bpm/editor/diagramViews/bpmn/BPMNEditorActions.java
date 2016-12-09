package nl.rug.ds.bpm.editor.diagramViews.bpmn;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Mark Kloosterhuis.
 */
public class BPMNEditorActions {
    public static BPMNview BPMNview;

    /**
     *
     */
    @SuppressWarnings("serial")
    public static class HistoryAction extends AbstractAction {
        /**
         *
         */
        protected boolean undo;

        /**
         *
         */
        public HistoryAction(boolean undo) {
            this.undo = undo;
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {

            if (BPMNview != null) {
                if (undo) {
                    BPMNview.getUndoManager().undo();
                } else {
                    BPMNview.getUndoManager().redo();
                }
            }
        }
    }
}
