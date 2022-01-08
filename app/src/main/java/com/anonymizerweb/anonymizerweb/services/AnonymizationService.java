package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.*;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDto;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoColumn;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoHierarchyNodeDto;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoList;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.*;
import com.anonymizerweb.anonymizerweb.enums.AnonymizationTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.repositories.AnonymizationRepository;
import com.google.gson.Gson;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.*;

@Service
public class AnonymizationService {
    Logger logger = LoggerFactory.getLogger(AnonymizationService.class);

    @Autowired
    AnonymizationRepository anonymizationRepository;

    @Autowired
    DefinitionService definitionService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    CombineService combineService;

    @Autowired
    AnonymizeService anonymizeService;

    public Anonymization findbyId(Long id) {
        Optional<Anonymization> byId = anonymizationRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<Anonymization> findAll() {
        List<Anonymization> anonymizations = new LinkedList<>();
        for (Anonymization anonymization : anonymizationRepository.findAll()) {
            anonymizations.add(anonymization);
        }

        return anonymizations;
    }

    public Integer findNumberOfAnonymizations() {
        return ((java.util.Collection<?>) anonymizationRepository.findAll()).size();
    }

    public Anonymization save(NewAnonymizationCommand command) {
        Collection collection = collectionService.findbyId(command.getCollectionId());
        return saveFromCollection(collection);
    }

    public Anonymization saveFromCollection(Collection collection) {
        Anonymization anonymization = new Anonymization();
        Gson gson = new Gson();

        anonymization.setName("Full collection ++++ " + collection.getName());
        anonymization.setFileName("Full collection ++++ " + collection.getName());
        anonymization.setHeading(collection.getHeading());
        anonymization.setCollectionUid(collection.getName() + "_" + collection.getId());
        anonymization.setDefinitionUid(null);
        anonymization.setAnonymizationTyp(AnonymizationTyp.FROM_COLLECTION);
        anonymization.setTargetK(collection.getTargetK());
        anonymization.setFast(true);
        anonymization.setBatch(3000);
        anonymization.setLoss(null);
        anonymization.setDuration(null);
        anonymization.setRunning(false);
        anonymization.setAnonymized(false);
        List<String> anonymizationRawData = new LinkedList<>(collection.getRawData());

        anonymization.setRawData(anonymizationRawData);
        Set<AnonymizationColumn> anonymizationColumns = new HashSet<>();
        for (CollectionColumn column : collection.getColumns()) {
            AnonymizationColumn anonymizationColumn = new AnonymizationColumn();
            anonymizationColumn.setPosition(column.getPosition());
            anonymizationColumn.setName(column.getName());
            anonymizationColumn.setCode(column.getCode());
            anonymizationColumn.setTyp(column.getTyp());
            anonymizationColumn.setDataTyp(column.getDataTyp());
            Hibernate.initialize(column.getHierarchyRoot());
            String jsonString = gson.toJson((CollectionHierarchyNode) Hibernate.unproxy(column.getHierarchyRoot()));
            AnonymizationHierarchyNode anonymizationHierarchyNode = gson.fromJson(jsonString, AnonymizationHierarchyNode.class);
            anonymizationColumn.setHierarchyRoot(anonymizationHierarchyNode);

            anonymizationColumns.add(anonymizationColumn);
        }

        anonymization.setColumns(anonymizationColumns);

        return anonymizationRepository.save(anonymization);
    }

    public Anonymization saveFromMatch(MatchCommand command) {
        Anonymization anonymization = new Anonymization();

        Definition definition = definitionService.findbyId(command.getDefinitionId());
        Collection collection = collectionService.findbyId(command.getCollectionId());
        LinkedList<DefinitionColumn> definitionColumns = new LinkedList<>(definition.getColumns());
        Integer cnt = 0;
        String heading = "";
        Gson gson = new Gson();
        Collections.sort(command.getColumns());
        List<AnonymizationColumn> columns = new LinkedList<>();
        for (MatchColumnCommand column : command.getColumns()) {
            AnonymizationColumn property = new AnonymizationColumn();
            property.setPosition(column.getDefinitionColumnPosition());
            property.setName(column.getDefinitionColumnName());
            property.setCode(column.getDefinitionColumnCode());
            for (CollectionColumn collectionColumn : collection.getColumns()) {
                if(collectionColumn.getPosition().equals(column.getCollectionColumnPosition())){
                    property.setTyp(collectionColumn.getTyp());
                    property.setDataTyp(collectionColumn.getDataTyp());
                    Hibernate.initialize(collectionColumn.getHierarchyRoot());
                    String jsonString = gson.toJson((CollectionHierarchyNode) Hibernate.unproxy(collectionColumn.getHierarchyRoot()));
                    AnonymizationHierarchyNode anonymizationHierarchyNode = gson.fromJson(jsonString, AnonymizationHierarchyNode.class);
                    property.setHierarchyRoot(anonymizationHierarchyNode);
                    break;
                }
            }
            columns.add(property);
            heading = heading + column.getDefinitionColumnName();
            if (!(cnt >= definitionColumns.size() - 1)) {
                heading = heading + ";";
            }
            cnt++;
        }
        anonymization.setHeading(heading);

        List<String> data = new LinkedList<>();
        for (String rawDatum : collection.getRawData()) {
            String[] splits = rawDatum.split(";");
            String filteredDatum = "";
            Collections.sort(command.getColumns());
            cnt = 0;
            for (MatchColumnCommand column : command.getColumns()) {
                filteredDatum = filteredDatum + splits[column.getCollectionColumnPosition() - 1];
                if (!(cnt >= command.getColumns().size() - 1)) {
                    filteredDatum = filteredDatum + ";";
                }
                cnt++;
            }

            data.add(filteredDatum);
        }

        anonymization.setRawData(data);
        anonymization.setTargetK(definition.getTargetK());
        anonymization.setName(definition.getName() + " ++++ " + collection.getName());
        anonymization.setFileName(definition.getFileName() + " ++++ " + collection.getFileName());
        anonymization.setFast((definition.getFast() != null ? definition.getFast() : false));
        anonymization.setBatch((definition.getBatch() != null && definition.getBatch() >= definition.getTargetK() ? definition.getBatch() : 5000));
        anonymization.setCollectionUid(null);
        anonymization.setDefinitionUid(definition.getuId());
        anonymization.setAnonymizationTyp(AnonymizationTyp.FROM_DEFINITION);
        Anonymization save = anonymizationRepository.save(anonymization);
        save.setColumns(new HashSet<>(columns));
        return anonymizationRepository.save(save);
    }

    public EditAnonymizationCommand getEditCommand(Long id) {
        EditAnonymizationCommand editAnonymizationCommand = new EditAnonymizationCommand();
        Optional<Anonymization> anonymizationOptional = anonymizationRepository.findById(id);
        if (anonymizationOptional.isPresent()) {
            Anonymization anonymization = anonymizationOptional.get();
            editAnonymizationCommand.setName(anonymization.getName());
            editAnonymizationCommand.setTargetk(anonymization.getTargetK());
            editAnonymizationCommand.setFast(anonymization.getFast());
            editAnonymizationCommand.setBatch(anonymization.getBatch());
        }
        return editAnonymizationCommand;
    }

    public Anonymization updateAnonymization(EditAnonymizationCommand editAnonymizationCommand, Long id) {
        Optional<Anonymization> anonymizationOptional = anonymizationRepository.findById(id);
        if (anonymizationOptional.isPresent()) {
            Anonymization anonymization = anonymizationOptional.get();
            anonymization.setName(editAnonymizationCommand.getName());
            anonymization.setTargetK(editAnonymizationCommand.getTargetk());
            anonymization.setFast(editAnonymizationCommand.getFast());
            anonymization.setBatch(editAnonymizationCommand.getBatch());
            anonymization.setAnonymized(false);
            anonymizationRepository.save(anonymization);
            return anonymization;
        }
        return null;
    }

    public Anonymization saveColumns(Long id, AnonymizationColumnsCommand command) throws IOException {
        Anonymization anonymization = null;
        Optional<Anonymization> anonymizationOptional = anonymizationRepository.findById(id);
        if (anonymizationOptional.isPresent()) {
            anonymization = anonymizationOptional.get();
        }

        if (anonymization != null) {
            List<AnonymizationColumn> propertyList = new LinkedList<>();
            for (AnonymizationColumnsCommandPropertiesCommand anonymizationColumnsCommandPropertiesCommand : command.getColumns()) {
                AnonymizationColumn property = new AnonymizationColumn();
                property.setId(anonymizationColumnsCommandPropertiesCommand.getId());
                property.setPosition(anonymizationColumnsCommandPropertiesCommand.getPosition());
                property.setName(anonymizationColumnsCommandPropertiesCommand.getName());
                property.setDataTyp(ColumnDataTyp.getByCode(anonymizationColumnsCommandPropertiesCommand.getDataTyp()));
                property.setTyp(ColumnTyp.getByCode(anonymizationColumnsCommandPropertiesCommand.getTyp()));
                if (anonymizationColumnsCommandPropertiesCommand.getHierarchyFile() != null && !anonymizationColumnsCommandPropertiesCommand.getHierarchyFile().isEmpty()) {
                    InputStream inputStream = anonymizationColumnsCommandPropertiesCommand.getHierarchyFile().getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    String content = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        content = content + line;
                    }
                    Gson gson = new Gson();
                    AnonymizationHierarchyNode node = gson.fromJson(content, AnonymizationHierarchyNode.class);
                    property.setHierarchyRoot(node);
                } else {
                    property.setHierarchyRoot(null);
                }
                propertyList.add(property);

            }

            anonymization.getColumns().clear();
            anonymization.getColumns().addAll(propertyList);
            anonymization.setAnonymized(false);
            return anonymizationRepository.save(anonymization);
        }

        return null;
    }

    public void delete(Long id) {
        anonymizationRepository.deleteById(id);
    }

    public InputStreamResource outputAnonymizedData(Long id) {
        Optional<Anonymization> anonymizationOptional = anonymizationRepository.findById(id);
        InputStreamResource file = null;
        if (anonymizationOptional.isPresent()) {
            Anonymization anonymization = anonymizationOptional.get();
            String out = anonymization.getHeading() + "\n";
            for (String outputDatum : anonymization.getOutputData()) {
                out += outputDatum;
            }
            file = new InputStreamResource(new ByteArrayInputStream(out.getBytes()));
        }
        return file;
    }
}
