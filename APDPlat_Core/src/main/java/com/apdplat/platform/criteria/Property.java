package com.apdplat.platform.criteria;

/**
 * 属性描述
 * @author 杨尚川
 *
 */
public class Property {
    private static int source;
    private int seq;
    private String name;
    private Object value;

    public Property() {
        this(null, null);
    }

    public Property(String name, Object value) {
        this.name = name;
        this.value = value;
        seq=source++;
    }

    public String getNameParameter() {
        return name.replace(".", "_") + "_" + seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
