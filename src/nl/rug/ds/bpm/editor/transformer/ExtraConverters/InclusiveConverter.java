package nl.rug.ds.bpm.editor.transformer.ExtraConverters;

import nl.rug.ds.bpm.editor.transformer.CPNConverter;
import nl.rug.ds.bpm.editor.transformer.CPNGroup;
import nl.rug.ds.bpm.verification.models.cpn.Arc;
import nl.rug.ds.bpm.verification.models.cpn.CPNElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Kloosterhuis.
 */
public class InclusiveConverter {
    CPNConverter converter;

    public InclusiveConverter(CPNConverter converter) {
        this.converter = converter;
        findInclusiveElements();
    }

    private void findInclusiveElements() {
        List<CPNGroup> groups = converter.getGroups()
                .stream()
                .filter(g -> g.originCell.getInputElement().isInclusiveElement())
                .collect(Collectors.toList());

        for (CPNGroup group : groups) {
            findInclusive(group, group);
        }
    }

    private void findInclusive(CPNGroup targetGroup, CPNGroup node) {

        for (CPNGroup group : node.getParentGroups()) {
            if (group.originCell.getInputElement().isInclusiveElement()) {
                linkForkMerge(group, targetGroup);
                findInclusive(group, group);
            }
            findInclusive(targetGroup, group);

        }
    }

    private void linkForkMerge(CPNGroup sourceGroup, CPNGroup targetGroup) {
        CPNElement source = sourceGroup.getOutgoingElement2(0);
        CPNElement target = targetGroup.getIncomingElement2(0);
        Arc a = new Arc(source, target, 1, "REVERT HIER", new ArrayList<>());
        converter.getCpn().addArc(a);

    }

}
