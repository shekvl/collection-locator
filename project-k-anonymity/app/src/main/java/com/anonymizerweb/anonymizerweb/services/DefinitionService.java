package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.NewDefinitionCommand;
import com.anonymizerweb.anonymizerweb.dto.*;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.Definition;
import com.anonymizerweb.anonymizerweb.entities.DefinitionColumn;
import com.anonymizerweb.anonymizerweb.entities.Options;
import com.anonymizerweb.anonymizerweb.repositories.DefinitionRepository;
import com.anonymizerweb.anonymizerweb.repositories.OptionsRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

@Service
public class DefinitionService {
    Logger logger = LoggerFactory.getLogger(DefinitionService.class);

    @Autowired
    DefinitionRepository definitionRepository;

    @Autowired
    OptionsRepository optionsRepository;

    @Autowired
    AnonymizationService anonymizationService;

    public Definition findbyId(Long id) {
        Optional<Definition> byId = definitionRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<Definition> findAll() {
        List<Definition> definitions = new LinkedList<>();
        for (Definition definition : definitionRepository.findAll()) {
            definitions.add(definition);
        }

        return definitions;
    }

    public Integer findNumberOfDefinitions() {
        return ((java.util.Collection<?>) definitionRepository.findAll()).size();
    }

    public Definition save(NewDefinitionCommand command) throws JAXBException, IOException {

        String line;
        String content = "";
        InputStream inputStream = command.getFile().getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((line = bufferedReader.readLine()) != null) {
            content = content + line;
        }
        Definition definition = null;

        String originalFilename = command.getFile().getOriginalFilename();
        definition = isJsonDefinition(originalFilename, content);
        if (definition == null) {
            JAXBContext context = JAXBContext.newInstance(UploadDefinitionDto.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader stringReader = new StringReader(content);
            UploadDefinitionDto definitionDto = (UploadDefinitionDto) unmarshaller.unmarshal(stringReader);
            definition = uploadDtoToDefinition(originalFilename, definitionDto);
        }

        return definitionRepository.save(definition);

    }

    private Definition isJsonDefinition(String filename, String content) {
        Definition definition = null;
        try {
            Gson gson = new Gson();
            definition = gson.fromJson(content, Definition.class);
            definition.setFileName(filename);

        } catch (Exception ex) {

        }
        return definition;
    }

    public Definition uploadDtoToDefinition(String originalFilename, UploadDefinitionDto dto) {
        Definition definition = new Definition();
        definition.setuId(dto.getuId());
        definition.setName(dto.getName());
        definition.setTargetK(dto.getTargetK());
        definition.setFast(dto.getFast());
        definition.setBatch(dto.getBatch());
        definition.setFileName(originalFilename);
        Set<DefinitionColumn> columns = new HashSet<>();
        for (UploadDefinitionColumnDto dtoColumn : dto.getColumns()) {
            DefinitionColumn column = new DefinitionColumn();
            column.setPosition(dtoColumn.getPosition());
            column.setName(dtoColumn.getName());
            column.setCode(dtoColumn.getCode());
            columns.add(column);
        }

        definition.setColumns(columns);

        return definition;
    }

    public void delete(Long id) {
        definitionRepository.deleteById(id);
    }
}
