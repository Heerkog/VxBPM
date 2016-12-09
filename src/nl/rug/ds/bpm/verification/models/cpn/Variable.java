package nl.rug.ds.bpm.verification.models.cpn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Variable implements Serializable {
    private int id;
    private String name;
    private List<String> values;
    private List<String> defaultValues = new ArrayList<>();

    private String defaultCpnName;


    public Variable(int id, String name, List<String> values) {
        this.id = id;
        this.name = name;
        this.values = values;
    }

    public Variable(int id, String name) {
        this(id, name, new ArrayList<>());
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getTableName(){
        return name+(isDefault()?" ("+defaultCpnName+")":"");
    }

    public void addValue(String value) {
        if (!values.contains(value))
            values.add(value);
    }
    public void addDefaultValue(String value) {
        if (!defaultValues.contains(value))
            defaultValues.add(value);
        addValue(value);
    }
    public List<String> getValues() {
        return values;
    }

    public List<String> getDefaultValues() {
        return defaultValues;
    }

    public List<String> getValues(String condition, String filterValue) {
        if (filterValue == null || filterValue.isEmpty())
            return values;

        List<String> filteredValues = new ArrayList<String>();
        for (String value : values) {
            int intValue = 0, intFilterValue = 0;
            try {
                intValue = Integer.valueOf(value.trim());
                intFilterValue = Integer.valueOf(filterValue.trim());
            } catch (Exception e) {
            }

            if (filterValue != null && !filterValue.isEmpty()) {
                if (condition.equals("==")) {
                    String[] splitValues = filterValue.split(",");
                    for (String splitValue : splitValues) {
                        if (splitValue.trim().toLowerCase().equals(value.trim().toLowerCase())) {
                            filteredValues.add(value.trim());
                        }
                    }
                } else if (condition == "!=") {
                    String[] splitValues = filterValue.split(",");
                    filteredValues.add(value.trim());
                    for (String splitValue : splitValues) {
                        if (splitValue.trim().toLowerCase().equals(value.trim().toLowerCase())) {
                            filteredValues.remove(filteredValues.size() - 1);
                        }
                    }
                } else if (condition == ">" && intValue > intFilterValue)
                    filteredValues.add(value);
                else if (condition == ">=" && intValue >= intFilterValue)
                    filteredValues.add(value);
                else if (condition == "<" && intValue < intFilterValue)
                    filteredValues.add(value);
                else if (condition == "<=" && intValue <= intFilterValue)
                    filteredValues.add(value);


            } else
                filteredValues.add(value);
        }

        return filteredValues;
    }


    public String toString() {
        StringBuilder vars = new StringBuilder(name + ": {");
        Iterator<String> k = values.iterator();
        while (k.hasNext()) {
            vars.append(k.next());
            if (k.hasNext()) vars.append(", ");
        }
        vars.append("}");

        return vars.toString();
    }
    public boolean isDefault(){
        return this.defaultValues.size()>0;
    }
    public String getDefaultCpnName() {
        return defaultCpnName;
    }

    public void setDefaultCpnName(String defaultCpnName) {
        this.defaultCpnName = defaultCpnName;
    }
}
