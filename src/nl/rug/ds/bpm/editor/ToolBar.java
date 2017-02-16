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
public class ToolBar extends JMenuBar {
    mxGraphComponent graphComponent;
    ButtonGroup checkerGroup;
    JCheckBoxMenuItem autocheck, fullAutoput;
    JMenu verificationMenu;

    public ToolBar(final GUIApplication editor) {
        BPMNEditorActions.BPMNview = editor.getBPMNView();
        this.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        this.add(fileMenu);
        this.add(editMenu);

        addMenuItem(fileMenu, "New", null).addActionListener(e -> {
            AppCore.app.clear();
        });
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Open", null).addActionListener(e -> {
            AppCore.app.openXPDL();
        });
        addMenuItem(fileMenu, "Save", null).addActionListener(e -> {
            AppCore.app.saveXPDL();
        });
        addMenuItem(fileMenu, "Save As", null).addActionListener(e -> {
            AppCore.app.saveXPDLAs();
        });
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Import specifications", null).addActionListener(e -> {
            AppCore.app.importSpecificationSet();
        });
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Exit", null).addActionListener(e -> {
            System.exit(0);
        });

        addMenuItem(editMenu, "Undo", null).addActionListener(new BPMNEditorActions.HistoryAction(true));
        addMenuItem(editMenu, "Redo", null).addActionListener(new BPMNEditorActions.HistoryAction(false));

        verificationMenu = new JMenu("Verification");
        this.add(verificationMenu);
        buildVerificationMenu();
    }

    public void buildVerificationMenu()
    {
        verificationMenu.removeAll();

        JMenuItem checkModel  = new JMenuItem("Verify");
        checkModel.setBorderPainted(false);
        checkModel.setEnabled(false);
        verificationMenu.add(checkModel);
        checkModel.addActionListener(e -> {
            checkModel.setEnabled(false);
            EventSource.fireEvent(EventType.CHECKMODEL_BUTTON_CLICK, null);
        });
        EventSource.addListener(EventType.CHECKMODEL_BUTTON_ENABLED, e -> {
            checkModel.setEnabled((boolean) e);
        });

        verificationMenu.addSeparator();

        checkerGroup = new ButtonGroup();
        AppCore.app.config.getModelCheckers().forEach((item) -> {
            if(item.isEnabled()) {
                JRadioButtonMenuItem checker = new JRadioButtonMenuItem(item.getName(), true);
                checker.setActionCommand(item.getId());
                verificationMenu.add(checker);
                checkerGroup.add(checker);
                CellPropertyPanel.addChangeListener(checker, checkerGroup, e -> {
                    EventSource.fireEvent(EventType.MODEL_CHECKER_CHANGE, AppCore.app.config.getModelCheckers().stream().filter(
                            modelChecker -> modelChecker.getId().equals(checkerGroup.getSelection().getActionCommand())
                    ).findFirst().get());
                });
            }
        });

        verificationMenu.addSeparator();

        autocheck = new JCheckBoxMenuItem("Automatic Verification");
        autocheck.setSelected(false);
        verificationMenu.add(autocheck);

        fullAutoput = new JCheckBoxMenuItem("Full output");
        fullAutoput.setSelected(false);
        fullAutoput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventSource.fireEvent(EventType.CONSOLE_FULLOUTPUT_CHANGED, 0);
            }
        });
        verificationMenu.add(fullAutoput);

        verificationMenu.addSeparator();

        addMenuItem(verificationMenu, "Settings", null).addActionListener(e -> {
            OptionsDialog od = new OptionsDialog(AppCore.gui.frame);
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
