package nl.rug.ds.bpm.editor.core.enums;

import nl.rug.ds.bpm.editor.models.ModelChecker;
import nl.rug.ds.bpm.editor.models.graphModels.InputCell;

import java.util.ArrayList;

/**
 * Created by Mark Kloosterhuis.
 */
public enum EventType {
    LOADED(null),
    VARIABLES_CHANGED(null),
    BPMN_REDRAW(null),
    BPMN_CHANGED(null),
    ADD_CONSOLE_LINE(String.class),
    CONSOLE_WORKFLOW(String.class),
    CONSOLE_KRIPKE(String.class),
    CONSOLE_RAWINPUT(String.class),
    CONSOLE_RAWOUTPUT(int.class),
    CONSOLE_CONSTRAINT(ArrayList.class),
    CONSOLE_VARIABLES(ArrayList.class),
    FILE_OPENED(ArrayList.class),
    SELECTION_CHANGED(InputCell.class),
    EDITOR_TABVIEW_CHANGED(int.class),
    CONSOLE_TABVIEW_CHANGED(int.class),
    CPN_CONVERTED(int.class),
    KRIPKE_CONSOLE_TABVIEW_CHANGED(int.class),
    MODEL_CHECKER_CHANGE(ModelChecker.class),
    //Kripke structure
    KRIPKE_STRUCTURE_NAME_CHANGE(String.class),
    KRIPKE_STRUCTURE_TAB_CHANGE(String.class),
    KRIPKE_STRUCTURE_VALUE_CHANGE(int.class),
    KRIPKE_STRUCTURE_CHANGE(int.class),
    KRIPKE_CONSTRAINT_CHANGE(int.class),
    CONSTRAINT_SELECT_CHANGE(String.class),
    CHECKMODEL_BUTTON_CLICK(String.class),
    CONSOLE_FULLOUTPUT_CHANGED(String.class),
    CHECKMODEL_BUTTON_ENABLED(boolean.class);


    public Class<?> MessageClass;

    private EventType(Class<?> clazz) {
        this.MessageClass = clazz;
    }
};