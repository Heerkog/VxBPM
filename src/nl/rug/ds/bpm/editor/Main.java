package nl.rug.ds.bpm.editor;


import nl.rug.ds.bpm.editor.core.AppCore;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Created by Mark on 5-5-2015.
 */
public class Main extends JFrame {

    private static final long serialVersionUID = -2707712944901661771L;

    static GUIApplication guiApplication;
    static AppCore appCore;

    public static void main(String[] args) {
        UIManager.put("ComboBox.background", new ColorUIResource(Color.white));

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            //e.printStackTrace();
        }


        appCore = new AppCore();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();

        JFrame frame = AppCore.gui.getFrame();
        frame.setTitle("VxBPM v0.2.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 720);
        frame.setLocation((width-1000)/2, (height-720)/2);
        frame.setVisible(true);

    }

    public class SimplelookandfeelExample extends JPanel {

    }


}
