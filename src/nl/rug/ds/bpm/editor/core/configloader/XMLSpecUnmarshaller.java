package nl.rug.ds.bpm.editor.core.configloader;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.ConstraintType;
import nl.rug.ds.bpm.editor.diagramViews.bpmn.BPMNGraph;
import nl.rug.ds.bpm.editor.models.Constraint;
import nl.rug.ds.bpm.editor.models.ImportConstraint;
import nl.rug.ds.bpm.editor.models.SpecificationLanguage;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.services.ImportService;
import nl.rug.ds.bpm.jaxb.xmlspec.AtomicProposition;
import nl.rug.ds.bpm.jaxb.xmlspec.Specification;
import nl.rug.ds.bpm.jaxb.xmlspec.XmlSpec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by p256867 on 7-2-2017.
 */
public class XMLSpecUnmarshaller {
    private BPMNGraph graph;
    private XmlSpec xmlSpec;
    private HashMap<String, InputCell> inputCells;

    public XMLSpecUnmarshaller(File file) {
        graph = AppCore.gui.getGraph();
        inputCells = new HashMap<>();

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
        for(AtomicProposition p: xmlSpec.getAtomicPropositions()) {
            if (!p.getName().equalsIgnoreCase("silent")) {
                InputCell cell = graph.getByName(p.getName());
                if (cell != null) {
                    inputCells.put(p.getId(), cell);
                } else {
                    Console.error("Imported AP not in model:" + p.getName());
                }
            }
        }
    }

    private void importSpecifications() {
        ImportService importService = AppCore.gui.importService;
        int id = 0;

        for(Specification specification: xmlSpec.getSpecifications()) {
            //find cell with first AP
            InputCell cell = null;
            Pattern pattern = Pattern.compile("\\{\\w{1,2}\\}");
            Matcher matcher = pattern.matcher(specification.getValue());
            if (matcher.find()) {
                cell = inputCells.get(matcher.group(0));
            }

            //replace AP in formula
            String s = specification.getValue();
            for(String sid: inputCells.keySet())
                s.replaceAll(sid, inputCells.get(sid).getId());

            List<String> formulas = new ArrayList<>();
            formulas.add(s);

            //find specification language
            SpecificationLanguage lang = null;
            Iterator<SpecificationLanguage> i = AppCore.app.config.getSpecificationLanguages().iterator();
            while(lang == null && i.hasNext()) {
                SpecificationLanguage l = i.next();
                if(l.getName().equalsIgnoreCase(specification.getLanguage()))
                    lang = l;
            }

            Constraint constraint = new Constraint(specification.getType() + id++, formulas, lang, ConstraintType.Import);
            ImportConstraint importConstraint = new ImportConstraint(cell, constraint);

            importService.addImportConstraint(importConstraint);
        }
    }
}
