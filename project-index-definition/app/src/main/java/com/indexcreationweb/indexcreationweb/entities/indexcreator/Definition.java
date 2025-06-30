package com.indexcreationweb.indexcreationweb.entities.indexcreator;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Definition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "targetk")
    private Integer targetK;

    private Boolean fast;

    private Integer batch;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "definitionId")
    private Set<DefinitionColumn> columns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetK() {
        return targetK;
    }

    public void setTargetK(Integer targetK) {
        this.targetK = targetK;
    }

    public Boolean getFast() {
        return fast;
    }

    public void setFast(Boolean fast) {
        this.fast = fast;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Set<DefinitionColumn> getColumns() {
        return columns;
    }

    public void setColumns(Set<DefinitionColumn> columnNames) {
        this.columns = columnNames;
    }
}
