package nl.rug.ds.bpm.verification.models.cpn;

public class Place extends CPNElement {
    public Place() {
    }

    public Place(String id) {
        this(id, "");
    }

    public Place(String id, String name) {
        this.id = id;
        this.name = name;
        this.width = 26;
        this.height = 26;
    }

    public void setId(int id) {
        this.id = "p" + id;
    }
}
