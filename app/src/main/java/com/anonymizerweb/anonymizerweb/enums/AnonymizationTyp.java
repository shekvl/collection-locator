package com.anonymizerweb.anonymizerweb.enums;

import java.util.LinkedList;
import java.util.List;

public enum AnonymizationTyp {
    FROM_DEFINITION("From definition", "from_def"),
    FROM_COLLECTION("From collection", "from_col");

    String description;
    String code;

    AnonymizationTyp(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static List<String> getAllCodes(){
        List<String> result = new LinkedList<>();
        for (AnonymizationTyp value : AnonymizationTyp.values()) {
            result.add(value.getCode());
        }
        return result;
    }
    public static AnonymizationTyp getByCode(String code){
        AnonymizationTyp result = null;
        for (AnonymizationTyp value : AnonymizationTyp.values()) {
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