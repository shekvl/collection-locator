package com.indexcreationweb.indexcreationweb.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "column")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"position", "name", "code", "codeText"})
public class DefinitionDownloadDtoColumnDto implements Comparable<DefinitionDownloadDtoColumnDto> {
    private Integer position;

    private String name;

    private String code;
    private String codeText;

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

    @Override
    public int compareTo(DefinitionDownloadDtoColumnDto o) {
        return position.compareTo(o.position);
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public String getCodeText() {
        return codeText;
    }
}
