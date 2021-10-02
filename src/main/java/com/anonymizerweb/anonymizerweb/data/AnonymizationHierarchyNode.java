package com.anonymizerweb.anonymizerweb.data;

import java.util.List;

public class AnonymizationHierarchyNode {

    private String value;

    private List<AnonymizationHierarchyNode> children;

    private AnonymizationHierarchyNode parent;

    private Integer descendants;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
