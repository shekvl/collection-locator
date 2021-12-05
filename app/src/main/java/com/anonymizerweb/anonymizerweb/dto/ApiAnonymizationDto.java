package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "targetK", "definitionUid", "columns", "data"})
public class ApiAnonymizationDto {
    private String name;

    private Integer targetK;

    private String definitionUid;

    @XmlElement(name = "column", type = ApiAnonymizationDtoColumn.class)
    @XmlElementWrapper(name = "columns")
    private List<ApiAnonymizationDtoColumn> columns;

    @XmlElement(name = "dat", type = String.class)
    @XmlElementWrapper(name = "data")
    private List<String> data;

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

    public String getDefinitionUid() {
        return definitionUid;
    }

    public void setDefinitionUid(String definitionUid) {
        this.definitionUid = definitionUid;
    }

    public List<ApiAnonymizationDtoColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ApiAnonymizationDtoColumn> columns) {
        this.columns = columns;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
