package nl.rug.ds.bpm.editor.transformer;

import java.util.UUID;

/**
 * Created by Mark on 8/1/2015.
 */
public class CPNTransformerNode implements java.io.Serializable {
    protected String id;
    public float x;
    public float y;
    public UUID uniqueId;
    public boolean token;
    public String name;

    public CPNTransformerNode(String id, float x, float y, Boolean token, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        uniqueId = UUID.randomUUID();
        this.token = token;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }
}
