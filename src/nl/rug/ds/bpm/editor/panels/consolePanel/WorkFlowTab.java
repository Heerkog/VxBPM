package nl.rug.ds.bpm.editor.panels.consolePanel;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * Created by Mark Kloosterhuis.
 */
public class WorkFlowTab {
    JTextArea textArea;
    JScrollPane scrollPanel;

    public WorkFlowTab() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        scrollPanel = new JScrollPane();
        scrollPanel.add(textArea);
        scrollPanel.setViewportView(textArea);
        EventSource.addListener(EventType.CONSOLE_WORKFLOW, (e) -> setText());
        EventSource.addListener(EventType.CONSOLE_FULLOUTPUT_CHANGED, (e) -> setText());
    }

    private void setText() {
        try {
            textArea.setText(AppCore.app.converter.cpn.toString(AppCore.gui.toolbar.isFullOutput()));
        } catch (Exception ex) {
            Exception ex2 = ex;
        }
    }

    public JScrollPane getPanel() {
        return scrollPanel;
    }
}
