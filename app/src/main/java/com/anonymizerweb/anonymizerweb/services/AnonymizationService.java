package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.*;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.*;
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

    public Anonymization save(NewAnonymizationCommand command) throws IOException {
        Anonymization anonymization = new Anonymization();
        String line;
        List<String> data = new LinkedList<>();
        InputStream inputStream = command.getFile().getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        line = bufferedReader.readLine();
        anonymization.setHeading(line);
        String[] split = line.split(";");
        Integer cnt = 1;
        for (String s : split) {
            AnonymizationColumn property = new AnonymizationColumn();
            property.setPosition(cnt);
            property.setName(s);
            property.setDataTyp(ColumnDataTyp.NUMBER);
            property.setTyp(ColumnTyp.QUASIIDENTIFIER);
            if (anonymization.getColumns() == null) {
                anonymization.setColumns(new HashSet<>());
            }

            anonymization.getColumns().add(property);
            cnt++;
        }

        while ((line = bufferedReader.readLine()) != null) {
            data.add(line);
        }

        anonymization.setRawData(data);
        anonymization.setTargetK(command.getTargetk());
        anonymization.setName(command.getName());
        anonymization.setFileName(command.getFile().getOriginalFilename());
        anonymization.setFast(command.getFast());
        anonymization.setBatch(command.getBatch());

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

    public void anonymzieFromImport(List<Long> ids) throws InterruptedException {
        List<CombineCommand> combineCommands = new LinkedList<>();
        List<Collection> collections = collectionService.findAll();

        for (Long id : ids) {
            Definition definition = definitionService.findbyId(id);
            for (Collection collection : collections) {
                CombineCommand combineCommand = new CombineCommand();
                combineCommand.setCollectionId(collection.getId());
                combineCommand.setDefinitionId(definition.getId());
                combineCommands.add(combineCommand);
            }
        }
        for (CombineCommand combineCommand : combineCommands) {
            MatchCommand matchCommand = combineService.getMatchCommandFromCombine(combineCommand);
            if(matchCommand.getMatchable()){
                Anonymization anonymization = saveFromMatch(matchCommand);
                anonymizeService.anonymize(anonymization.getId());
            }
        }
    }
}
