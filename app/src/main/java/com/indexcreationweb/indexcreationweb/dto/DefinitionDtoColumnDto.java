package com.indexcreationweb.indexcreationweb.dto;

public class DefinitionDtoColumnDto implements Comparable<DefinitionDtoColumnDto> {
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

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    @Override
    public int compareTo(DefinitionDtoColumnDto o) {
        return position.compareTo(o.position);
    }
}
