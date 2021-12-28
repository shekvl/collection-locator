package com.indexcreationweb.indexcreationweb.dto;

import java.util.List;

public class DefinitionDto {
    private Long id;

    private String name;

    private Integer targetK;

    private Boolean isFast;

    private Integer batch;

    private List<DefinitionDtoColumnDto> columns;

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
        return isFast;
    }

    public void setFast(Boolean fast) {
        isFast = fast;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public List<DefinitionDtoColumnDto> getColumns() {
        return columns;
    }

    public void setColumns(List<DefinitionDtoColumnDto> columns) {
        this.columns = columns;
    }
}
