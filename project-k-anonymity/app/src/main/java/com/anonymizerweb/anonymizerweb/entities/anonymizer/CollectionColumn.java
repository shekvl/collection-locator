package com.anonymizerweb.anonymizerweb.entities.anonymizer;

import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;

import javax.persistence.*;

@Entity
public class CollectionColumn implements Comparable<CollectionColumn>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer position;

    private String name;

    private String code;

    @Enumerated(EnumType.STRING)
    private ColumnTyp typ;

    @Enumerated(EnumType.STRING)
    private ColumnDataTyp dataTyp;

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    private CollectionHierarchyNode hierarchyRoot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public CollectionHierarchyNode getHierarchyRoot() {
        return hierarchyRoot;
    }

    public void setHierarchyRoot(CollectionHierarchyNode hierarchyRoot) {
        this.hierarchyRoot = hierarchyRoot;
    }

    @Override
    public int compareTo(CollectionColumn o) {
        return position.compareTo(o.position);
    }
}
