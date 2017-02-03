package nl.rug.ds.bpm.verification.modelCheckers;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.Main;
import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.models.ConstraintResult;
import nl.rug.ds.bpm.editor.models.ModelChecker;
import nl.rug.ds.bpm.verification.models.kripke.State;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class MChecker extends AbstractChecker {

    public MChecker(ModelChecker checkerSettings) {
        super("MCheck");
        this.checkerSettings = checkerSettings;
        checkerPath = "/mch.jar";    //checkerSettings.getLocation();
        inputChecker = new StringBuilder();
    }

    public void createInputData() {
        if (kripkeModel == null)
            inputChecker = new StringBuilder();

        inputChecker.append(convertStates());
        inputChecker.append(convertFORMULAS());
    }

    protected String convertStates() {
        StringBuilder f = new StringBuilder();
        f.append("{\n");

        for (State s : kripkeModel.getStates()) {
            if (kripkeModel.getInitial().contains(s))
                f.append(">  ");
            else
                f.append("   ");

            f.append(s.getID().replace("S", "") + "  ");

            for (State n : s.getNextStates())
                f.append(n.getID().replace("S", "") + "  ");

            for (String ap : s.getAtomicPropositions())
                f.append((ap.toLowerCase().replace(".", "_")) + "  ");
            if (s.getAtomicPropositions().size() == 0) {
                f.append("silent ");
            }


            //for (String ap : s.getConditionAP())
            //    f.append(ap.toLowerCase() + "  ");

            f.append("\n");
        }

        f.append("}\n\n");

        return f.toString();
    }

    protected Process createProcess() {
        try {
            File jar = new File(System.getProperty("user.home") + "/VxBPM/mch.jar");
            if (!jar.exists()) {
                jar.getParentFile().mkdirs();
                InputStream link = (Main.class.getResourceAsStream(checkerPath));
                Files.copy(link, jar.getAbsoluteFile().toPath());
                jar.deleteOnExit();
            }

            Process proc = Runtime.getRuntime().exec("java -jar " + jar.getAbsoluteFile() + " " + file.getAbsolutePath());
            return proc;
        } catch (Throwable t) {
            t.printStackTrace();
            outputChecker.append("WARNING: Could not callModelChecker NuSMV2.\n");
            outputChecker.append("WARNING: No checks were performed.\n");
            return null;
        }
    }

    protected void checkResults(List<String> resultLines, List<String> errorLines) {
        List<String> results = new ArrayList<>();
        ConstraintResult currentConstraint = null;

        for (String line : resultLines) {
            if (line.startsWith("CTL") || line.startsWith("LTL")) {
                currentConstraint = getConstraintResult(line);
            }
            if (currentConstraint != null) {
                if (line.startsWith("Satisfied") || line.startsWith("Satisified")) {
                    currentConstraint.setStatus(ConstraintStatus.Valid);
                    currentConstraint = null;
                } else if (line.startsWith("Not satisfied")) {
                    currentConstraint.setStatus(ConstraintStatus.Invalid);
                    currentConstraint = null;
                }
            }
        }


    }

    private ConstraintResult getConstraintResult(String resultFormula) {
        String formula = trimFormula(resultFormula);

        ConstraintResult constraint = constraintsResults
                .stream().filter(c -> trimFormula(c.formula.getTypeName() + ":" + c.formulaInput).equalsIgnoreCase(formula))
                .filter(c -> c.hasStatus(ConstraintStatus.None)).findFirst().orElse(null);
        if (constraint == null)
            Console.error("rawoutput matching FAILED! " + formula);
        return constraint;
    }
}
