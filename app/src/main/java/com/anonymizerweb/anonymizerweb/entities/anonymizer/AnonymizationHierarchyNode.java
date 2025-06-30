package com.anonymizerweb.anonymizerweb.entities.anonymizer;

import javax.persistence.*;
import java.util.List;

@Entity
public class AnonymizationHierarchyNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    private String sort;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL},  orphanRemoval=true)
    @JoinColumn(name = "parentHierarchyNodeId")
    private List<AnonymizationHierarchyNode> children;

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

    public List<AnonymizationHierarchyNode> getChildren() {
        return children;
    }

    public void setChildren(List<AnonymizationHierarchyNode> children) {
        this.children = children;
    }
}
