package com.anonymizerweb.anonymizerweb.data;

import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;

public class Column {
    private Integer position;
    private String name;
    private Double min;
    private Double max;
    private ColumnTyp typ;
    private ColumnDataTyp dataTyp;
    private AnonymizationHierarchyNode hierarchyRootNode;

    public Column() {
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public ColumnTyp getTyp() {
        return typ;
    }

    public void setTyp(ColumnTyp typ) {
        this.typ = typ;
    }

    public ColumnDataTyp getDataTyp() {
        return dataTyp;
    }

    public void setDataTyp(ColumnDataTyp dataTyp) {
        this.dataTyp = dataTyp;
    }

    public AnonymizationHierarchyNode getHierarchyRootNode() {
        return hierarchyRootNode;
    }

    public void setHierarchyRootNode(AnonymizationHierarchyNode hierarchyRootNode) {
        this.hierarchyRootNode = hierarchyRootNode;
    }
}
