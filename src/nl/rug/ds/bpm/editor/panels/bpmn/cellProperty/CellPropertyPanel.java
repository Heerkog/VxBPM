package nl.rug.ds.bpm.editor.panels.bpmn.cellProperty;

import nl.rug.ds.bpm.editor.core.AppCore;
import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;
import nl.rug.ds.bpm.editor.models.InputCellConstraint;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;
import nl.rug.ds.bpm.editor.models.graphModels.SuperCell;
import nl.rug.ds.bpm.editor.panels.bpmn.cellProperty.fields.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Created by Mark Kloosterhuis.
 */
public class CellPropertyPanel implements IFormContainer
{
    List<IPropertyField> fields = new ArrayList<>();

    List<CellProperty> orderedFieldsList;
    JPanel panel, returnPanel;
    GridBagConstraints cons;
    SuperCell selectedCell;
    ConstrainSelector checkBoxList;
    GridBagConstraints checkBoxListConstrains;
    ConditionTable conditionTable;
    public static GridBagConstraints rowCons;
    InputContraintsTable inputContraint;
    InputConstraintEdit inputConstraintEdit;

    public CellPropertyPanel() {

        super();

        returnPanel = new JPanel(new GridBagLayout());

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.setLayout(new GridBagLayout());
        panel.setAlignmentY(JPanel.TOP_ALIGNMENT);


        checkBoxList = new ConstrainSelector();


        rowCons = new GridBagConstraints();
        rowCons.fill = GridBagConstraints.HORIZONTAL;
        rowCons.anchor = GridBagConstraints.NORTH;
        rowCons.weightx = 1.0;
        rowCons.gridx = 0;


        orderedFieldsList = new ArrayList<CellProperty>();

        conditionTable = new ConditionTable(this);
        inputConstraintEdit = new InputConstraintEdit(this);
        inputContraint = new InputContraintsTable(this,inputConstraintEdit);


        cons = new GridBagConstraints();

        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.weightx = 1;
        cons.weighty = 1;
        cons.gridx = 0;
        returnPanel.add(panel, cons);


        EventSource.addListener(EventType.SELECTION_CHANGED, e -> selectionChanged(e));

        EventSource.addListener(EventType.MODEL_CHECKER_CHANGE, e -> {
                    if (selectedCell instanceof ConstrainEdgeCell) {
                        setRelationCell();
                    }
                }

        );
    }

    public void addComponent(Component el) {
        panel.add(el, rowCons);
    }

    public JPanel getPanel() {
        return returnPanel;
    }


    private IPropertyField addField(CellProperty property) {
        IPropertyField field = null;
        switch (property.getFieldType()) {
            case TextField:
                field = new PropTextField(property, this, e -> updateCell());
                break;
            case CheckField:
                field = new CheckField(property, this, e -> updateCell());
                break;
            /*case ComboField:
                field = new ComboField(property, panel, values, e -> updateCell());
                break;*/
        }
        field.setValue(property.getValue());
        fields.add(field);
        return field;
    }
    public void setInputCell(InputCellConstraint inputCellConstraint){
        inputConstraintEdit.getPanel().setVisible(true);
        inputConstraintEdit.setInputCell(inputCellConstraint);
    }

    private void updateCell() {
        if (selectedCell != null) {
            for (IPropertyField field : fields) {
                field.getCellProperty().setValue(field.getValue());
            }
            selectedCell.updateLayout();
            AppCore.gui.getGraph().refresh();

        }
    }


    //public void
    private void selectionChanged(Object obj) {
        List<SuperCell> inputCells = AppCore.gui.getCellService().getSelectedCells();
        if (inputCells.size() == 0) {
            selectedCell = null;
        } else if (inputCells.size() == 1) {
            selectedCell = inputCells.get(0);
            fillForm();
        }
    }

    private void setRelationCell() {
        JScrollPane constraintsPanel = checkBoxList.getPanel();
        constraintsPanel.setPreferredSize(new Dimension(190, 100));
        constraintsPanel.updateUI();

        panel.add(constraintsPanel, rowCons);
        checkBoxList.setItems(selectedCell);
    }

    public void fillForm() {
        panel.removeAll();
        fields.clear();
        if (selectedCell != null) {
            for (CellProperty property : selectedCell.getCellProperties().properties) {
                addField(property);
            }
        }
        // conditionPanel.addToParent();

        if (selectedCell instanceof EdgeCell || selectedCell instanceof ConstrainEdgeCell) {
            conditionTable.addToParent();
            EdgeCell relationEdgeCell = (EdgeCell) selectedCell;
            conditionTable.setVariables(relationEdgeCell.getVariablesValues());
        }
        if (selectedCell instanceof InputCell) {
            inputContraint.setInputCell((InputCell) selectedCell);
            inputContraint.addToParent();
            inputConstraintEdit.addToParent();
            inputConstraintEdit.getPanel().setVisible(false);
        }


        if (selectedCell instanceof ConstrainEdgeCell) {
            setRelationCell();
        }

        panel.revalidate();
        panel.repaint();

        returnPanel.revalidate();
        returnPanel.repaint();
    }

    public static void addChangeListener(JCheckBox field, ChangeListener changeListener) {
        field.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                changeListener.stateChanged(new ChangeEvent(field.isSelected()));
            }
        });
    }

    public static void addChangeListener(JComboBox field, ChangeListener changeListener) {
        field.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                changeListener.stateChanged(new ChangeEvent(field.getSelectedItem()));
            }
        });
    }

    public static void addChangeListener(JRadioButtonMenuItem field, ButtonGroup selection, ChangeListener changeListener) {
        field.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                changeListener.stateChanged(new ChangeEvent(selection.getSelection().getActionCommand()));
            }
        });
    }



    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {
            private int lastChange = 0, lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if (lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document) e.getOldValue();
            Document d2 = (Document) e.getNewValue();
            if (d1 != null) d1.removeDocumentListener(dl);
            if (d2 != null) d2.addDocumentListener(dl);
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if (d != null) d.addDocumentListener(dl);
    }

}
