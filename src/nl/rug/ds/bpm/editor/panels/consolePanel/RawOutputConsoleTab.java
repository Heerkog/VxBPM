package nl.rug.ds.bpm.editor.panels.consolePanel;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.KripkeStructure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;

/**
 * Created by Mark Kloosterhuis.
 */
public class RawOutputConsoleTab extends AbstractKripkeConsoleTab<RawOutputConsoleTab.StructureConsoleTab> {

    public RawOutputConsoleTab() {
        super();
    }


    StructureConsoleTab CreateTab(KripkeStructure structure){
        return new StructureConsoleTab(structure.getId());
    }

    public class  StructureConsoleTab implements IJPanel {
        JScrollPane scrollPanel;
        JTextArea textArea;
        int structureId;
        JPanel panel;
        public StructureConsoleTab(int structureId){
            panel = new JPanel();
            panel.setLayout(new GridLayout(0, 1));
            panel.setBorder(new EmptyBorder(0, 0, 0, 0));

            this.structureId = structureId;

            textArea = new JTextArea();
            textArea.setEditable(false);
            DefaultCaret caret = (DefaultCaret) textArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            textArea.setCaretPosition(textArea.getDocument().getLength());
            scrollPanel = new JScrollPane();
            scrollPanel.add(textArea);
            scrollPanel.setViewportView(textArea);
            EventSource.addListener(EventType.CONSOLE_RAWOUTPUT, (e) -> {
                if((int)e == this.structureId){
                    getKripkeModelString();
                }
            });
            panel.add(scrollPanel);
        }
        private void getKripkeModelString(){
            KripkeStructure structure = AppCore.app.getKripkeStructures().get(this.structureId);
            try {
                textArea.setText(structure.getRawOutput());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public JPanel getPanel() {
            return panel;
        }
    }
}
