package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;


@XmlRootElement(name = "anonymizations")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"anonymizations"})
@XmlSeeAlso(ApiAnonymizationDtoHierarchyNodeDto.class)
public class ApiAnonymizationDtoList {

    @XmlElement(name = "anonymization", type = ApiAnonymizationDto.class)
    private List<ApiAnonymizationDto> anonymizations;

    public List<ApiAnonymizationDto> getAnonymizations() {
        return anonymizations;
    }

    public void setAnonymizations(List<ApiAnonymizationDto> anonymizations) {
        this.anonymizations = anonymizations;
    }
}
