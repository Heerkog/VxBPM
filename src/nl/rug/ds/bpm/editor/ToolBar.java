package nl.rug.ds.bpm.editor;

import com.mxgraph.swing.mxGraphComponent;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNEditorActions;
import nl.rug.ds.bpm.editor.models.ModelChecker;
import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.CellPropertyPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Mark on 8/1/2015.
 */
public class ToolBar extends JToolBar {
    mxGraphComponent graphComponent;
    JComboBox comboBox;
    JCheckBox autocheck, fullAutoput;

    public ToolBar(final GUIApplication editor) {
        BPMNEditorActions.BPMNview = editor.getBPMNView();
        this.setFloatable(false);
        this.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        addButton("New", null).addActionListener(e -> {
            AppCore.app.clear();
        });

        addButton("Open", null).addActionListener(e -> {
            AppCore.app.OpenXPDL();
        });
        addButton("Save", null).addActionListener(e -> {
            AppCore.app.saveXPDL();
        });

        addButton("Undo", null).addActionListener(new BPMNEditorActions.HistoryAction(true));
        addButton("Redo", null).addActionListener(new BPMNEditorActions.HistoryAction(false));

        //editor.config.getModelCheckers().entrySet().stream().map(p -> p.getValue().getName()).collect(Collectors. ());

        comboBox = new JComboBox<String>();


        AppCore.app.config.getModelCheckers().forEach((item) -> {
            comboBox.addItem(item.getName());
        });
        comboBox.setMaximumSize(comboBox.getPreferredSize());

        CellPropertyPanel.addChangeListener(comboBox, e -> {
            int index = comboBox.getSelectedIndex();
            EventSource.fireEvent(EventType.MODEL_CHECKER_CHANGE, AppCore.app.config.getModelCheckers().get(index));
        });
        comboBox.setSelectedIndex(0);
        EventSource.fireEvent(EventType.MODEL_CHECKER_CHANGE, AppCore.app.config.getModelCheckers().get(0));
        add(comboBox);


        //add(Box.createHorizontalGlue());
        JButton checkModel = addButton("CheckModel", null);

        checkModel.addActionListener(e -> {
            checkModel.setEnabled(false);
            EventSource.fireEvent(EventType.CHECKMODEL_BUTTON_CLICK, null);
        });

        EventSource.addListener(EventType.CHECKMODEL_BUTTON_ENABLED, e -> {
            checkModel.setEnabled((boolean) e);
        });

        autocheck = new JCheckBox("Auto-check ");
        autocheck.setSelected(true);
        add(autocheck);

        fullAutoput = new JCheckBox("Full output");
        fullAutoput.setSelected(true);
        fullAutoput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventSource.fireEvent(EventType.CONSOLE_FULLOUTPUT_CHANGED, 0);
            }
        });
        add(fullAutoput);

    }

    public boolean isFullOutput() {
        return fullAutoput.isSelected();
    }

    public boolean autocheckModel() {
        return autocheck.isSelected();
    }

    public ModelChecker selectedModelChecker() {
        int index = comboBox.getSelectedIndex();
        return AppCore.app.config.getModelCheckers().get(index);
    }

    public JButton addButton(String text, final Action action) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        add(button);
        button.addActionListener(action);
        return button;
    }


    public Action bind(String name, final Action action) {
        return bind(name, action, null);
    }

    /**
     * @param name
     * @param action
     * @return a new Action bound to the specified string name and icon
     */
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, String iconUrl) {
        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(GUIApplication.class.getResource(iconUrl)) : null) {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(graphComponent, e.getID(), e.getActionCommand()));
            }
        };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

    public JMenuItem MenuItem(String name, final Action action, String iconUrl) {
        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(GUIApplication.class.getResource(iconUrl)) : null) {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(graphComponent, e.getID(), e.getActionCommand()));
            }
        };

        JMenuItem menuItem = new JMenuItem(newAction);

        return menuItem;
    }
}
