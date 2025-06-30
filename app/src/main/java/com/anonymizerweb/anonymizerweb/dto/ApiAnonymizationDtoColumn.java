package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"position", "name", "code", "hierarchy"})
public class ApiAnonymizationDtoColumn implements Comparable<ApiAnonymizationDtoColumn>{
    private Integer position;

    private String name;

    private String code;

    @XmlElement(name = "hierarchyRoot", type = ApiAnonymizationDtoHierarchyNodeDto.class)
    private ApiAnonymizationDtoHierarchyNodeDto hierarchy;

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

    public ApiAnonymizationDtoHierarchyNodeDto getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(ApiAnonymizationDtoHierarchyNodeDto hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public int compareTo(ApiAnonymizationDtoColumn o) {
        return position.compareTo(o.getPosition());
    }
}
