package com.anonymizerweb.anonymizerweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "definitions")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"definitions"})
public class UploadDefinitionListDto {
    @XmlElement(name = "definition", type = UploadDefinitionDto.class)
    private List<UploadDefinitionDto> definitions;

    public List<UploadDefinitionDto> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<UploadDefinitionDto> definitions) {
        this.definitions = definitions;
    }
}
