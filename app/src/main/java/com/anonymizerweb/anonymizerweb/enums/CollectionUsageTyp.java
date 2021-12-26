package com.anonymizerweb.anonymizerweb.enums;

import java.util.LinkedList;
import java.util.List;

public enum CollectionUsageTyp {
    FOR_INDEXES("For indexes", "for_index"),
    COMPLETE_COLLECTION("Complete collection", "comp_col"),
    SUPPORT_BOTH("Support both", "both");

    String description;
    String code;

    CollectionUsageTyp(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static List<String> getAllCodes(){
        List<String> result = new LinkedList<>();
        for (CollectionUsageTyp value : CollectionUsageTyp.values()) {
            result.add(value.getCode());
        }
        return result;
    }
    public static CollectionUsageTyp getByCode(String code){
        CollectionUsageTyp result = null;
        for (CollectionUsageTyp value : CollectionUsageTyp.values()) {
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