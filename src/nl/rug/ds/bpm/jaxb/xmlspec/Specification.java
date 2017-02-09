package nl.rug.ds.bpm.jaxb.xmlspec;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by p256867 on 7-2-2017.
 */

@XmlRootElement
public class Specification {
    private String language, type, value;

    public Specification() {}

    @XmlAttribute
    public void setLanguage(String language) { this.language = language; }
    public String getLanguage() { return language; }

    @XmlAttribute
    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    @XmlValue
    public void setValue(String value) { this.value = value; }
    public String getValue() { return value; }
}
