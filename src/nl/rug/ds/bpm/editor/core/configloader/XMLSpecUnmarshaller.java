package nl.rug.ds.bpm.editor.core.configloader;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstraintType;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.Arrow;
import nl.rug.ds.bpm.editor.models.Constraint;
import nl.rug.ds.bpm.editor.models.ImportConstraint;
import nl.rug.ds.bpm.editor.models.SpecificationLanguage;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import nl.rug.ds.bpm.editor.services.ImportService;
import nl.rug.ds.bpm.jaxb.xmlspec.AtomicProposition;
import nl.rug.ds.bpm.jaxb.xmlspec.Specification;
import nl.rug.ds.bpm.jaxb.xmlspec.XmlSpec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by p256867 on 7-2-2017.
 */
public class XMLSpecUnmarshaller {
    private BPMNGraph graph;
    private XmlSpec xmlSpec;
    private HashMap<String, InputCell> inputCells;
    private HashMap<String, String> missingAP;
    private ImportService importService;

    public XMLSpecUnmarshaller(File file) {
        graph = AppCore.gui.getGraph();
        inputCells = new HashMap<>();
        missingAP = new HashMap<>();
        importService = AppCore.gui.importService;

        try {
            JAXBContext context = JAXBContext.newInstance(XmlSpec.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            xmlSpec = (XmlSpec) unmarshaller.unmarshal(file);
            try {
                importAtomicPropositions();
                importSpecifications();
            } finally {
                graph.getModel().endUpdate();
                graph.refresh();
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void importAtomicPropositions() {
        int m = 0;
        for(AtomicProposition p: xmlSpec.getAtomicPropositions()) {
            if (!p.getName().equalsIgnoreCase("silent")) {
                InputCell cell = graph.getByName(p.getName());
                if (cell != null) {
                    inputCells.put(p.getId(), cell);
                } else {
                    missingAP.put(p.getId(), "NiM" + m);
                    importService.addMissingAP( "NiM" + m);
                    Console.error("Imported AP " + p.getName() + " not in model. Adding as NiM" + m);
                    m++;
                }
            }
        }
        importService.addMissingAP( "silent");
    }

    private void importSpecifications() {
        int id = 0;

        for(Specification specification: xmlSpec.getSpecifications()) {
            //find source cell
            InputCell cell = inputCells.get(specification.getSource());

            if (cell != null) {
                //replace AP in formula & find cells used
                String s = specification.getValue();
                List<SuperCell> cells = new ArrayList<>();
    
                for (String sid : inputCells.keySet()) {
                    if(s.contains(sid)) {
                        cells.add(inputCells.get(sid));
                        String[] tids = inputCells.get(sid).getCpnTransitionIds();
                        String cids = "";
                        if (tids.length > 1) {
                            cids = cids + "(";
                            Iterator<String> iterator = Arrays.asList(tids).iterator();
                            while (iterator.hasNext()) {
                                cids = cids + iterator.next();
                                if (iterator.hasNext())
                                    cids = cids + " | ";
                            }
                            cids = cids + ")";
                        } else
                            cids = tids[0];
                        s = s.replaceAll(Pattern.quote(sid), cids);
                    }
                }
                for(String sid: missingAP.keySet()) {
                    s = s.replaceAll(Pattern.quote(sid), missingAP.get(sid));
                }
    
                List<String> formulas = new ArrayList<>();
                formulas.add(s);
    
                //find specification language
                SpecificationLanguage lang = null;
                Iterator<SpecificationLanguage> i = AppCore.app.config.getSpecificationLanguages().iterator();
                while (lang == null && i.hasNext()) {
                    SpecificationLanguage l = i.next();
                    if (l.getId().equalsIgnoreCase(specification.getLanguage()))
                        lang = l;
                }
    
                //find arrow
                Arrow arrow;
                String arrowId = "Import";
                if (AppCore.app.config.getArrowMap().containsKey(arrowId)) {
                    arrow = AppCore.app.config.getArrowMap().get(arrowId);
    
                    Constraint constraint = new Constraint(specification.getType() + id++, formulas, arrow, lang, ConstraintType.Import);
                    ImportConstraint importConstraint = new ImportConstraint(specification.getType(), cell, cells, constraint);
    
                    importService.addImportConstraint(importConstraint);
                }
                else
                    Console.error("Error: Failed to import specification, because arrow " + arrowId + " not found");
            }
            else
                Console.error("Error: Failed to import specification " + specification.getValue() + ", because source " + specification.getSource() + " is missing.");
        }
    }
}
