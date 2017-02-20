package nl.rug.ds.bpm.editor.services;

import nl.rug.ds.bpm.editor.models.ImportConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by p256867 on 9-2-2017.
 */
public class ImportService {
    private List<ImportConstraint> importConstraints;
    private List<String> usedAP;

    public ImportService() {
        importConstraints = new ArrayList<>();
        usedAP = new ArrayList<>();
    }

    public void addImportConstraint(ImportConstraint importConstraint) {
        importConstraints.add(importConstraint);
    }

    public List<ImportConstraint> getImportConstraints() {
        return importConstraints;
    }
    
    public void addAP(String ap) { usedAP.add(ap); }
    
    public void addAllAP(List<String> ap) { usedAP.addAll(ap); }
    
    public void addAllAP(String[] ap) { addAllAP(new ArrayList<String>(Arrays.asList(ap))); }
    
    public List<String> getAP() { return usedAP; }

    public void clear() {
        importConstraints.clear();
        usedAP.clear();
    }
}
