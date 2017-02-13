package nl.rug.ds.bpm.editor.services;

import nl.rug.ds.bpm.editor.models.ImportConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p256867 on 9-2-2017.
 */
public class ImportService {
    private List<ImportConstraint> importConstraints;

    public ImportService() {
        importConstraints = new ArrayList<>();
    }

    public void addImportConstraint(ImportConstraint importConstraint) {
        importConstraints.add(importConstraint);
    }

    public List<ImportConstraint> getImportConstraints() {
        return importConstraints;
    }

    public void clear() {
        importConstraints.clear();
    }
}
