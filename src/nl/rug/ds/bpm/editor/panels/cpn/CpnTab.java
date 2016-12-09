package nl.rug.ds.bpm.editor.panels.cpn;

import nl.rug.ds.bpm.editor.diagramViews.cpn.CPNview;
import nl.rug.ds.bpm.editor.panels.genericViews.GenericTab;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Mark on 28-12-2015.
 */
public class CpnTab extends GenericTab {
    CPNview cpNview;

    public CpnTab() {
        cpNview = new CPNview();
        createViews();
    }

    JPanel libraryPanel;

    public void fillLeftPanel() {
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new GridLayout(0, 1));
        JSplitPane bpmnSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPanel, cpNview.getGraphOutline());
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

        createSelect("V-spacing", createNumberArray(10, 100, 10), 3, e -> {
            String value = (String) e.getSource();
            cpNview.setIntraCellSpacing(Integer.valueOf(value));

        });
        createSelect("H-spacing", createNumberArray(40, 100, 10), 1, e -> {
            String value = (String) e.getSource();
            cpNview.setInterRankCellSpacing(Integer.valueOf(value));

        });

        createSelect("Show BPMN", new ArrayList<>(Arrays.asList("Yes", "No")), 1, e -> {
            String value = (String) e.getSource();
            cpNview.showBpmnBorder(value == "Yes");
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


    public void fillRightPanel() {
        rightContainer.setVisible(false);
    }

    public void fillCenterPanel() {
        JScrollPane scrollView = new JScrollPane(cpNview.getView());
        centerContainer.add(scrollView);

    }
}
