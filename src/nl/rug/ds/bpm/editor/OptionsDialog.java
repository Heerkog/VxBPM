package nl.rug.ds.bpm.editor;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.configloader.Configsaver;
import nl.rug.ds.bpm.editor.models.ModelChecker;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Created by Heerko Groefsema on 31-1-2017.
 */
public class OptionsDialog extends JDialog
{
    JFrame parent;
    Configsaver cs;

    public OptionsDialog(JFrame parent)
    {
        this.parent = parent;
        cs = new Configsaver();

        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        for (ModelChecker m: AppCore.app.config.getModelCheckers()) {
            addModelChecker(m);
        }

        JButton save = new JButton("Save");
        this.add(save);
        save.addActionListener(e -> {
            cs.saveModelCheckers(AppCore.app.config.getModelCheckers());
            AppCore.gui.toolbar.buildVerificationMenu();
            this.dispatchEvent(new WindowEvent(
                    this, WindowEvent.WINDOW_CLOSING));
        });

        pack();
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocation((parent.getWidth()-this.getWidth())/2, (parent.getHeight()-this.getHeight())/2);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addModelChecker(ModelChecker m)
    {
        JPanel checkerPanel = new JPanel();
        checkerPanel.setLayout(new BoxLayout(checkerPanel, BoxLayout.LINE_AXIS));

        checkerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(m.getName()),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        JTextField path = new JTextField(m.getLocation(), 30);
        path.setEditable(false);
        checkerPanel.add(path);

        JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("EXE File","exe");
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);

        JButton open = new JButton("Open");
        checkerPanel.add(open);
        open.addActionListener(e -> {
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                path.setText(file.getAbsolutePath());
                m.setLocation(file.getAbsolutePath());
            }
        });

        JToggleButton enabled = new JToggleButton((m.isEnabled() ? "Disable" : "Enable"));
        checkerPanel.add(enabled);
        enabled.setSelected(m.isEnabled());
        enabled.addActionListener(e -> {
            if(enabled.isSelected()) {
                if (!m.getLocation().isEmpty()) {
                    m.setEnabled(enabled.isSelected());
                    enabled.setText((m.isEnabled() ? "Disable" : "Enable"));
                }
                else {
                    enabled.setSelected(false);
                }
            }
            else {
                m.setEnabled(enabled.isSelected());
                enabled.setText((m.isEnabled() ? "Disable" : "Enable"));
            }
        });

        if(m.getName().equalsIgnoreCase("MCheck")) {
            open.setEnabled(false);
            enabled.setEnabled(false);
        }

        this.add(checkerPanel);
    }
}
