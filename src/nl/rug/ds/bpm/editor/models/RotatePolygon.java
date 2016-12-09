package nl.rug.ds.bpm.editor.models;

import java.awt.*;

/**
 * Created by Mark Kloosterhuis.
 */
public class RotatePolygon extends Polygon {
    public enum Direction {
        RL, LR, TB, BT
    }
    Polygon poly;
    int startX, startY, size;
    Direction direction;

    public RotatePolygon(int startX, int startY, int size, Direction direction) {
        this.direction = direction;
        this.startX = startX;
        this.startY = startY;
        if (direction == Direction.TB)
            this.startX+= size/2;
        else if (direction == Direction.BT) {
            this.startX += size / 2;
            this.startY += size ;
        }
        this.size = size;
    }

    public void addPoint(int addX, int addY) {
        if (direction == Direction.TB)
            super.addPoint(startX - addY, startY+addX);
        else if (direction == Direction.BT)
            super.addPoint(startX - addY, startY-addX);
        else if (direction == Direction.LR)
            super.addPoint(startX + addX, startY+addY);
        else
            super.addPoint(startX - addX, startY+addY);
    }
}

