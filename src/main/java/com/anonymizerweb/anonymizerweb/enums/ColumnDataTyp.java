package com.anonymizerweb.anonymizerweb.enums;

import java.util.LinkedList;
import java.util.List;

public enum ColumnDataTyp {
    NUMBER("Number", "n"),
    STRUCTUREDNUMBER("Structured number", "sn"),
    CUSTOMHIERARCHY("Custom hierarchy", "ch");

    String description;
    String code;

    ColumnDataTyp(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static List<String> getAllCodes(){
        List<String> result = new LinkedList<>();
        for (ColumnDataTyp value : ColumnDataTyp.values()) {
            result.add(value.getCode());
        }
        return result;
    }
    public static ColumnDataTyp getByCode(String code){
        ColumnDataTyp result = null;
        for (ColumnDataTyp value : ColumnDataTyp.values()) {
            if(value.getCode().equals(code)){
                result = value;
                break;
            }
        }
        return result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}