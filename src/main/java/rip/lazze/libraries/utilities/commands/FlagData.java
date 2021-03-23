package rip.lazze.libraries.utilities.commands;

import java.beans.ConstructorProperties;
import java.util.List;

public class FlagData implements Data {
    private List<String> names;
    private String description;
    private boolean defaultValue;
    private int methodIndex;

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    @ConstructorProperties({"names", "description", "defaultValue", "methodIndex"})
    public FlagData(List<String> names, String description, boolean defaultValue, int methodIndex) {
        this.names = names;
        this.description = description;
        this.defaultValue = defaultValue;
        this.methodIndex = methodIndex;
    }

    public List<String> getNames() {
        return this.names;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMethodIndex() {
        return this.methodIndex;
    }
}
