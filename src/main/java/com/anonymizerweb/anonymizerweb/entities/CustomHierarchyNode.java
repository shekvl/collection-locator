package com.anonymizerweb.anonymizerweb.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class CustomHierarchyNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    private String sort;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL},  orphanRemoval=true)
    private List<CustomHierarchyNode> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<CustomHierarchyNode> getChildren() {
        return children;
    }

    public void setChildren(List<CustomHierarchyNode> children) {
        this.children = children;
    }
}
