package com.anonymizerweb.anonymizerweb.commands;

import com.anonymizerweb.anonymizerweb.entities.CollectionColumn;

import java.util.List;

public class EditCollectionCommand {
    String name;

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
}
