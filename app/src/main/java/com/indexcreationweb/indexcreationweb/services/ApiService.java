package com.indexcreationweb.indexcreationweb.services;

import com.indexcreationweb.indexcreationweb.dto.ApiDefinitionListDto;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDownloadDto;
import com.indexcreationweb.indexcreationweb.entities.indexcreator.Definition;
import com.indexcreationweb.indexcreationweb.repositories.indexcreator.DefinitionRepository;
import com.indexcreationweb.indexcreationweb.xml.IndexgenSchemaOutputResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class ApiService {
    Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    DefinitionRepository definitionRepository;

    @Autowired
    DefinitionService definitionService;

    public ApiDefinitionListDto getApiDataFromId(String id) {
        ApiDefinitionListDto apiDefinitionListDto = new ApiDefinitionListDto();
        List<DefinitionDownloadDto> downloadDtoList = new LinkedList<>();
        if (id.equals("all")) {
            for (Definition definition : definitionRepository.findAll()) {
                downloadDtoList.add(definitionService.getDefinitionDownloadDto(String.valueOf(definition.getId())));
            }
        } else {
            downloadDtoList.add(definitionService.getDefinitionDownloadDto(id));
        }

        apiDefinitionListDto.setDefinitions(downloadDtoList);
        return apiDefinitionListDto;
    }

    public String getAnoXmlSchema() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ApiDefinitionListDto.class);
        IndexgenSchemaOutputResolver sor = new IndexgenSchemaOutputResolver();
        jaxbContext.generateSchema(sor);

        return sor.getSchema();
    }
}
