package com.indexcreationweb.indexcreationweb.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "definitions")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"definitions"})
public class ApiDefinitionListDto {
    @XmlElement(name = "definition", type = DefinitionDownloadDto.class)
    private List<DefinitionDownloadDto> definitions;

    public List<DefinitionDownloadDto> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<DefinitionDownloadDto> definitions) {
        this.definitions = definitions;
    }
}
