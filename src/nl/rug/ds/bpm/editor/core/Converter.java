package nl.rug.ds.bpm.editor.core;

import com.mxgraph.model.mxCell;
import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.EdgeCellVariable;
import nl.rug.ds.bpm.editor.models.IConstraintHolder;
import nl.rug.ds.bpm.editor.models.KripkeStructure;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputEdgeCell;
import nl.rug.ds.bpm.editor.services.CellService;
import nl.rug.ds.bpm.editor.services.ConstraintService;
import nl.rug.ds.bpm.editor.transformer.CPNConverter;
import nl.rug.ds.bpm.editor.transformer.CPNGroup;
import nl.rug.ds.bpm.verification.constraints.Formula;
import nl.rug.ds.bpm.verification.modelConverters.CPN2KripkeConverter;
import nl.rug.ds.bpm.verification.models.cpn.CPN;
import nl.rug.ds.bpm.verification.models.cpn.CPNElement;
import nl.rug.ds.bpm.verification.models.cpn.Variable;
import nl.rug.ds.bpm.verification.models.kripke.Kripke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark on 15-12-2015.
 */
public class Converter {
    BPMNGraph graph;
    public CPN cpn;
    public Kripke kripke;
    public ArrayList<CPNGroup> groups;
    public List<InputCell> inputCells;
    public List<EdgeCell> edgeCells;
    public List<ConstrainEdgeCell> constrainCells;
    public HashMap<String, List<CPNElement>> bpmnToCPNLookup;
    public CPN2KripkeConverter CPN2KripkeConverter;
    public HashMap<Integer, Variable> variables;
    public List<Formula> formulas;
    private List<IConstraintHolder> constraintHolders;

    CPNConverter cpnConverter;
    CellService cellService;
    ConstraintService constraintService;

    public Converter() {
        cellService = AppCore.gui.getCellService();
        constraintService = AppCore.gui.getConstraintService();

        graph = AppCore.gui.getGraph();
        EventSource.addListener(EventType.CONSTRAINT_SELECT_CHANGE, e -> {
            checkChanged(false);
        });
        EventSource.addListener(EventType.BPMN_REDRAW, e -> {
            checkChanged(false);
        });


        EventSource.addListener(EventType.KRIPKE_STRUCTURE_VALUE_CHANGE, e -> {
            KripkeStructure structure = AppCore.app.getKripkeStructures().get(e);
            generateKripkeStucture(structure);
        });

        EventSource.addListener(EventType.MODEL_CHECKER_CHANGE, e -> {
            checkChanged(false);
        });
        EventSource.addListener(EventType.BPMN_CHANGED, e -> {
            checkChanged(false);
        });

        EventSource.addListener(EventType.CHECKMODEL_BUTTON_CLICK, e -> {
            checkChanged(true);
        });

        EventSource.addListener(EventType.EDITOR_TABVIEW_CHANGED, e -> {
            if ((int) e == 1 || (int) e == 2) {
                checkChanged(false);
            }
        });


    }

    private boolean checkHasChanges() {
        Integer hashCode = new Integer(cpn.toString().hashCode());
        if (!hashCode.equals(checkedCpnHashCode)) {
            System.out.println("BPMN HASH CHANGED");
            return true;
        }

        StringBuilder constraintString = new StringBuilder("");
        for (IConstraintHolder con : constraintHolders) {
            constraintString.append(con.toString());
        }

        constraintHashCode = constraintString.toString().hashCode();
        if (!constraintHashCode.equals(checkedConstraintHashCode)) {
            System.out.println("CONSTRAINT HASH CHANGED");
            return true;
        }
        if (!checkerName.equals(AppCore.app.selectedModelChecker().getName())) {
            System.out.println("MDOEL CHECKER CHANHED");
            return true;
        }

        return false;
    }

    Integer checkedCpnHashCode = 0;
    Integer checkedConstraintHashCode = 0;
    Integer constraintHashCode = 0;
    String checkerName;

    private void checkChanged(boolean checkClicked) {
        inputCells = cellService.getCells(InputCell.class);
        if(inputCells.stream().filter(c->c.deleted).count()>0) {
            String test = "ERROR!!!";
        }
        inputCells.addAll(cellService.getCells(InputEdgeCell.class));
        edgeCells = graph.getAllEdgeCells();
        constrainCells = cellService.getCells(ConstrainEdgeCell.class);
        constraintHolders = constraintService.getAllConstraintHolders();
        GenerateVariables();
        GenerateCPN();
        GenerateConstrains();

        EventSource.fireEvent(EventType.CPN_CONVERTED, null);
        boolean hasChanged = checkHasChanges();
        EventSource.fireEvent(EventType.CHECKMODEL_BUTTON_ENABLED, hasChanged);
        if (checkClicked || AppCore.gui.toolbar.autocheckModel())
            convert();
    }

    public void convert() {
        Console.error("Start createInputData");
        checkedCpnHashCode = cpn.toString().hashCode();
        checkedConstraintHashCode = constraintHashCode;
        checkerName = AppCore.app.selectedModelChecker().getName();
        EventSource.fireEvent(EventType.CHECKMODEL_BUTTON_ENABLED, false);

        List<KripkeStructure> structures = new ArrayList<>();

        KripkeStructure foundStructure2 = new KripkeStructure(0);
        foundStructure2.values2 = new HashMap<>();
        for (Variable variable : variables.values()) {
            foundStructure2.values2.put(variable.getId(), variable.getValues());
        }
        structures.add(foundStructure2);


        for (IConstraintHolder constraint : constraintHolders) {
            HashMap<Integer, List<String>> values = new HashMap<>();
            List<Integer> showInName = new ArrayList<>();
            for (Variable variable : variables.values()) {
                EdgeCellVariable conditionVariable = constraint.getVariablesValues().stream().filter(v -> v.getVariableId() == variable.getId()).findFirst().orElse(null);
                List<String> values2 = null;
                if (conditionVariable != null) {
                    showInName.add(variable.getId());
                    values2 = variable.getValues(conditionVariable.getCondition(), conditionVariable.getValue());
                } else
                    values2 = variable.getValues();
                values.put(variable.getId(), values2);
            }
            KripkeStructure foundStructure = null;
            for (KripkeStructure structure : structures) {
                if (structure.values2.equals(values)) {
                    foundStructure = structure;
                }
            }
            if (foundStructure == null) {
                foundStructure = new KripkeStructure(structures.size());
                foundStructure.values2 = values;
                structures.add(foundStructure);
            }
            foundStructure.addConstraint(constraint);
            foundStructure.mergeNamedVariables(showInName);
        }


        AppCore.app.getKripkeStructures().clear();
        for (KripkeStructure structure : structures)
            AppCore.app.getKripkeStructures().put(structure.getId(), structure);
        EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_NAME_CHANGE, null);
        GenerateKripke();

    }


    private void GenerateCPN() {
        try {
            GenerateVariables();
            cpnConverter = new CPNConverter(variables);
            cpn = cpnConverter.getCpn();
            bpmnToCPNLookup = cpnConverter.getBpmnToCPNLookup();

            cpn.setVariables(new ArrayList<Variable>(variables.values()));
            cpn.init();

            groups = cpnConverter.getGroups();
            EventSource.fireEvent(EventType.CONSOLE_WORKFLOW, cpn.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void GenerateConstrains() {
        EventSource.fireEvent(EventType.CONSOLE_CONSTRAINT, constrainCells);
    }

    private void GenerateVariables() {
        variables = new HashMap<>();
        for (Variable variable : AppCore.app.getVariables()) {
            for (String value : variable.getDefaultValues()) {
                addVariableValues(variable.getId(), variable.getName(), value);
            }
        }


        for (EdgeCell edge : edgeCells) {


            for (EdgeCellVariable cellVariable : edge.getVariablesValues()) {
                addVariableValues(cellVariable.getVariableId(), cellVariable.getName(), cellVariable.getValue());
            }
        }
        EventSource.fireEvent(EventType.CONSOLE_VARIABLES, new ArrayList<>(variables.values()));
    }

    private void addVariableValues(int variableId, String variableName, String value) {
        Variable variable = null;
        if (!variables.containsKey(variableId)) {
            variable = new Variable(variableId, variableName);
            variables.put(variableId, variable);
        } else
            variable = variables.get(variableId);

        variable.addValue(value);

    }

    private void GenerateKripke() {


        CPN2KripkeConverter = new CPN2KripkeConverter(cpn);
        kripke = CPN2KripkeConverter.convert();

        for (KripkeStructure structure : AppCore.app.getKripkeStructures().values()) {
            generateKripkeStucture(structure);
        }


    }

    private void generateKripkeStucture(KripkeStructure structure) {
        try {
            structure.cpnToKripke();
            EventSource.fireEvent(EventType.KRIPKE_STRUCTURE_CHANGE, structure.getId());
            EventSource.fireEvent(EventType.CONSOLE_RAWINPUT, structure.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CPNGroup getGroupByCell(mxCell cell) {
        return groups.stream().filter(g -> g.originCell.getId().equals(cell.getId())).findFirst().orElse(null);
    }


}