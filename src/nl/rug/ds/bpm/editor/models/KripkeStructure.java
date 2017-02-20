package nl.rug.ds.bpm.editor.models;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.transformer.CPNConverter;
import nl.rug.ds.bpm.verification.comparators.StringComparator;
import nl.rug.ds.bpm.verification.constraints.Formula;
import nl.rug.ds.bpm.verification.modelCheckers.AbstractChecker;
import nl.rug.ds.bpm.verification.modelCheckers.MChecker;
import nl.rug.ds.bpm.verification.modelCheckers.NuSMVChecker;
import nl.rug.ds.bpm.verification.modelCheckers.NuXMVChecker;
import nl.rug.ds.bpm.verification.modelConverters.KripkeConverter;
import nl.rug.ds.bpm.verification.models.cpn.CPN;
import nl.rug.ds.bpm.verification.models.cpn.Variable;
import nl.rug.ds.bpm.verification.models.kripke.Kripke;
import nl.rug.ds.bpm.verification.models.kripke.State;

import java.util.*;

/**
 * Created by Mark on 28-12-2015.
 */
public class KripkeStructure {
    int id;
    String name;
    String rawInput;
    String rawOutput;
    public HashMap<Integer, List<String>> values2;
    List<IConstraintHolder> edges = new ArrayList<>();

    Set<Integer> namedVariables = new HashSet<>();
    private Kripke kripke;
    public KripkeConverter kripkeConverter;
    List<ConstraintResult> constraintResults = new ArrayList<>();

    public KripkeStructure(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addConstraint(IConstraintHolder edge) {
        this.edges.add(edge);
    }

    public void mergeNamedVariables(List<Integer> namedVariables) {
        this.namedVariables.addAll(namedVariables);
    }

    public String getRawInput() {
        return rawInput;
    }

    public String getRawOutput() {
        return rawOutput;
    }

    public Kripke getKripke() {
        return kripke;
    }

    public String getName() {
        if (this.name == null) {
            List<String> nameList = new ArrayList<>();
            for (int variableId : namedVariables) {
                Variable variable = AppCore.app.converter.variables.get(variableId);
                List<String> values = values2.get(variableId);
                nameList.add(String.format("%s {%s}", variable.getName(), String.join(",", values)));
            }
            if (nameList.size() == 0)
                this.name = "Full model";
            else
                this.name = String.join(" & ", nameList);

        }
        return this.name;
    }

    public List<ConstraintResult> getConstraintResults() {
        return constraintResults;
    }

    public HashMap<Integer, Variable> getVariables() {
        HashMap<Integer, Variable> variables = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : values2.entrySet()) {
            Variable variable = AppCore.app.converter.variables.get(entry.getKey());
            variables.put(variable.getId(), new Variable(
                    variable.getId(),
                    variable.getName(),
                    entry.getValue()
            ));
        }
        return variables;
    }

    public List<Variable> getVariableList() {
        return new ArrayList<>(getVariables().values());
    }


    public void cpnToKripke() {
        HashMap<Integer, Variable> variables = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : values2.entrySet()) {
            Variable variable = AppCore.app.converter.variables.get(entry.getKey());
            variables.put(variable.getId(), new Variable(
                    variable.getId(),
                    variable.getName(),
                    entry.getValue()
            ));
        }


        CPNConverter cpnConverter = new CPNConverter(variables);
        CPN cpn = cpnConverter.getCpn();

        cpn.setVariables(getVariableList());
        cpn.init();


        kripkeConverter = new KripkeConverter(cpn);

        kripke = kripkeConverter.convert();


        constraintResults = new ArrayList<>();

        TreeSet<String> unusedAps = (TreeSet) kripke.getAtomicPropositions().clone();


        unusedAps.removeAll(Arrays.asList("start", "end", "silent"));

        for (IConstraintHolder constraintEdge : edges) {
            for (Formula formula : constraintEdge.getFormula()) {
                ConstraintResult constraintResult = new ConstraintResult(constraintEdge, formula, formula.getName());
                constraintResult.setFormulaInput(constraintResult.getCheckerInput(kripke));
                constraintResults.add(constraintResult);
                unusedAps.removeAll(constraintResult.getSourceIds());
                unusedAps.removeAll(constraintResult.getTargetIds());
            }
        }
        
        //add ghost State to host AP missing from model;
        TreeSet<String> missingAP = new TreeSet<>(new StringComparator());
        missingAP.addAll(AppCore.gui.importService.getAP());
        missingAP.removeAll(kripke.getAtomicPropositions());
        missingAP.add("silent");
        
        List<String> missingList = new ArrayList<>(AppCore.gui.importService.getAP());
        missingList.removeAll(kripke.getAtomicPropositions());
        kripke.addAtomicPropositions(missingList);
            
        State ghost = new State("ghost", missingAP);
        ghost.addNext(ghost);
        kripke.addState(ghost);
        
        if(AppCore.app.modelReductionEnabled) {
            kripkeConverter.propositionOptimize(unusedAps);
            int m = kripkeConverter.stutterOptimize();
        }
        AbstractChecker checker = null;

        ModelChecker checkerSettings = AppCore.app.selectedModelChecker();
        switch (checkerSettings.getId()) {
            case "NuSMV":
                checker = new NuSMVChecker(checkerSettings);
                break;
            case "NuXMV":
                checker = new NuXMVChecker(checkerSettings);
                break;
            case "MCheck":
                checker = new MChecker(checkerSettings);
                break;
        }

        checker.setKripkeModel(kripke);
        checker.setConstraintsResults(constraintResults);


        checker.createInputData();
        rawInput = checker.getInputChecker();
        checker.callModelChecker();

        rawOutput = checker.getOutputChecker();


        EventSource.fireEvent(EventType.KRIPKE_CONSTRAINT_CHANGE, this.getId());
        EventSource.fireEvent(EventType.CONSOLE_RAWOUTPUT, this.getId());
    }
}
