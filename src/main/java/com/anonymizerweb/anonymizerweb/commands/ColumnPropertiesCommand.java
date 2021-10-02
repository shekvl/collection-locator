package com.anonymizerweb.anonymizerweb.commands;

import com.anonymizerweb.anonymizerweb.entities.ColumnProperty;

import java.util.List;

public class ColumnPropertiesCommand {
    private String anonymizationId;

    private List<ColumnPropertiesCommandProperties> propertyList;

    public String getAnonymizationId() {
        return anonymizationId;
    }

    public void setAnonymizationId(String anonymizationId) {
        this.anonymizationId = anonymizationId;
    }

    public List<ColumnPropertiesCommandProperties> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<ColumnPropertiesCommandProperties> propertyList) {
        this.propertyList = propertyList;
    }
}
