package com.anonymizerweb.anonymizerweb.data;

import java.util.List;

public class AnonymizationHierarchyNode implements Comparable<AnonymizationHierarchyNode>{

    private String value;

    private String sort;

    private List<AnonymizationHierarchyNode> children;

    private AnonymizationHierarchyNode parent;

    private Integer descendants;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<AnonymizationHierarchyNode> getChildren() {
        return children;
    }

    public void setChildren(List<AnonymizationHierarchyNode> children) {
        this.children = children;
    }

    public AnonymizationHierarchyNode getParent() {
        return parent;
    }

    public void setParent(AnonymizationHierarchyNode parent) {
        this.parent = parent;
    }

    public Integer getDescendants() {
        return descendants;
    }

    public void setDescendants(Integer descendants) {
        this.descendants = descendants;
    }

    @Override
    public int compareTo(AnonymizationHierarchyNode obj) {
        try {
            Double sortThis = Double.parseDouble(this.sort);
            Double sortObj = Double.parseDouble(obj.sort);
            return sortThis.compareTo(sortObj);
        } catch (NumberFormatException nfe) {
            return this.sort.compareTo(obj.sort);
        }
    }
}
