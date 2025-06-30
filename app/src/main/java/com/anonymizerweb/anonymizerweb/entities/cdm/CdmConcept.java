package com.anonymizerweb.anonymizerweb.entities.cdm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "concept")
public class CdmConcept {

    @Id
    @Column(name = "concept_id")
    private String id;
    @Column(name = "concept_name")
    private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
