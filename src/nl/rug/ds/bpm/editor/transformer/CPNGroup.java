package nl.rug.ds.bpm.editor.transformer;

import nl.rug.ds.bpm.editor.Console;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.Converter;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.verification.models.cpn.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mark Kloosterhuis.
 */
public class CPNGroup {
    CPNConverter cpnConverter;
    public ArrayList<ElementGeometry> elements;
    public InputCell originCell;
    public float top = 0, right = -1, bottom = -1, left = -1;
    float startLeft, startTop;
    CPNGroup parent;
    CPNTranformerElement tranformerElement;
    String cellName;
    HashMap<String, CPNElement> arcLookupList;
    public List<CPNGroup> incomingSubGroups = new ArrayList<>();
    public List<CPNGroup> outgoingSubGroups = new ArrayList<>();
    HashMap<String, String> arcVariables;
    public static int PlaceCount = 0;
    public static int TransitionCount = 0;
    Converter converter;
    public Arc outGoingConstraintArc = null;

    public CPNGroup(CPNConverter cpnConverter, InputCell cell, CPNTranformerElement tranformerElement, float startLeft, float startTop, CPNGroup parent) {
        this.converter = AppCore.app.converter;
        this.cpnConverter = cpnConverter;
        elements = new ArrayList<>();
        arcLookupList = new HashMap<>();
        this.originCell = cell;
        this.tranformerElement = tranformerElement;
        this.startLeft = startLeft;
        this.startTop = startTop;
        this.parent = parent;
        cellName = originCell.getCellProperties().getId() + "." + originCell.getCellProperties().getName();
        if (parent != null)
            outGoingConstraintArc = parent.outGoingConstraintArc;
        setDefaultArcVariables();
        createNodes();
    }

    private void addLinkCpnELement(CPNElement element) {
        if (!cpnConverter.getBpmnToCPNLookup().containsKey(this.originCell.getVisibleId()))
            cpnConverter.getBpmnToCPNLookup().put(this.originCell.getVisibleId(), new ArrayList<>());
        cpnConverter.getBpmnToCPNLookup().get(this.originCell.getVisibleId()).add(element);

    }

    public CPNElement getMessageIncomingElement() {
        CPNTransformerNode incomingElement = tranformerElement.getIncomingMessageElement();
        CPNElement element = arcLookupList.get(incomingElement.getId());
        return element;
    }

    public CPNElement getMessageOutgoingElement() {
        CPNTransformerNode outgoingElement = tranformerElement.getOutgoingMessageElement();
        CPNElement element = arcLookupList.get(outgoingElement.getId());
        return element;
    }

    public CPNElement getEventElement() {
        return arcLookupList.get(tranformerElement.getEventElement().getId());
    }

    private void setDefaultArcVariables() {
        arcVariables = new HashMap<>();
        arcVariables.put("incomingCount", String.valueOf(originCell.getIncomingEdges().size()));
        arcVariables.put("outgoingCount", String.valueOf(originCell.getOutgoingEdges().size()));
        originCell.getInputElement().getAttributes().forEach((a) -> {
            String value = String.valueOf(originCell.getCellProperties().getCellProperty(a.getName()).getValue());
            arcVariables.put(a.getName(), value);
        });
    }

    public void createNodes() {
        if (tranformerElement != null) {
            parsePlaces();
            parseTransitions();
            parseArcs();
        }
    }

    private void parsePlaces() {
        for (CPNTransformerNode node : tranformerElement.getNodes()) {
            if (node instanceof TPlace) {
                String id = node.name.replace("{x}", String.valueOf(PlaceCount));
                Place p = new Place(id, cellName);
                p.setConvertSourceId(originCell.getId());
                p.setLabel(this.originCell.getValue() + "." + id);
                p.setSourceElementId(node.uniqueId);
                p.setX(node.x * cpnConverter.cellSpacing);
                p.setY(node.y * cpnConverter.cellSpacing);
                addLinkCpnELement(p);

                cpnConverter.cpn.addPlace(p);
                if (node.token)
                    cpnConverter.cpn.addToken(p);

                arcLookupList.put(node.getId(), p);
                this.addElement(p, false);
            }
            PlaceCount++;
        }
    }

    private void parseTransitions() {
        for (CPNTransformerNode node : tranformerElement.getNodes()) {
            if (node instanceof TTransition) {
                String id = node.name.replace("{x}", String.valueOf(TransitionCount)).replace("{id}", originCell.getVisibleId());
                //String id = this.originCell.getValue() + "." + String.valueOf(TransitionCount);


                Transition t = new Transition(id, cellName);
                t.setParentIds(originCell.getParentIds());
                t.setConvertSourceId(originCell.getId());
                t.setLabel(this.originCell.getValue() + "." + TransitionCount);


                t.setSourceElementId(node.uniqueId);
                t.setX((node.x * cpnConverter.cellSpacing));
                t.setY((node.y * cpnConverter.cellSpacing));
                addLinkCpnELement(t);

                //t.setX(startLeft + (node.x * cpnConverter.cellSpacing));
                //t.setY(startTop + (node.y * cpnConverter.cellSpacing));
                cpnConverter.cpn.addTransition(t);
                arcLookupList.put(node.getId(), t);
                this.addElement(t, false);
                TransitionCount++;
            }
        }
    }

    private void parseArcs() {
        for (CPNTransformerNode node : tranformerElement.getNodes()) {
            if (node instanceof TArc) {
                TArc tArc = (TArc) node;
                if ((arcLookupList.containsKey(tArc.from) || parent.arcLookupList.containsKey(tArc.from)) &&
                        (arcLookupList.containsKey(tArc.to) || parent.arcLookupList.containsKey(tArc.to))) {
                    CPNElement source = arcLookupList.get(((TArc) node).from);
                    CPNElement target = arcLookupList.get(((TArc) node).to);
                    if (source == null && parent != null)
                        source = parent.arcLookupList.get(((TArc) node).from);

                    if (target == null && parent != null)
                        target = parent.arcLookupList.get(((TArc) node).to);

                    String condition = tArc.getCondition(arcVariables);
                    int weight = tArc.getWeight(arcVariables);

                    Arc a = new Arc(source, target, weight, condition, getDefaultAvailableVariables(condition));
                    a.setSourceElementId(node.uniqueId);

                    if (node.x != 0 || node.y != 0) {
                        a.setX(node.x * cpnConverter.cellSpacing);
                        a.setY(node.y * cpnConverter.cellSpacing);
                    }
                    cpnConverter.cpn.addArc(a);

                    if (tArc.hasOutgoingCondition)
                        outGoingConstraintArc = a;

                } else
                    Console.log("Source/Target not found!");
            }
        }
    }

    public List<Variable> getDefaultAvailableVariables(String condition) {
        List<Variable> variables = new ArrayList<>();
        if (condition != null && !condition.isEmpty()) {
            for (Variable defaultVariable : this.tranformerElement.getVariables()) {
                Variable var = cpnConverter.availableVariables.get(defaultVariable.getId());
                variables.add(var);
            }
        }

        return variables;

    }

    public CPNElement getIncomingElement2(int index) {
        List<CPNTransformerNode> incomingElements = tranformerElement.getIncomingElements();
        CPNTransformerNode incomingElement = incomingElements.get(index % incomingElements.size());

        CPNElement CPNElementSource = this.getCPNElementByUUId(incomingElement.uniqueId);
        if (CPNElementSource != null) {
            return CPNElementSource;
        } else {
            return incomingSubGroups.get(index).getIncomingElement2(0);
        }
    }


    public CPNElement getOutgoingElement2(int index) {
        List<CPNTransformerNode> outgoingElements = tranformerElement.getOutgoingElements();
        CPNTransformerNode outgoingElement = outgoingElements.get(index % outgoingElements.size());

        CPNElement CPNElementSource = this.getCPNElementByUUId(outgoingElement.uniqueId);
        if (CPNElementSource != null) {
            return CPNElementSource;
        } else {
            return outgoingSubGroups.get(index).getOutgoingElement2(0);
        }
    }


    public CPNElement getIncomingElement(int index, ArrayList<CPNGroup> groups) {

        if (tranformerElement.getIncomingElements().size() == 0)
            return null;

        List<CPNTransformerNode> incomingElements = tranformerElement.getIncomingElements();
        int incomingIndex = index % incomingElements.size();
        if (incomingElements.size() <= incomingIndex)
            return null;
        CPNTransformerNode incomingElement = incomingElements.get(incomingIndex);

        CPNElement CPNElementSource = this.getCPNElementByUUId(incomingElement.uniqueId);
        if (CPNElementSource != null) {
            return CPNElementSource;
        } else {
            CPNTransformerNode node = tranformerElement.getById(incomingElement.getId());
            if (node instanceof CPNTranformerElement) {
                CPNTranformerElement transformer = (CPNTranformerElement) node;
                float top = node.y;
                if (incomingSubGroups.size() > 0) {
                    float height = incomingSubGroups.get(incomingSubGroups.size() - 1).bottom - incomingSubGroups.get(incomingSubGroups.size() - 1).top;
                    top += incomingSubGroups.size() * height;
                    top += 20;
                    if (top + height > bottom || bottom == -1)
                        bottom = top + height;
                }


                CPNGroup subGroup = new CPNGroup(cpnConverter, this.originCell, transformer, startLeft + node.x, top, this);
                subGroup.top = top;
                subGroup.bottom += top;
                incomingSubGroups.add(subGroup);
                for (ElementGeometry geoNode : subGroup.elements) {
                    this.addElement(geoNode, true);
                }
                return subGroup.getIncomingElement(0, groups);
            }
            return null;
        }
    }

    public CPNElement getOutGoingElement(int index, ArrayList<CPNGroup> groups) {
        List<CPNTransformerNode> outgoingElements = tranformerElement.getOutgoingElements();


        CPNTransformerNode outgoingElement = outgoingElements.get(index % outgoingElements.size());

        CPNElement CPNElementSource = this.getCPNElementByUUId(outgoingElement.uniqueId);
        if (CPNElementSource != null) {
            return CPNElementSource;
        } else {
            CPNTransformerNode node = tranformerElement.getById(outgoingElement.getId());
            if (node instanceof CPNTranformerElement) {
                CPNTranformerElement transformer = (CPNTranformerElement) node;
                float top = node.y;
                if (outgoingSubGroups.size() > 0) {
                    float height = outgoingSubGroups.get(outgoingSubGroups.size() - 1).bottom - outgoingSubGroups.get(outgoingSubGroups.size() - 1).top;
                    top += outgoingSubGroups.size() * height;
                    top += 20;
                    if (top + height > bottom || bottom == -1)
                        bottom = top + height;
                }


                CPNGroup subGroup = new CPNGroup(cpnConverter, this.originCell, transformer, startLeft + node.x, top, this);
                subGroup.top = top;
                subGroup.bottom += top;
                outgoingSubGroups.add(subGroup);


                for (ElementGeometry geoNode : subGroup.elements) {
                    this.addElement(geoNode, true);
                }
                return subGroup.getOutGoingElement(0, groups);
            }
            return null;
        }
    }

    public void addElement(ElementGeometry element, boolean skipAdd) {
        if (!skipAdd)
            elements.add(element);
        //top
        if (element.getY() < top)
            top = element.getY();

        //right
        if (element.getX() + element.getWidth() > right || right == -1)
            right = element.getX() + element.getWidth() + 10;

        //bottom
        if (element.getY() + element.getHeight() > bottom || bottom == -1)
            bottom = element.getY() + element.getHeight() + 10;

        //left
        if (element.getX() < left || left == -1)
            left = element.getX();
    }


    private CPNElement getCPNElementByUUId(UUID uniqueId) {
        return elements.stream()
                .filter(p -> CPNElement.class.isInstance(p))
                .map(c -> (CPNElement) c)
                .filter(g -> g.getSourceElementId().equals(uniqueId))
                .findFirst()
                .orElse(null);
    }

    public List<CPNGroup> getParentGroups() {
        List<CPNGroup> parents = new ArrayList<>();
        for (EdgeCell edge : originCell.getIncomingEdges()) {
            CPNGroup group = cpnConverter.getGroups().stream().filter(g -> g.originCell.getId().equals(edge.getSource().getId())).findFirst().orElse(null);
            if (group != null) {
                parents.add(group);
            }
        }

        parents.stream().sorted((p, p2) -> Integer.compare((int) p.originCell.getSource().getGeometry().getY(), (int) p2.originCell.getSource().getGeometry().getY()));
        return parents;
    }

}