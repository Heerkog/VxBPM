package nl.rug.ds.bpm.editor.panels.genericViews;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by Mark on 28-12-2015.
 */
public abstract class GenericTab {
    public JPanel tabPanel, centerContainer, rightContainer, leftContainer;


    public GenericTab() {

    }

    public JPanel getPanel() {
        return tabPanel;
    }

    public void createViews() {
        tabPanel = new JPanel(new BorderLayout());


        centerContainer = new JPanel();
        centerContainer.setLayout(new GridLayout(0, 1));
        createLeftPanel();
        createRightPanel();
        fillLeftPanel();
        fillRightPanel();
        fillCenterPanel();

        tabPanel.add(centerContainer, BorderLayout.CENTER);
        tabPanel.add(rightContainer, BorderLayout.EAST);
        tabPanel.add(leftContainer, BorderLayout.WEST);
    }

    private void createLeftPanel() {
        leftContainer = new JPanel();
        leftContainer.setLayout(new GridLayout(0, 1));
        leftContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        leftContainer.setPreferredSize(new Dimension(205, 600));
    }


    private void createRightPanel() {
        rightContainer = new JPanel();
        rightContainer.setLayout(new GridLayout(0, 1));
        rightContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        rightContainer.setPreferredSize(new Dimension(200, 600));

    }

    public abstract void fillLeftPanel();

    public abstract void fillRightPanel();

    public abstract void fillCenterPanel();

    //Settings
    protected GridBagConstraints gbc;
    protected JPanel settingsPanel;

    protected void createSelect(String label, ArrayList<String> values, int selectedIndex, ChangeListener changeListener) {
        JLabel myLabel = new JLabel(label);
        gbc.gridx = 0;
        settingsPanel.add(myLabel, gbc);
        JComboBox comboBox = new JComboBox();
        values.forEach(v -> comboBox.addItem(v));
        gbc.gridx++;
        settingsPanel.add(comboBox, gbc);
        gbc.gridy++;
        comboBox.setSelectedIndex(selectedIndex);

        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                changeListener.stateChanged(new ChangeEvent(comboBox.getSelectedItem()));
            }
        });
    }

    protected ArrayList<String> createNumberArray(int start, int end, int step) {
        ArrayList<String> values = new ArrayList<>();
        for (int i = start; i <= end; i += step) {
            values.add(String.valueOf(i));
        }
        return values;
    }
}
