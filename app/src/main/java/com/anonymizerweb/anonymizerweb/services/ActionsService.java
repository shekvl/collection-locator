package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.CombineCommand;
import com.anonymizerweb.anonymizerweb.commands.MatchCommand;
import com.anonymizerweb.anonymizerweb.dto.*;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.*;
import com.anonymizerweb.anonymizerweb.enums.CollectionUsageTyp;
import com.anonymizerweb.anonymizerweb.repositories.DefinitionRepository;
import com.anonymizerweb.anonymizerweb.repositories.OptionsRepository;
import com.anonymizerweb.anonymizerweb.xml.AnoSchemaOutputResolver;
import com.google.gson.Gson;
import org.hibernate.Hibernate;
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
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
                if (collection.getUsageTyp().getCode().equals(CollectionUsageTyp.FOR_INDEXES.getCode()) ||
                        collection.getUsageTyp().getCode().equals(CollectionUsageTyp.SUPPORT_BOTH.getCode())) {
                    CombineCommand combineCommand = new CombineCommand();
                    combineCommand.setCollectionId(collection.getId());
                    combineCommand.setDefinitionId(definition.getId());
                    combineCommands.add(combineCommand);
                }
            }
        }
        for (CombineCommand combineCommand : combineCommands) {
            MatchCommand matchCommand = combineService.getMatchCommandFromCombine(combineCommand);
            if (matchCommand.getMatchable()) {
                anonymizationService.saveFromMatch(matchCommand);
            }
        }

        for (Collection collection : collections) {
            if (collection.getUsageTyp().getCode().equals(CollectionUsageTyp.COMPLETE_COLLECTION.getCode()) ||
                    collection.getUsageTyp().getCode().equals(CollectionUsageTyp.SUPPORT_BOTH.getCode())) {
                anonymizationService.saveFromCollection(collection);
            }
        }
    }

    @Async
    public Future<List<Anonymization>> anonymizeAll() throws InterruptedException, ExecutionException {
        List<Anonymization> anonymizations = anonymizationService.findAll();
        List<Future<Anonymization>> futureList = new LinkedList<>();

        for (Anonymization anonymization : anonymizations) {
            if (anonymization.getAnonymized() == null || !anonymization.getAnonymized()) {
                if (anonymization.getRunning() == null || !anonymization.getRunning()) {
                    Future<Anonymization> anonymize = anonymizeService.anonymize(anonymization.getId());
                    futureList.add(anonymize);
                }
            }
        }

        List<Anonymization> anonymizationList = new LinkedList<>();
        for (Future<Anonymization> future : futureList) {
            anonymizationList.add(future.get());
        }

        return new AsyncResult<>(anonymizationList);
    }

    public ApiAnonymizationDtoList sendAllAnonymizationsXml(List<Anonymization> anonymizations) throws JAXBException, IOException {
            logger.info("DONE WITH ALL ANONYMIZATIONS ----- SEND HTTP");
            if(anonymizations == null || anonymizations.size() == 0){
                anonymizations = anonymizationService.findAll();
            }
            List<Anonymization> anonymizationsTmp = new LinkedList<>();
            for (Anonymization anonymization : anonymizations) {
                if (anonymization.getAnonymized() != null && anonymization.getAnonymized()) {
                    if (anonymization.getRunning() != null && !anonymization.getRunning()) {
                        anonymizationsTmp.add(anonymization);
                    }
                }
            }
            anonymizations = anonymizationsTmp;

            ApiAnonymizationDtoList dtoList = getApiAnonymizationListDtoFromIds(anonymizations);
            JAXBContext context = JAXBContext.newInstance(ApiAnonymizationDtoList.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            mar.marshal(dtoList, sw);
            String xmlString = sw.toString();

            try {
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
            } catch (IOException e) {
                logger.error("Error while sending Anonymizations: " + e.toString());
            }
            return dtoList;
        }

    public ApiAnonymizationDtoList sendAllAnonymizationsJson(List<Anonymization> anonymizations){
        logger.info("DONE WITH ALL ANONYMIZATIONS ----- SEND HTTP");
        if(anonymizations == null || anonymizations.size() == 0){
            anonymizations = anonymizationService.findAll();
        }
        List<Anonymization> anonymizationsTmp = new LinkedList<>();
        for (Anonymization anonymization : anonymizations) {
            if (anonymization.getAnonymized() != null && anonymization.getAnonymized()) {
                if (anonymization.getRunning() != null && !anonymization.getRunning()) {
                    anonymizationsTmp.add(anonymization);
                }
            }
        }
        anonymizations = anonymizationsTmp;

        ApiAnonymizationDtoList dtoList = getApiAnonymizationListDtoFromIds(anonymizations);
        Gson gson = new Gson();
        String jsonString = gson.toJson(dtoList);

        try {
            Optional<Options> indexCollOptional = optionsRepository.findById("indexColl");
            if (indexCollOptional.isPresent()) {
                String indexCollUrl = indexCollOptional.get().getOptValue();
                URL url = new URL(indexCollUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                http.setRequestProperty("Accept", "application/json");

                byte[] out = jsonString.getBytes(StandardCharsets.UTF_8);

                OutputStream stream = http.getOutputStream();
                stream.write(out);

                http.disconnect();
            }
        } catch (IOException e) {
            logger.error("Error while sending Anonymizations: " + e.toString());
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
            Optional<Options> biobankId = optionsRepository.findById("biobankId");
            if (biobankId.isPresent()) {
                anonymizationDto.setBiobankUid(biobankId.get().getOptValue());
            }else {
                anonymizationDto.setBiobankUid(null);
            }
            anonymizationDto.setAnonymizationTyp(anonymization.getAnonymizationTyp().getCode());
            anonymizationDto.setCollectionUid(anonymization.getCollectionUid());
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
            Map<String, Integer> rowMap = new HashMap<>();
            for (String outputDatum : anonymization.getOutputData()) {
                if(rowMap.containsKey(outputDatum)){
                    Integer val = rowMap.get(outputDatum);
                    val = val + 1;
                    rowMap.put(outputDatum,val);
                }else {
                    rowMap.put(outputDatum, 1);
                }
            }
            List<ApiAnonymizationDtoDataDto> dtoDataDtoList = new LinkedList<>();
            for (Map.Entry<String, Integer> entry : rowMap.entrySet()) {
                ApiAnonymizationDtoDataDto dataDto = new ApiAnonymizationDtoDataDto();
                dataDto.setRow(entry.getKey());
                dataDto.setRowAmount(entry.getValue());
                dtoDataDtoList.add(dataDto);
            }
            anonymizationDto.setData(dtoDataDtoList);
            dtoList.getAnonymizations().add(anonymizationDto);
        }

        return dtoList;
    }

    public String getAnoXmlSchema() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ApiAnonymizationDtoList.class);
        AnoSchemaOutputResolver sor = new AnoSchemaOutputResolver();
        jaxbContext.generateSchema(sor);

        return sor.getSchema();
    }
}
