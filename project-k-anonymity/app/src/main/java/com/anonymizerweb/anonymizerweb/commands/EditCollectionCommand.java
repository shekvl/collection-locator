package com.anonymizerweb.anonymizerweb.commands;

import com.anonymizerweb.anonymizerweb.entities.CollectionColumn;
import com.anonymizerweb.anonymizerweb.enums.CollectionUsageTyp;

import java.util.List;

public class EditCollectionCommand {
    String name;

    private String usageTyp;

    private Integer targetK;

    List<EditCollectionCommandColumn> columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EditCollectionCommandColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<EditCollectionCommandColumn> columns) {
        this.columns = columns;
    }

    public String getUsageTyp() {
        return usageTyp;
    }

    public void setUsageTyp(String usageTyp) {
        this.usageTyp = usageTyp;
    }

    public Integer getTargetK() {
        return targetK;
    }

    public void setTargetK(Integer targetK) {
        this.targetK = targetK;
    }
}
