package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "targetK", "biobankUid", "anonymizationTyp", "definitionUid","collectionUid", "columns", "data"})
public class ApiAnonymizationDto {
    private String name;

    private Integer targetK;

    private String biobankUid;

    private String anonymizationTyp;

    private String definitionUid;

    private String collectionUid;

    @XmlElement(name = "column", type = ApiAnonymizationDtoColumn.class)
    @XmlElementWrapper(name = "columns")
    private List<ApiAnonymizationDtoColumn> columns;

    @XmlElement(name = "dat", type = String.class)
    @XmlElementWrapper(name = "data")
    private List<ApiAnonymizationDtoDataDto> data;

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

    public String getAnonymizationTyp() {
        return anonymizationTyp;
    }

    public void setAnonymizationTyp(String anonymizationTyp) {
        this.anonymizationTyp = anonymizationTyp;
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

    public List<ApiAnonymizationDtoDataDto> getData() {
        return data;
    }

    public void setData(List<ApiAnonymizationDtoDataDto> data) {
        this.data = data;
    }

    public String getBiobankUid() {
        return biobankUid;
    }

    public void setBiobankUid(String biobankUid) {
        this.biobankUid = biobankUid;
    }

    public String getCollectionUid() {
        return collectionUid;
    }

    public void setCollectionUid(String collectionUid) {
        this.collectionUid = collectionUid;
    }
}
