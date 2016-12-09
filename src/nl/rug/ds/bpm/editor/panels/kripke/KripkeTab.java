package nl.rug.ds.bpm.editor.panels.kripke;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.diagramViews.kripke.KripkeView;
import nl.rug.ds.bpm.editor.models.KripkeStructure;
import nl.rug.ds.bpm.editor.panels.genericViews.GenericTab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 28-12-2015.
 */
public class KripkeTab extends GenericTab {
    JTabbedPane tabbedPanel;
    Map<Integer, KripkeStructureTab> kripkeViews;
    KripkeStructuresTable structuresTable;
    KripkeStructureValues structuresValues;

    public class KripkeViewConfig {
        public int vSpacing;
        public int hSpacing;
        public boolean showAP;

        public int getMaxApLines() {
            return (int) Math.floor(vSpacing);
        }
    }

    KripkeViewConfig kripkeViewConfig = new KripkeViewConfig();

    public KripkeTab() {
        kripkeViewConfig.vSpacing = 30;
        kripkeViewConfig.hSpacing = 40;
        kripkeViewConfig.showAP = true;

        kripkeViews = new HashMap<>();
        createViews();

        EventSource.addListener(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, e -> {
            CreateTabs();
        });

        tabbedPanel.addChangeListener(e -> {
            EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_TAB_CHANGE, tabbedPanel.getSelectedIndex());
            try {
                if (tabbedPanel.getSelectedIndex() >= 0 && bpmnSplitPanel != null) {
                    KripkeView kripkeView = kripkeViews.get(tabbedPanel.getSelectedIndex()).getKripkeView();
                    bpmnSplitPanel.setBottomComponent(kripkeView.getGraphOutline());

                }

            } catch (Exception ex) {

            }

        });

    }

    private void CreateTabs() {
        tabbedPanel.removeAll();

        for (KripkeStructure structure : AppCore.app.getKripkeStructures().values()) {
            tabbedPanel.addTab(structure.getName(), getStructureTab(structure));
        }
        //TODO REMOVE
    }

    public int getSelectedTabIndex() {
        return tabbedPanel.getSelectedIndex();
    }

    JPanel libraryPanel;
    JSplitPane bpmnSplitPanel;

    public void fillLeftPanel() {
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new GridLayout(0, 1));
        bpmnSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPanel, new JPanel());
        bpmnSplitPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        bpmnSplitPanel.setResizeWeight(.6d);
        //libraryPanel.add(editorPallete);
        leftContainer.add(bpmnSplitPanel);
        settingsPanel();
    }

    private void settingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 4, 2, 2));
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = 0;

        createSelect("V-spacing", createNumberArray(10, 100, 10), 2, e -> {
            kripkeViewConfig.vSpacing = Integer.valueOf((String) e.getSource());
            updateViewConfig();

        });
        createSelect("H-spacing", createNumberArray(40, 100, 10), 0, e -> {
            kripkeViewConfig.hSpacing = Integer.valueOf((String) e.getSource());
            updateViewConfig();

        });

        createSelect("Show Atomic Propositions", new ArrayList<>(Arrays.asList("Yes", "No")), 0, e -> {
            kripkeViewConfig.showAP = (String) e.getSource() == "Yes";
            updateViewConfig();

        });

        createSelect("Model Reduction", new ArrayList<>(Arrays.asList("Yes", "No")), 0, e -> {
           AppCore.app.modelReductionEnabled = e.getSource() == "Yes";
            EventSource.fireEvent(EventType.CHECKMODEL_BUTTON_CLICK, null);

        });

        libraryPanel.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.anchor = GridBagConstraints.FIRST_LINE_START;
        cons.weightx = 1;
        cons.weighty = 1;
        cons.gridx = 0;
        libraryPanel.add(settingsPanel, cons);
    }

    private void updateViewConfig() {
        kripkeViews.values().forEach(k -> {
            k.kripkeView.setViewConfig(kripkeViewConfig);
        });
    }

    public void fillRightPanel() {
        rightContainer.setVisible(false);
    }

    public HashMap<Integer, KripkeStructure> getKripkeStructures() {
        return structuresTable.getKripkeStructures();
    }

    public void fillCenterPanel() {
        tabbedPanel = new JTabbedPane();


        centerContainer.add(tabbedPanel);
    }

    private JScrollPane getStructureTab(KripkeStructure structure) {
        if (!kripkeViews.containsKey(structure.getId())) {
            kripkeViews.put(structure.getId(), new KripkeStructureTab(structure));
        }
        return kripkeViews.get(structure.getId()).scrollView;

    }


    private class KripkeStructureTab {
        KripkeView kripkeView;
        JScrollPane scrollView;

        public KripkeStructureTab(KripkeStructure structure) {
            kripkeView = new KripkeView(structure, kripkeViewConfig);
            scrollView = new JScrollPane(kripkeView.getView());
            scrollView.setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        public JScrollPane getPanel() {
            return scrollView;
        }

        public KripkeView getKripkeView() {
            return kripkeView;
        }
    }

}
