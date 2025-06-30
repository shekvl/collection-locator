package com.anonymizerweb.anonymizerweb.entities.anonymizer;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Definition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uId;

    private String name;

    private String fileName;

    @Column(name = "targetk")
    private Integer targetK;

    private Boolean fast;

    private Integer batch;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "definitionId")
    @OrderBy("position ASC")
    private Set<DefinitionColumn> columns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
