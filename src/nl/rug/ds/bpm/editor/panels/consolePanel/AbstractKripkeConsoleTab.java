package nl.rug.ds.bpm.editor.panels.consolePanel;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.KripkeStructure;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark Kloosterhuis.
 */
public abstract class AbstractKripkeConsoleTab<T extends IJPanel> {
    protected JTabbedPane tabbedPanel;
    JPanel panel;
    Map<Integer, T> kripkeViews = new HashMap<>();

    public AbstractKripkeConsoleTab() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));


        tabbedPanel = new JTabbedPane();
        panel.add(tabbedPanel);


        EventSource.addListener(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, e -> {
            CreateTabs();
        });
        EventSource.addListener(EventType.KRIPKE_STRUCTURE_TAB_CHANGE, e -> {
            int index = (int) e;
            if(tabbedPanel.getTabCount() >index)
                tabbedPanel.setSelectedIndex(index);
        });
    }

    private void CreateTabs() {
        tabbedPanel.removeAll();

        for (KripkeStructure structure : AppCore.app.getKripkeStructures().values()) {
            tabbedPanel.addTab(structure.getName(), getStructureTab(structure));
        }
        //TODO REMOVE
    }

    private JPanel getStructureTab(KripkeStructure structure) {
        if (!kripkeViews.containsKey(structure.getId())) {
            kripkeViews.put(structure.getId(), CreateTab(structure));
        }
        return kripkeViews.get(structure.getId()).getPanel();

    }

    abstract T CreateTab(KripkeStructure structure);

    public JPanel getPanel() {
        return panel;
    }


}
