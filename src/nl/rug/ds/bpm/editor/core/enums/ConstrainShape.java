package nl.rug.ds.bpm.editor.core.enums;

/**
 * Created by Mark Kloosterhuis.
 */
public enum ConstrainShape {
    RECTANGLE("rectangle"),
    RECTANGLEFILLED("rectangleFilled"),
    ARROWEAST("arrowEast"),
    ARROWEASTTFILLED("arrowEastFilled"),
    ARROWWEST("arrowWest"),
    ARROWWESTFILLED("arrowWestFilled"),
    ECLIPSE("eclipse"),
    ECLIPSEFILLED("eclipseFilled"),
    EXCLUSIVE("exclusive"),
    PARALLEL("parallel");

    private String xmlName;

    private ConstrainShape(String xmlName){
        this.xmlName = xmlName;
    }

    public static ConstrainShape get(String xmlName) {
        for(ConstrainShape s : values()) {
            if(s.xmlName.equals(xmlName)) return s;
        }
        return null;
    }

}