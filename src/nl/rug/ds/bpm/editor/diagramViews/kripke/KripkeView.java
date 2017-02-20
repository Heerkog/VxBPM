package nl.rug.ds.bpm.editor.diagramViews.kripke;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.*;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import nl.rug.ds.bpm.editor.GUIApplication;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.Converter;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.KripkeStructure;
import nl.rug.ds.bpm.editor.panels.kripke.KripkeTab;
import nl.rug.ds.bpm.verification.models.cpn.*;
import nl.rug.ds.bpm.verification.models.kripke.Kripke;
import nl.rug.ds.bpm.verification.models.kripke.State;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mark Kloosterhuis.
 */
public class KripkeView {
    KripkeGraph graph;
    mxGraphOutline graphOutline;
    KripkeGraphComponent graphComponent;
    GUIApplication guiApplication;
    HashMap<UUID, Object> vertexObjects = new HashMap<>();
    private CPN cpn;
    JTabbedPane tabbedPanel;
    KripkeStructure structure;
    KripkeTab.KripkeViewConfig viewConfig;


    public KripkeView(KripkeStructure structure, KripkeTab.KripkeViewConfig viewConfig) {
        this.viewConfig = viewConfig;
        this.structure = structure;
        this.guiApplication = AppCore.gui;
        graph = new KripkeGraph();
        graph.setEnabled(true);

        graph.setDisconnectOnMove(false);
        graph.setHtmlLabels(true);
        // graph.getModel().beginUpdate();
        graph.setGridEnabled(false);


        mxGraphModel model = (mxGraphModel) graph.getModel();
        graphComponent = new KripkeGraphComponent(graph);
        graphComponent.setConnectable(false);


        graphOutline = new mxGraphOutline(graphComponent);
        //   graphComponent.setConnectable(false);
        //graphComponent.setLocation(new Point(50, 50));
        EventSource.addListener(EventType.EDITOR_TABVIEW_CHANGED, e -> {
            tabChanged(e);
        });
        EventSource.addListener(EventType.KRIPKE_STRUCTURE_CHANGE, e -> {
            if ((int) e == structure.getId()) {
                renderModel();
            }
        });
/*
        graph.getSelectionModel().addListener(mxEvent.CHANGE, (s, e) -> {
                    try {
                        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);

                        ImageIO.write(image, "PNG", new File("C:\\Temp\\Kripke.png"));
                    } catch (Exception ex) {

                    }
                }
        );
*/
        KripkeView view = this;
        MouseWheelListener wheelTracker = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getSource() instanceof mxGraphOutline
                        || e.isControlDown()) {
                    view.mouseWheelMoved(e);
                }
            }

        };

        // Handles mouse wheel events in the outline and graph component
        graphOutline.addMouseWheelListener(wheelTracker);
        graphComponent.addMouseWheelListener(wheelTracker);


    }

    public void setViewConfig(KripkeTab.KripkeViewConfig viewConfig) {
        this.viewConfig = viewConfig;
        renderModel();
    }

    private void renderModel() {
        Kripke k = AppCore.app.getKripkeStructures().get(structure.getId()).getKripke();
        HashMap<String, mxCell> states = new HashMap<>();

        try {
            ((mxGraphModel) graph.getModel()).clear();
            graph.getModel().beginUpdate();
            Object parent = graph.getDefaultParent();

            for (State state : k.getStates()) {
                mxCell v = (mxCell) graph.insertVertex(parent, null, state.getID(), 10, 10, 25, 25, "shape=ellipse;fontSize=8;foldable=0");
                v.setValue(state.getID());
                states.put(state.getID(), v);
            }
            for (State state : k.getStates()) {

                mxCell source = states.get(state.getID());
                for (State nextState : state.getNextStates()) {
                    mxCell target = states.get(nextState.getID());
                    mxCell egde = (mxCell) graph.insertEdge(parent, null, "", source, target, "");
                }


            }
            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
            layout.setIntraCellSpacing(this.viewConfig.vSpacing);
            layout.setInterRankCellSpacing(this.viewConfig.hSpacing);
            layout.execute(parent);
            if (this.viewConfig.showAP) {
                for (State state : k.getStates()) {
                    mxCell stateCell = states.get(state.getID());

                    List<String> aps = new ArrayList<>();
                    List<String> all = new ArrayList<String>(state.getAtomicPropositions());

                    int lines = (int) Math.max(Math.floor(viewConfig.vSpacing / 10) - 1, 1);
                    for (int i = 0; i < lines && i < all.size(); i++)
                        aps.add(all.get(i));

                    String label = String.join("\n", aps);
                    if (all.size() > lines)
                        label += "...";


                    mxCell targetLabel = new mxCell(stateCell, new mxGeometry(0, 1, 30, 30), "shape=;verticalAlign=top;fontSize=8;");
                    targetLabel.setValue(label);
                    targetLabel.getGeometry().setRelative(true);
                    targetLabel.setConnectable(false);
                    targetLabel.setVertex(true);
                    stateCell.insert(targetLabel);


                }
            }

        } finally {
            graph.getModel().endUpdate();
            graph.refresh();
        }
    }

    private void tabChanged(Object obj) {
        if ((int) obj == 2) {
            Converter converter = AppCore.app.converter;
            cpn = converter.cpn;


            //  Object parent = graph.getDefaultParent();
            //  mxGraphLayout layout = new mxOrganicLayout(graph);
            //  layout.execute(parent);


        }
    }

    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    protected void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphComponent.zoomIn();
        } else {
            graphComponent.zoomOut();
        }
    }

    protected mxIGraphLayout createLayout(String ident, boolean animate) {
        mxIGraphLayout layout = null;

        if (ident != null) {
            mxGraph graph = graphComponent.getGraph();

            if (ident.equals("verticalHierarchical")) {
                layout = new mxHierarchicalLayout(graph);
            } else if (ident.equals("horizontalHierarchical")) {
                layout = new mxHierarchicalLayout(graph, JLabel.WEST);
            } else if (ident.equals("verticalTree")) {
                layout = new mxCompactTreeLayout(graph, false);
            } else if (ident.equals("horizontalTree")) {
                layout = new mxCompactTreeLayout(graph, true);
            } else if (ident.equals("parallelEdges")) {
                layout = new mxParallelEdgeLayout(graph);
            } else if (ident.equals("placeEdgeLabels")) {
                layout = new mxEdgeLabelLayout(graph);
            } else if (ident.equals("organicLayout")) {
                layout = new mxOrganicLayout(graph);
            }
            if (ident.equals("verticalPartition")) {
                layout = new mxPartitionLayout(graph, false) {
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("horizontalPartition")) {
                layout = new mxPartitionLayout(graph, true) {
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("verticalStack")) {
                layout = new mxStackLayout(graph, false) {
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("horizontalStack")) {
                layout = new mxStackLayout(graph, true) {
                    public mxRectangle getContainerSize() {
                        return graphComponent.getLayoutAreaSize();
                    }
                };
            } else if (ident.equals("circleLayout")) {
                layout = new mxCircleLayout(graph);
            }
        }

        return layout;
    }


    private void buildCaseNet() {
        cpn = new CPN();

        //Variables and possible values
        Variable urgent = new Variable(1, "urgent");    //boolean
        urgent.addValue("0");
        urgent.addValue("1");

        Variable TID = new Variable(1, "TID");    //boolean
        TID.addValue("0");
        TID.addValue("1");

        Variable contact = new Variable(1, "contact");    //boolean
        contact.addValue("0");
        contact.addValue("1");

        Variable bill = new Variable(1, "bill");    //boolean
        bill.addValue("0");
        bill.addValue("1");

        Variable easy = new Variable(1, "easy");    //boolean
        easy.addValue("0");
        easy.addValue("1");

        Variable accept = new Variable(1, "accept");    //boolean
        accept.addValue("0");
        accept.addValue("1");

        Variable additional = new Variable(1, "additional");    //boolean
        additional.addValue("0");
        additional.addValue("1");

        Variable technical = new Variable(1, "technical");    //boolean
        technical.addValue("0");
        technical.addValue("1");

        Variable nontechnical = new Variable(1, "nontechnical");    //boolean
        nontechnical.addValue("0");
        nontechnical.addValue("1");

        Variable CSG = new Variable(1, "CSG");    //boolean
        CSG.addValue("0");
        CSG.addValue("1");

        Variable delay = new Variable(1, "delay");    //boolean
        delay.addValue("0");
        delay.addValue("1");

        //Variables per gate
        ArrayList<Variable> urgencyXOR = new ArrayList<Variable>();
        urgencyXOR.add(urgent);

        ArrayList<Variable> TIDXOR = new ArrayList<Variable>();
        TIDXOR.add(TID);

        ArrayList<Variable> contactXOR = new ArrayList<Variable>();
        contactXOR.add(contact);

        ArrayList<Variable> billXOR = new ArrayList<Variable>();
        billXOR.add(bill);

        ArrayList<Variable> easyXOR = new ArrayList<Variable>();
        easyXOR.add(easy);

        ArrayList<Variable> acceptXOR = new ArrayList<Variable>();
        acceptXOR.add(accept);

        ArrayList<Variable> additionalXOR = new ArrayList<Variable>();
        additionalXOR.add(additional);

        ArrayList<Variable> technicalOR = new ArrayList<Variable>();
        technicalOR.add(technical);
        technicalOR.add(nontechnical);

        ArrayList<Variable> CSGXOR = new ArrayList<Variable>();
        CSGXOR.add(CSG);

        ArrayList<Variable> delayXOR = new ArrayList<Variable>();
        delayXOR.add(delay);

        //Places
        Place start = new Place();
        Place p0 = new Place();
        Place p1 = new Place();
        Place p2 = new Place();
        Place p3 = new Place();
        Place p4 = new Place();
        Place p5 = new Place();
        Place p6 = new Place();
        Place p7 = new Place();
        Place p8 = new Place();
        Place p9 = new Place();
        Place p10 = new Place();
        Place p11 = new Place();
        Place p12 = new Place();
        Place p13 = new Place();
        Place p14 = new Place();
        Place p15 = new Place();
        Place p16 = new Place();
        Place p17 = new Place();
        Place p18 = new Place();
        Place p19 = new Place();
        Place p20 = new Place();
        Place p21 = new Place();
        Place p22 = new Place();
        Place p23 = new Place();
        Place p24 = new Place();
        Place p25 = new Place();
        Place p26 = new Place();
        Place p27 = new Place();
        Place p28 = new Place();
        Place end = new Place();
        cpn.addPlace(start);
        cpn.addPlace(p0);
        cpn.addPlace(p1);
        cpn.addPlace(p2);
        cpn.addPlace(p3);
        cpn.addPlace(p4);
        cpn.addPlace(p5);
        cpn.addPlace(p6);
        cpn.addPlace(p7);
        cpn.addPlace(p8);
        cpn.addPlace(p9);
        cpn.addPlace(p10);
        cpn.addPlace(p11);
        cpn.addPlace(p12);
        cpn.addPlace(p13);
        cpn.addPlace(p14);
        cpn.addPlace(p15);
        cpn.addPlace(p16);
        cpn.addPlace(p17);
        cpn.addPlace(p18);
        cpn.addPlace(p19);
        cpn.addPlace(p20);
        cpn.addPlace(p21);
        cpn.addPlace(p22);
        cpn.addPlace(p23);
        cpn.addPlace(p24);
        cpn.addPlace(p25);
        cpn.addPlace(p26);
        cpn.addPlace(p27);
        cpn.addPlace(p28);
        cpn.addPlace(end);

        cpn.addToken(start);

        //Transitions
        Transition tstart = new Transition("start");
        Transition t0 = new Transition("t0");
        Transition t1 = new Transition("t1");
        Transition t2 = new Transition("t2");
        Transition t3 = new Transition("t3");
        Transition t4 = new Transition("t4");
        Transition t5 = new Transition("t5");
        Transition t6 = new Transition("t6");
        Transition t7 = new Transition("t7");
        Transition t8 = new Transition("t8");
        Transition t9 = new Transition("t9");
        Transition t10 = new Transition("t10");
        Transition t11 = new Transition("t11");
        Transition t12 = new Transition("t12");
        Transition t13 = new Transition("t13");
        Transition t14 = new Transition("t14");
        Transition t15 = new Transition("t15");
        Transition t16 = new Transition("t16");
        Transition t17 = new Transition("t17");
        Transition t18 = new Transition("t18");
        Transition t19 = new Transition("t19");
        Transition t20 = new Transition("t20");
        Transition t21 = new Transition("t21");
        Transition t22 = new Transition("t22");
        Transition t23 = new Transition("t23");
        Transition t24 = new Transition("t24");
        Transition tend = new Transition("end");
        Transition silent = new Transition("silent");
        Transition silent2 = new Transition("silent");

        cpn.addTransition(tstart);
        cpn.addTransition(t0);
        cpn.addTransition(t1);
        cpn.addTransition(t2);
        cpn.addTransition(t3);
        cpn.addTransition(t4);
        cpn.addTransition(t5);
        cpn.addTransition(t6);
        cpn.addTransition(t7);
        cpn.addTransition(t8);
        cpn.addTransition(t9);
        cpn.addTransition(t10);
        cpn.addTransition(t11);
        cpn.addTransition(t12);
        cpn.addTransition(t13);
        cpn.addTransition(t14);
        cpn.addTransition(t15);
        cpn.addTransition(t16);
        cpn.addTransition(t17);
        cpn.addTransition(t18);
        cpn.addTransition(t19);
        cpn.addTransition(t20);
        cpn.addTransition(t21);
        cpn.addTransition(t22);
        cpn.addTransition(t23);
        cpn.addTransition(t24);
        cpn.addTransition(tend);
        cpn.addTransition(silent);
        cpn.addTransition(silent2);

        //Arcs


        //Start
        Arc a0 = new Arc(start, tstart, 1);

        //Activity
        Arc a1 = new Arc(tstart, p0, 1);
        Arc a2 = new Arc(p0, t0, 1);


        Arc a3 = new Arc(t0, p1, 1, "urgent == 1", urgencyXOR);
        Arc a4 = new Arc(t0, p2, 1, "urgent == 0", urgencyXOR);
        Arc a5 = new Arc(p1, t1, 1);
        Arc a7 = new Arc(p2, t2, 1);

        Arc a6 = new Arc(t1, p3, 1);//
        Arc a8 = new Arc(t2, p3, 1);//
        Arc a9 = new Arc(p3, silent, 1);


        Arc a10 = new Arc(silent, p4, 1, "contact == 0", contactXOR);
        Arc a11 = new Arc(silent, p7, 1, "contact == 1", contactXOR);

        Arc a12 = new Arc(p4, t3, 1);
        Arc a13 = new Arc(t3, p5, 1, "TID == 1", TIDXOR);
        Arc a14 = new Arc(t3, p6, 1, "TID == 0", TIDXOR);
        Arc a15 = new Arc(p5, t4, 1);
        Arc a16 = new Arc(t4, p6, 1);
        Arc a17 = new Arc(p6, tend, 1);
        Arc a18 = new Arc(tend, end, 1);

        Arc a19 = new Arc(p7, t5, 1);
        Arc a20 = new Arc(t5, p8, 1);
        Arc a21 = new Arc(p8, t6, 1);
        Arc a22 = new Arc(t6, p9, 1);
        Arc a23 = new Arc(p9, t7, 1);
        Arc a24 = new Arc(t7, p10, 1, "bill == 1", billXOR);
        Arc a25 = new Arc(t7, p11, 1, "bill == 0", billXOR);
        Arc a26 = new Arc(p10, t8, 1);
        Arc a27 = new Arc(t8, p11, 1);
        Arc a28 = new Arc(p11, t9, 1);

        Arc a29 = new Arc(t9, p12, 1, "easy == 1", easyXOR);
        Arc a30 = new Arc(t9, p18, 1, "easy == 0", easyXOR);
        Arc a31 = new Arc(p12, t10, 1);
        Arc a32 = new Arc(t10, p13, 1);
        Arc a33 = new Arc(p13, t11, 1);

        Arc a34 = new Arc(t11, p16, 1, "accept == 1", acceptXOR);
        Arc a35 = new Arc(t11, p14, 1, "accept == 0", acceptXOR);
        Arc a36 = new Arc(p16, t14, 1);
        Arc a37 = new Arc(t14, p17, 1);
        Arc a38 = new Arc(p17, t15, 1);
        Arc a39 = new Arc(t15, p6, 1);

        Arc a40 = new Arc(p14, t12, 1);
        Arc a41 = new Arc(t12, p13, 1, "additional == 1", additionalXOR);
        Arc a42 = new Arc(t12, p15, 1, "additional == 0", additionalXOR);
        Arc a43 = new Arc(p15, t13, 1);
        Arc a44 = new Arc(t13, p16, 1);

        Arc a45 = new Arc(p18, t16, 1);
        Arc a46 = new Arc(t16, p19, 1);
        Arc a47 = new Arc(p19, t17, 1);
        Arc a48 = new Arc(t17, p20, 1);
        Arc a49 = new Arc(p20, t18, 1);
        Arc a50 = new Arc(t18, p21, 1);
        Arc a51 = new Arc(p21, t19, 1);

        Arc a52 = new Arc(t19, p26, 1, "nontechnical == 1", technicalOR);
        Arc a53 = new Arc(t19, p27, 1, "nontechnical == 0", technicalOR);
        Arc a54 = new Arc(t19, p22, 1, "technical == 1", technicalOR);
        Arc a55 = new Arc(t19, p25, 1, "technical == 0", technicalOR);

        Arc a56 = new Arc(p26, t22, 1);
        Arc a57 = new Arc(t22, p27, 1);
        Arc a58 = new Arc(p27, t23, 1);

        Arc a59 = new Arc(p22, silent2, 1);
        Arc a60 = new Arc(silent2, p23, 1, "CSG == 1", CSGXOR);
        Arc a61 = new Arc(silent2, p24, 1, "CSG == 0", CSGXOR);
        Arc a62 = new Arc(p23, t20, 1);
        Arc a63 = new Arc(t20, p24, 1);
        Arc a64 = new Arc(p24, t21, 1);
        Arc a65 = new Arc(t21, p25, 1);
        Arc a66 = new Arc(p25, t23, 1);

        Arc a67 = new Arc(t23, p28, 1, "delay == 1", delayXOR);
        Arc a68 = new Arc(t23, p13, 1, "delay == 0", delayXOR);
        Arc a69 = new Arc(p28, t24, 1);
        Arc a70 = new Arc(t24, p13, 1);

        cpn.addArc(a0);
        cpn.addArc(a1);
        cpn.addArc(a2);
        cpn.addArc(a3);
        cpn.addArc(a4);
        cpn.addArc(a5);
        cpn.addArc(a6);
        cpn.addArc(a7);
        cpn.addArc(a8);
        cpn.addArc(a9);
        cpn.addArc(a10);
        cpn.addArc(a11);
        cpn.addArc(a12);
        cpn.addArc(a13);
        cpn.addArc(a14);
        cpn.addArc(a15);
        cpn.addArc(a16);
        cpn.addArc(a17);
        cpn.addArc(a18);
        cpn.addArc(a19);
        cpn.addArc(a20);
        cpn.addArc(a21);
        cpn.addArc(a22);
        cpn.addArc(a23);
        cpn.addArc(a24);
        cpn.addArc(a25);
        cpn.addArc(a26);
        cpn.addArc(a27);
        cpn.addArc(a28);
        cpn.addArc(a29);
        cpn.addArc(a30);
        cpn.addArc(a31);
        cpn.addArc(a32);
        cpn.addArc(a33);
        cpn.addArc(a34);
        cpn.addArc(a35);
        cpn.addArc(a36);
        cpn.addArc(a37);
        cpn.addArc(a38);
        cpn.addArc(a39);
        cpn.addArc(a40);
        cpn.addArc(a41);
        cpn.addArc(a42);
        cpn.addArc(a43);
        cpn.addArc(a44);
        cpn.addArc(a45);
        cpn.addArc(a46);
        cpn.addArc(a47);
        cpn.addArc(a48);
        cpn.addArc(a49);
        cpn.addArc(a50);
        cpn.addArc(a51);
        cpn.addArc(a52);
        cpn.addArc(a53);
        cpn.addArc(a54);
        cpn.addArc(a55);
        cpn.addArc(a56);
        cpn.addArc(a57);
        cpn.addArc(a58);
        cpn.addArc(a59);
        cpn.addArc(a60);
        cpn.addArc(a61);
        cpn.addArc(a62);
        cpn.addArc(a63);
        cpn.addArc(a64);
        cpn.addArc(a65);
        cpn.addArc(a66);
        cpn.addArc(a67);
        cpn.addArc(a68);
        cpn.addArc(a69);
        cpn.addArc(a70);
    }

    public mxGraphComponent getView() {
        return graphComponent;
    }

}
