package com.anonymizerweb.anonymizerweb.commands;

import org.springframework.web.multipart.MultipartFile;

public class AnonymizationColumnsCommandPropertiesCommand implements Comparable<AnonymizationColumnsCommandPropertiesCommand>{

    private Long id;

    private Integer position;

    private String name;

    private String typ;

    private String dataTyp;

    private MultipartFile hierarchyFile;

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

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getDataTyp() {
        return dataTyp;
    }

    public void setDataTyp(String dataTyp) {
        this.dataTyp = dataTyp;
    }

    public MultipartFile getHierarchyFile() {
        return hierarchyFile;
    }

    public void setHierarchyFile(MultipartFile hierarchyFile) {
        this.hierarchyFile = hierarchyFile;
    }

    @Override
    public int compareTo(AnonymizationColumnsCommandPropertiesCommand o) {
        return this.position.compareTo(o.position);
    }
}
