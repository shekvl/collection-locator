package com.anonymizerweb.anonymizerweb.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Definition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileName;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<DefinitionColumn> getColumns() {
        return columns;
    }

    public void setColumns(Set<DefinitionColumn> columnNames) {
        this.columns = columnNames;
    }
}
