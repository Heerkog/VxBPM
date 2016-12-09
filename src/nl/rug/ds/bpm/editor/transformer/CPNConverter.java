package nl.rug.ds.bpm.editor.transformer;

import com.mxgraph.model.mxCell;
import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.Converter;
import nl.rug.ds.bpm.editor.models.EdgeCellVariable;
import nl.rug.ds.bpm.editor.models.InputElement;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputEdgeCell;
import nl.rug.ds.bpm.editor.transformer.ExtraConverters.InclusiveConverter;
import nl.rug.ds.bpm.verification.models.cpn.Arc;
import nl.rug.ds.bpm.verification.models.cpn.CPN;
import nl.rug.ds.bpm.verification.models.cpn.CPNElement;
import nl.rug.ds.bpm.verification.models.cpn.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class CPNConverter {
    float cellSpacing = 50;
    ArrayList<CPNGroup> groups = new ArrayList<>();
    Converter converter;
    CPN cpn;
    HashMap<String, List<CPNElement>> bpmnToCPNLookup;
    HashMap<Integer, Variable> availableVariables;

    public CPNConverter(HashMap<Integer, Variable> availableVariables) {
        try {
            this.availableVariables = availableVariables;
            converter = AppCore.app.converter;
            cpn = new CPN();
            bpmnToCPNLookup = new HashMap<>();
            CPNGroup.TransitionCount = 0;//Reset counter
            CPNGroup.PlaceCount = 0;//Reset counter


            for (InputCell cell : converter.inputCells) {
                InputElement inputELement = cell.getInputElement();
                int startLeft = (int) cell.getGeometry().getX();
                int startTop = (int) cell.getGeometry().getY();
                CPNGroup group = new CPNGroup(this, cell, inputELement.getCpnTranformerElement(), startLeft, startTop, null);
                groups.add(group);

                if (cell.getCellEvent() != null && cell.getCellEvent().getOutgoingEdges().size() > 0) {
                    InputElement inputEventELement = cell.getCellEvent().getInputElement();
                    CPNGroup groupEvent = new CPNGroup(this, cell.getCellEvent(), inputEventELement.getCpnTranformerElement(), 0, 0, null);
                    groups.add(groupEvent);
                }
            }


            for (InputCell cell : converter.inputCells) {
                if (!cell.getInputElement().hasTranformationElements())
                    continue;

                List<CPNEdge> edges = getIncomingEdges(cell.getIncomingEdges(), cell, null);
                int index = 0;
                for (CPNEdge edge : edges) {
                    CPNGroup sourceGroup = getGroupByCell(edge.getSource());
                    CPNGroup targetGroup = getGroupByCell(edge.getTarget());


                    CPNElement CPNElementTarget = targetGroup.getIncomingElement(index, groups);
                    CPNElement CPNElementSource = sourceGroup.getOutGoingElement(index, groups);
                    Arc customOutgoingContionArc = null;
                    if (sourceGroup.outgoingSubGroups.size() > 0) {
                        CPNGroup last = sourceGroup.outgoingSubGroups.get(sourceGroup.outgoingSubGroups.size() - 1);
                        customOutgoingContionArc = last.outGoingConstraintArc;

                    }

                    if (CPNElementTarget == null || CPNElementSource == null)
                        continue;
                    List<Variable> variables = new ArrayList<>();
                    List<String> conditions = new ArrayList<>();

                    for (EdgeCellVariable variable : edge.getConditions()) {
                        conditions.add(String.format("%s %s %s", variable.getName(), variable.getCondition(), variable.getValue()));
                        Variable var = availableVariables.get(variable.getVariableId());
                        if (var.getValues().isEmpty())
                            var.getValues().add("NULL");
                        variables.add(var);
                    }
                    Arc a = new Arc(CPNElementSource, CPNElementTarget, 1, "");


                    if (customOutgoingContionArc != null) {
                        customOutgoingContionArc.setCondition(String.join(" && ", conditions));
                        customOutgoingContionArc.setVariables(variables);

                    } else {
                        a.setCondition(String.join(" && ", conditions));
                        a.setVariables(variables);

                    }

                    cpn.addArc(a);
                    index++;
                }
                if (cell.getCellEvent() != null && cell.getCellEvent().getOutgoingEdges().size() > 0) {
                    InputElement inputELement = cell.getCellEvent().getInputElement();
                    CPNGroup group = getGroupByCell(cell.getCellEvent());

                    CPNGroup sourceGroup = getGroupByCell((mxCell) cell);
                    CPNElement CPNElementSource = sourceGroup.getEventElement();

                    for (mxCell edge : cell.getCellEvent().getOutgoingEdges()) {
                        CPNElement eventIncoming = group.getIncomingElement2(0);

                        Arc a = new Arc(CPNElementSource, eventIncoming, 1, "");
                        cpn.addArc(a);

                        //edge = edge;
                        // CPNGroup targetGroup = getGroupByCell((mxCell) edge.getTarget());


                    }

                }

            }
            for (InputCell cell : converter.inputCells) {
                if (cell.isEdge()) {
                    InputEdgeCell edge = (InputEdgeCell) cell;
                    if (edge.getSource() != null && edge.getTarget() != null) {
                        CPNGroup sourceGroup = getGroupByCell((mxCell) edge.getSource());
                        CPNGroup targetGroup = getGroupByCell((mxCell) edge.getTarget());
                        CPNElement CPNElementTarget = targetGroup.getMessageIncomingElement();
                        CPNElement CPNElementSource = sourceGroup.getMessageOutgoingElement();


                        CPNGroup eventGroup = getGroupByCell(cell);
                        Arc a = new Arc(CPNElementSource, eventGroup.getIncomingElement2(0), 1, "");
                        cpn.addArc(a);

                        Arc a2 = new Arc(eventGroup.getOutGoingElement(0, null), CPNElementTarget, 1, "");
                        cpn.addArc(a2);
                    }
                }
            }


            new InclusiveConverter(this);
            //fixOverlappingGroups();
        } catch (Exception e) {
            e = e;
            e.printStackTrace();
        }
    }


    private List<CPNEdge> getIncomingEdges(List<EdgeCell> edges, InputCell target, List<EdgeCellVariable> conditions) {
        List<CPNEdge> returnEdges = new ArrayList<>();
        if (conditions == null)
            conditions = new ArrayList<>();


        for (EdgeCell edge : edges) {
            List<EdgeCellVariable> conditions2 = new ArrayList<>(conditions);
            conditions2.addAll(edge.getVariablesValues());

            InputCell source = (InputCell) edge.getSource();
            if (source == null)
                continue;
            if (!source.getInputElement().hasTranformationElements()) {
                returnEdges.addAll(getIncomingEdges(source.getIncomingEdges(), target, conditions2));
            } else {
                returnEdges.add(new CPNEdge(source, target, conditions2));
            }
        }
        return returnEdges;
    }

    private class CPNEdge {
        InputCell source;
        InputCell target;
        List<EdgeCellVariable> conditions;

        public CPNEdge(InputCell source, InputCell target, List<EdgeCellVariable> conditions) {
            this.source = source;
            this.target = target;
            this.conditions = conditions;
        }

        public InputCell getSource() {
            return source;
        }

        public InputCell getTarget() {
            return target;
        }

        public List<EdgeCellVariable> getConditions() {
            return conditions;
        }

    }


    public ArrayList<CPNGroup> getGroups() {
        return this.groups;
    }

    public HashMap<String, List<CPNElement>> getBpmnToCPNLookup() {
        return this.bpmnToCPNLookup;
    }

    private CPNGroup getGroupByCell(mxCell cell) {
        return groups.stream().filter(g -> g.originCell.getId().equals(cell.getId())).findFirst().orElse(null);
    }

    /*private void fixOverlappingGroups() {
        boolean moved = false;
        while (moved) {
            for (CPNGroup group : groups) {
                moved = false;
                int right = group.right + 20;
                int bottom = group.bottom + 20;
                for (CPNGroup moveGroup : groups) {
                    int moveRight = 0;
                    int moveBottom = 0;
                    if (group.overlaps(moveGroup) && moveGroup.left >= group.left) {
                        if (moveGroup.left > group.left) {
                            if (moveGroup.left < group.right + 20)
                                moveRight = group.right - moveGroup.left + 20;
                        }
                        if (moveGroup.top > group.top) {
                            if (moveGroup.top < group.bottom + 20)
                                moveBottom = group.bottom - moveGroup.top + 20;
                        }
                        if (moveRight > 0 || moveBottom > 0) {
                            if (moveRight > 0 && moveRight >= moveBottom) {
                                moveGroup.moveRight(moveRight);
                            } else if (moveBottom > 0) {
                                moveGroup.moveBottom(moveBottom);
                            }
                            moved = true;
                            break;
                        }
                    }
                }
            }
        }
    }*/


    public CPN getCpn() {
        return cpn;
    }


}
