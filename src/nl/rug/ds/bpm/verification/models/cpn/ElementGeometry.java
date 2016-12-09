package nl.rug.ds.bpm.verification.models.cpn;

import java.util.UUID;

/**
 * Created by Mark Kloosterhuis.
 */
public class ElementGeometry {

    protected Integer x;
    protected Integer y;
    protected int width;
    protected int height;

    protected UUID uUId;
    protected UUID sourceElementId;

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = (int) x;
    }

    public void setY(float y) {
        this.y = (int) y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public UUID getSourceElementId() {
        return sourceElementId;
    }

    public void setSourceElementId(UUID sourceElementId) {
        this.sourceElementId = sourceElementId;
    }

    public UUID getuUId() {
        return uUId;
    }

    public void setuUId(UUID uUId) {
        this.uUId = uUId;
    }
}
