package com.anonymizerweb.anonymizerweb.enums;

import java.util.LinkedList;
import java.util.List;

public enum ColumnTyp {
    IDENTIFIER("Identifier", "id"),
    QUASIIDENTIFIER("Quasiidentifier", "qid"),
    SECRET("Secret", "sec");

    String description;
    String code;

    ColumnTyp(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static List<String> getAllCodes(){
        List<String> result = new LinkedList<>();
        for (ColumnTyp value : ColumnTyp.values()) {
            result.add(value.getCode());
        }
        return result;
    }
    public static ColumnTyp getByCode(String code){
        ColumnTyp result = null;
        for (ColumnTyp value : ColumnTyp.values()) {
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