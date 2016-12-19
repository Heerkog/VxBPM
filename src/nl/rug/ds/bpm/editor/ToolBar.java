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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Mark on 8/1/2015.
 */
public class ToolBar extends JMenuBar {
    mxGraphComponent graphComponent;
    ButtonGroup checkerGroup;
    JCheckBoxMenuItem autocheck, fullAutoput;

    public ToolBar(final GUIApplication editor) {
        BPMNEditorActions.BPMNview = editor.getBPMNView();
        this.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu optionsMenu = new JMenu("Options");

        this.add(fileMenu);
        this.add(editMenu);
        this.add(optionsMenu);

        addMenuItem(fileMenu, "New", null).addActionListener(e -> {
            AppCore.app.clear();
        });
        addMenuItem(fileMenu,"Open", null).addActionListener(e -> {
            AppCore.app.OpenXPDL();
        });
        addMenuItem(fileMenu,"Save", null).addActionListener(e -> {
            AppCore.app.saveXPDL();
        });
        addMenuItem(fileMenu, "Exit", null).addActionListener(e -> {
            System.exit(0);
        });

        addMenuItem(editMenu,"Undo", null).addActionListener(new BPMNEditorActions.HistoryAction(true));
        addMenuItem(editMenu,"Redo", null).addActionListener(new BPMNEditorActions.HistoryAction(false));

        checkerGroup = new ButtonGroup();
        AppCore.app.config.getModelCheckers().forEach((item) -> {
            JRadioButtonMenuItem checker = new JRadioButtonMenuItem(item.getName(), true);
            checker.setActionCommand(item.getId());
            optionsMenu.add(checker);
            checkerGroup.add(checker);
            CellPropertyPanel.addChangeListener(checker, checkerGroup, e -> {
                EventSource.fireEvent(EventType.MODEL_CHECKER_CHANGE, AppCore.app.config.getModelCheckers().stream().filter(
                        modelChecker -> modelChecker.getId().equals(checkerGroup.getSelection().getActionCommand())
                ).findFirst().get());
            });
        });


/*
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
*/

        //add(Box.createHorizontalGlue());
        optionsMenu.addSeparator();

        autocheck = new JCheckBoxMenuItem("Auto-check ");
        autocheck.setSelected(true);
        optionsMenu.add(autocheck);

        fullAutoput = new JCheckBoxMenuItem("Full output");
        fullAutoput.setSelected(true);
        fullAutoput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventSource.fireEvent(EventType.CONSOLE_FULLOUTPUT_CHANGED, 0);
            }
        });
        optionsMenu.add(fullAutoput);

        JButton checkModel  = new JButton("CheckModel");
        checkModel.setBorderPainted(false);
        checkModel.setContentAreaFilled(false);
        checkModel.setFocusable(false);
        checkModel.setRolloverEnabled(true);
        checkModel.getModel().addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                ButtonModel model = (ButtonModel) e.getSource();

                if(model.isRollover())
                {
                    checkModel.setBackground(new Color(145,201,247,128)); //Changes the colour of the button
                    checkModel.setOpaque(true);
                }

                else
                {
                    checkModel.setBackground(null);
                    checkModel.setOpaque(false);
                }
            }
        });
        JMenu sep = new JMenu("|");
        sep.setEnabled(false);
        this.add(sep);

        this.add(checkModel);


        checkModel.addActionListener(e -> {
            checkModel.setEnabled(false);
            EventSource.fireEvent(EventType.CHECKMODEL_BUTTON_CLICK, null);
        });

        EventSource.addListener(EventType.CHECKMODEL_BUTTON_ENABLED, e -> {
            checkModel.setEnabled((boolean) e);
        });
    }

    public boolean isFullOutput() {
        return fullAutoput.isSelected();
    }

    public boolean autocheckModel() {
        return autocheck.isSelected();
    }

    public ModelChecker selectedModelChecker() {
        return AppCore.app.config.getModelCheckers().stream().filter(
            modelChecker -> modelChecker.getId().equals(checkerGroup.getSelection().getActionCommand())
        ).findFirst().get();
    }

    public JMenuItem addMenuItem(JMenu menu, String text, final Action action) {
        JMenuItem button = new JMenuItem(text);
        button.setBorderPainted(false);
        menu.add(button);
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
