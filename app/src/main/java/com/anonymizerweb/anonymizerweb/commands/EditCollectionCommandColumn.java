package com.anonymizerweb.anonymizerweb.commands;

import org.springframework.web.multipart.MultipartFile;

public class EditCollectionCommandColumn implements Comparable<EditCollectionCommandColumn> {
    private Long id;

    private Integer position;

    private String name;

    private String code;

    private String codeText;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
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
    public int compareTo(EditCollectionCommandColumn o) {
        return position.compareTo(o.position);
    }
}
