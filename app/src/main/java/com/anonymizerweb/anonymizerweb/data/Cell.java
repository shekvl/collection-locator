package com.anonymizerweb.anonymizerweb.data;

public class Cell implements Comparable<Cell> {
    private Integer position;
    private String value;

    public Cell() {
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(Cell o) {
        return this.getPosition().compareTo(o.getPosition());
    }
}
