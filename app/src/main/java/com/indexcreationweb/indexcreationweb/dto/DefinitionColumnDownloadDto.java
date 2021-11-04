package com.indexcreationweb.indexcreationweb.dto;

public class DefinitionColumnDownloadDto implements Comparable<DefinitionColumnDownloadDto> {
    private Integer position;

    private String name;

    private String code;

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
    public int compareTo(DefinitionColumnDownloadDto o) {
        return position.compareTo(o.position);
    }
}
