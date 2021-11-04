package com.indexcreationweb.indexcreationweb.commands;

import java.util.List;

public class DefinitionCommand {
    private String name;

    private Integer targetk;

    private Boolean isFast;

    private Integer batch;

    private List<DefinitionCommandColumnCommand> columns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetk() {
        return targetk;
    }

    public void setTargetk(Integer targetk) {
        this.targetk = targetk;
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

    public List<DefinitionCommandColumnCommand> getColumns() {
        return columns;
    }

    public void setColumns(List<DefinitionCommandColumnCommand> columns) {
        this.columns = columns;
    }
}
