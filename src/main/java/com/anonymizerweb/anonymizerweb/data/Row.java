package com.anonymizerweb.anonymizerweb.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Row {
    private List<Cell> cells;

    public Row() {
        cells = new LinkedList<>();
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public void addCell(Cell cell){
        cells.add(cell);
        Collections.sort(cells);
    }
}
