package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "definition")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"uId", "name", "targetK", "fast", "batch", "columns"})
public class UploadDefinitionDto {
    private String uId;

    private String name;

    private Integer targetK;

    private Boolean fast;

    private Integer batch;

    @XmlElement(name = "column", type = UploadDefinitionColumnDto.class)
    @XmlElementWrapper(name = "columns")
    private List<UploadDefinitionColumnDto> columns;

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

    public List<UploadDefinitionColumnDto> getColumns() {
        return columns;
    }

    public void setColumns(List<UploadDefinitionColumnDto> columns) {
        this.columns = columns;
    }
}
