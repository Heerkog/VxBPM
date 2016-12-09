package nl.rug.ds.bpm.editor.core.enums;

import java.awt.*;

/**
 * Created by Mark Kloosterhuis.
 */
public enum ConstraintStatus {
    None("#000000","Did not test"),
    Unavailable("#C0C0C0","Could not test"),
    Valid("#00BA21","Passed"),
    Invalid("#FF0000","Failed");

    private String color;
    private String text;
    public String getColorCode(){
        return color;
    }
    public Color getColor(){
        return Color.decode(color);
    }
    public String getText(){
        return this.text;
    }


    private ConstraintStatus(String color,String text){
        this.color = color;
        this.text = text;
    }

}