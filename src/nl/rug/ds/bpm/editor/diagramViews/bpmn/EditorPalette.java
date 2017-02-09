package nl.rug.ds.bpm.editor.diagramViews.bpmn;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxMarkerRegistry;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.*;
import nl.rug.ds.bpm.editor.GUIApplication;
import nl.rug.ds.bpm.editor.Main;
import nl.rug.ds.bpm.editor.ShadowBorder;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.models.ConstrainMarker;
import nl.rug.ds.bpm.editor.models.ConstrainShapePainter;
import nl.rug.ds.bpm.editor.models.InputElement;
import nl.rug.ds.bpm.editor.models.PaletElement;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputEdgeCell;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.UUID;


/**
 * Created by Mark on 5-5-2015.
 */
public class EditorPalette extends JPanel {
    protected Color gradientColor = new Color(199, 212, 247);
    protected JLabel selectedEntry = null;
    protected mxEventSource eventSource = new mxEventSource(this);
    private BPMNGraph graph;
    GUIApplication guiApplication;

    public EditorPalette(BPMNGraph graph) {
        this.guiApplication = AppCore.gui;
        this.graph = graph;
        setBackground(new Color(199, 212, 247));
        setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        // Clears the current selection when the background is clicked
        addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                //clearSelection();
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });

        // Shows a nice icon for drag and drop but doesn't import anything
        setTransferHandler(new TransferHandler() {
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                return true;
            }
        });

    }


    public void paintComponent(Graphics g) {
        if (gradientColor == null) {
            super.paintComponent(g);
        } else {
            Rectangle rect = getVisibleRect();
            if (g.getClipBounds() != null) {
                rect = rect.intersection(g.getClipBounds());
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), 0, gradientColor));
            g2.fill(rect);
        }
    }

    public void setSelectionEntry(JLabel entry, mxGraphTransferable t) {
        JLabel previous = selectedEntry;
        selectedEntry = entry;

        if (previous != null) {
            previous.setBorder(null);
            previous.setOpaque(false);
        }
        if (selectedEntry != null) {
            selectedEntry.setBorder(ShadowBorder.getSharedInstance());
            selectedEntry.setOpaque(true);
        }
        eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry", selectedEntry, "transferable", t, "previous", previous));
    }


    public void addEdgeTemplate(final String name, ImageIcon icon, String style, int width, int height, Object value) {
        mxGeometry geometry = new mxGeometry(0, 0, width, height);
        geometry.setTerminalPoint(new mxPoint(0, height), true);
        geometry.setTerminalPoint(new mxPoint(width, 0), false);
        geometry.setRelative(true);

        mxMarkerRegistry.registerMarker("ConstrainMarker", new ConstrainMarker());
        mxGraphics2DCanvas.putShape("TESTSHAPE", new ConstrainShapePainter());
        EdgeCell cell = new EdgeCell(UUID.randomUUID().toString(), geometry, style + ";edgeStyle=elbowEdgeStyle;orthogonal=false;fontSize=8;");
        cell.setEdge(true);
        JLabel entry = createEntry(name, icon);
        addEgde(entry, cell);
    }

    public void addRelationEdgeTemplate(final String name, ImageIcon icon, String style, int width, int height, Object value) {
        mxGeometry geometry = new mxGeometry(0, 0, width, height);
        geometry.setTerminalPoint(new mxPoint(0, height), true);
        geometry.setTerminalPoint(new mxPoint(width, 0), false);
        geometry.setRelative(true);
        EdgeCell cell = new ConstrainEdgeCell(UUID.randomUUID().toString(), geometry, style + ";edgeStyle=elbowEdgeStyle;orthogonal=true");
        cell.setEdge(true);
        JLabel entry = createEntry(name, icon);
        addEgde(entry, cell);
    }


    public void createInputShapes(java.util.List<InputElement> inputElements) {
        for (InputElement inputElement : inputElements) {
            try {
                String nodeXml = mxUtils.readInputStream(Main.class.getResourceAsStream("/resources/inputElements/" + inputElement.getShapePath()));
                mxStencilShape newShape = new mxStencilShape(nodeXml);
                mxGraphics2DCanvas.putShape(inputElement.getId(), newShape);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addInputElement(PaletElement paletElement) {
        try {
            if (paletElement.getInputElements().size() > 0) {
                final InputElement inputElement = paletElement.getInputElements().get(0);
                ImageIcon image = new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/inputElements/" + paletElement.getPaletIconPath())));
                JLabel entry = createEntry(paletElement.getName(), image);

                addCell(entry, inputElement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public JLabel createEntry(final String name, ImageIcon icon) {
        if (icon != null) {
            //if (icon.getIconWidth() > 25 || icon.getIconHeight() > 25) {
            icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            //}
        }

        final JLabel entry = new JLabel(icon);
        entry.setPreferredSize(new Dimension(45, 44));
        entry.setBackground(EditorPalette.this.getBackground().brighter());
        entry.setFont(new Font(entry.getFont().getFamily(), 0, 9));

        entry.setVerticalTextPosition(JLabel.BOTTOM);
        entry.setHorizontalTextPosition(JLabel.CENTER);
        entry.setIconTextGap(0);

        entry.setToolTipText(name);
        entry.setText("<html><div style=\"text-align: center;\">" + name);
        entry.setHorizontalAlignment(JLabel.CENTER);
        entry.setVerticalAlignment(JLabel.TOP);
        entry.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                //setSelectionEntry(entry, t);
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
        add(entry);
        return entry;
    }

    public void addEgde(JLabel entry, EdgeCell edge) {
        DragGestureListener dragGestureListener = new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent e) {
                mxGraphTransferable t = new mxGraphTransferable(new Object[]{edge}, edge.getGeometry());
                e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(-10,-10), t, null);
            }
        };
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_COPY, dragGestureListener);
    }

    public void addCell(JLabel entry, InputElement inputElement) {
        DragGestureListener dragGestureListener = new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent e) {

                if (inputElement.isEdge()) {
                    InputEdgeCell edge = InputEdgeCell.generateCell(graph,inputElement);
                    mxGraphTransferable t = new mxGraphTransferable(new Object[]{edge}, edge.getGeometry());
                    e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(0,0), t, null);


                } else {
                    InputCell cell = InputCell.generateCell(graph, inputElement);
                    mxGraphTransferable t = new mxGraphTransferable(new Object[]{cell}, cell.getGeometry());
                    e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(-10,-10), t, null);
                }


            }
        };
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(entry, DnDConstants.ACTION_COPY, dragGestureListener);


    }

    public void addListener(String eventName, mxEventSource.mxIEventListener listener) {
        eventSource.addListener(eventName, listener);
    }

    public void removeListener(mxEventSource.mxIEventListener listener) {
        eventSource.removeListener(listener);
    }

    public void removeListener(mxEventSource.mxIEventListener listener, String eventName) {
        eventSource.removeListener(listener, eventName);
    }
}
