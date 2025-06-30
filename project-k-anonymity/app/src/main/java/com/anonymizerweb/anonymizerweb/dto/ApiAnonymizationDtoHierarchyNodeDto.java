package com.anonymizerweb.anonymizerweb.dto;

import com.anonymizerweb.anonymizerweb.entities.AnonymizationHierarchyNode;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"value", "sort", "children"})
public class ApiAnonymizationDtoHierarchyNodeDto {

    private String value;

    private String sort;

    @XmlElement(name = "hierarchyNode", type = ApiAnonymizationDtoHierarchyNodeDto.class)
    @XmlElementWrapper(name = "children")
    private List<ApiAnonymizationDtoHierarchyNodeDto> children;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<ApiAnonymizationDtoHierarchyNodeDto> getChildren() {
        return children;
    }

    public void setChildren(List<ApiAnonymizationDtoHierarchyNodeDto> children) {
        this.children = children;
    }
}
