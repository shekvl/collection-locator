package com.indexcreationweb.indexcreationweb.services;

import com.indexcreationweb.indexcreationweb.dto.ApiDefinitionListDto;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDownloadDto;
import com.indexcreationweb.indexcreationweb.entities.Definition;
import com.indexcreationweb.indexcreationweb.repositories.DefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
