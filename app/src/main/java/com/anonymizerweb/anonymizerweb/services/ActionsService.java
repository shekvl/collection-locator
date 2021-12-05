package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.CombineCommand;
import com.anonymizerweb.anonymizerweb.commands.MatchCommand;
import com.anonymizerweb.anonymizerweb.dto.*;
import com.anonymizerweb.anonymizerweb.entities.*;
import com.anonymizerweb.anonymizerweb.repositories.DefinitionRepository;
import com.anonymizerweb.anonymizerweb.repositories.OptionsRepository;
import com.google.gson.Gson;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ActionsService {
    Logger logger = LoggerFactory.getLogger(ActionsService.class);

    @Autowired
    DefinitionService definitionService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    AnonymizeService anonymizeService;

    @Autowired
    CombineService combineService;

    @Autowired
    OptionsRepository optionsRepository;

    @Autowired
    DefinitionRepository definitionRepository;

    public void importDefinitions() {
        Optional<Options> indexGen = optionsRepository.findById("indexGen");
        if (indexGen.isPresent()) {
            RestTemplateBuilder builder = new RestTemplateBuilder();
            RestTemplate restTemplate = builder.build();
            String url = indexGen.get().getOptValue();
            UploadDefinitionListDto dto = restTemplate.getForObject(url, UploadDefinitionListDto.class);
            List<Long> ids = new LinkedList<>();
            for (Definition definition : definitionRepository.findAll()) {
                Boolean delete = false;
                for (UploadDefinitionDto definitionDto : dto.getDefinitions()) {
                    if (definition.getuId() != null && definition.getuId().equals(definitionDto.getuId())) {
                        delete = true;
                        break;
                    }
                }
                if (delete) {
                    ids.add(definition.getId());
                }
            }

            for (Long id : ids) {
                definitionRepository.deleteById(id);
            }

            for (UploadDefinitionDto definitionDto : dto.getDefinitions()) {
                Definition definition = definitionService.uploadDtoToDefinition("From Import", definitionDto);
                definitionRepository.save(definition);
            }
        }
    }

    public void createAllAnonymizations() {
        List<CombineCommand> combineCommands = new LinkedList<>();
        List<Collection> collections = collectionService.findAll();
        List<Definition> definitions = definitionService.findAll();
        List<Anonymization> anonymizations = anonymizationService.findAll();

        for (Anonymization anonymization : anonymizations) {
            anonymizationService.delete(anonymization.getId());
        }

        for (Definition definition : definitions) {
            for (Collection collection : collections) {
                CombineCommand combineCommand = new CombineCommand();
                combineCommand.setCollectionId(collection.getId());
                combineCommand.setDefinitionId(definition.getId());
                combineCommands.add(combineCommand);
            }
        }
        for (CombineCommand combineCommand : combineCommands) {
            MatchCommand matchCommand = combineService.getMatchCommandFromCombine(combineCommand);
            if (matchCommand.getMatchable()) {
                anonymizationService.saveFromMatch(matchCommand);
            }
        }
    }

    @Async
    public Future<String> anonymizeAll() throws InterruptedException, ExecutionException {
        List<Anonymization> anonymizations = anonymizationService.findAll();
        List<Future<String>> futureList = new LinkedList<>();

        for (Anonymization anonymization : anonymizations) {
            if (anonymization.getAnonymized() == null || !anonymization.getAnonymized()) {
                if (anonymization.getRunning() == null || !anonymization.getRunning()) {
                    Future<String> anonymize = anonymizeService.anonymize(anonymization.getId());
                    futureList.add(anonymize);
                }
            }
        }

        for (Future<String> stringFuture : futureList) {
            stringFuture.get();
        }

        return new AsyncResult<>("done");
    }

    public ApiAnonymizationDtoList sendAllAnonymizations() throws JAXBException, IOException {
        logger.error("DONE WITH ALL ANONYMIZATIONS ----- SEND HTTP");

        List<Anonymization> anonymizations = anonymizationService.findAll();
        ApiAnonymizationDtoList dtoList = getApiAnonymizationListDtoFromIds(anonymizations);
        JAXBContext context = JAXBContext.newInstance(ApiAnonymizationDtoList.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(dtoList, sw);
        String xmlString = sw.toString();

        Optional<Options> indexCollOptional = optionsRepository.findById("indexColl");
        if (indexCollOptional.isPresent()) {
            String indexCollUrl = indexCollOptional.get().getOptValue();
            URL url = new URL(indexCollUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/xml");
            http.setRequestProperty("Accept", "application/xml");

            byte[] out = xmlString.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            http.disconnect();
        }
        return dtoList;
    }

    private ApiAnonymizationDtoList getApiAnonymizationListDtoFromIds(List<Anonymization> anonymizations) {
        ApiAnonymizationDtoList dtoList = new ApiAnonymizationDtoList();
        dtoList.setAnonymizations(new LinkedList<>());
        for (Anonymization anonymization : anonymizations) {
            ApiAnonymizationDto anonymizationDto = new ApiAnonymizationDto();
            anonymizationDto.setName(anonymization.getName());
            anonymizationDto.setTargetK(anonymization.getTargetK());
            anonymizationDto.setDefinitionUid(anonymization.getDefinitionUid());
            List<ApiAnonymizationDtoColumn> columnDtoList = new LinkedList<>();
            for (AnonymizationColumn column : anonymization.getColumns()) {
                ApiAnonymizationDtoColumn columnDto = new ApiAnonymizationDtoColumn();
                columnDto.setPosition(column.getPosition());
                columnDto.setName(column.getName());
                columnDto.setCode(column.getCode());

                Hibernate.initialize(column.getHierarchyRoot());
                Gson gson = new Gson();
                String jsonString = gson.toJson(Hibernate.unproxy(column.getHierarchyRoot()));
                ApiAnonymizationDtoHierarchyNodeDto apiAnonymizationDtoHierarchyNodeDto = gson.fromJson(jsonString, ApiAnonymizationDtoHierarchyNodeDto.class);
                columnDto.setHierarchy(apiAnonymizationDtoHierarchyNodeDto);

                columnDtoList.add(columnDto);
            }
            Collections.sort(columnDtoList);
            anonymizationDto.setColumns(columnDtoList);
            anonymizationDto.setData(new LinkedList<>());
            for (String outputDatum : anonymization.getOutputData()) {
                anonymizationDto.getData().add(outputDatum.replace("\n", ""));
            }
            dtoList.getAnonymizations().add(anonymizationDto);
        }

        return dtoList;
    }
}
