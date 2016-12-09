package nl.rug.ds.bpm.editor.panels.consolePanel;

import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * Created by Mark Kloosterhuis.
 */
public class RawOutputTabOLD {
    JTextArea textArea;
    JScrollPane scrollPanel;

    public RawOutputTabOLD() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        scrollPanel = new JScrollPane();
        scrollPanel.add(textArea);
        scrollPanel.setViewportView(textArea);
        EventSource.addListener(EventType.CONSOLE_RAWOUTPUT, (e) -> {
            try {
                textArea.setText((String) e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public JScrollPane getPanel() {
        return scrollPanel;
    }
}
