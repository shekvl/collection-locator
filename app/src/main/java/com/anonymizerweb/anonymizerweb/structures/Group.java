package com.anonymizerweb.anonymizerweb.structures;

import com.anonymizerweb.anonymizerweb.data.Row;

import java.util.LinkedList;
import java.util.List;

public class Group {
    String key;
    List<Row> rows;

    public Group() {
        rows = new LinkedList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public void addRow(Row row){
        rows.add(row);
    }
}
