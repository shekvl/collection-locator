package com.anonymizerweb.anonymizerweb.commands;

import java.util.List;

public class MatchCommand {
    private Long definitionId;

    private Long collectionId;

    private List<MatchColumnCommand> columns;

    public Long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public List<MatchColumnCommand> getColumns() {
        return columns;
    }

    public void setColumns(List<MatchColumnCommand> columns) {
        this.columns = columns;
    }
}
