package com.anonymizerweb.anonymizerweb.commands;

import java.util.List;

public class AnonymizationColumnsCommand {
    private String anonymizationId;

    private List<AnonymizationColumnsCommandPropertiesCommand> columns;

    public String getAnonymizationId() {
        return anonymizationId;
    }

    public void setAnonymizationId(String anonymizationId) {
        this.anonymizationId = anonymizationId;
    }

    public List<AnonymizationColumnsCommandPropertiesCommand> getColumns() {
        return columns;
    }

    public void setColumns(List<AnonymizationColumnsCommandPropertiesCommand> columns) {
        this.columns = columns;
    }
}
