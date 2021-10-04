package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.*;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.*;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.repositories.AnonymizationRepository;
import com.google.gson.Gson;
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
            ColumnProperty property = new ColumnProperty();
            property.setPosition(cnt);
            property.setName(s);
            property.setDataTyp(ColumnDataTyp.NUMBER);
            property.setTyp(ColumnTyp.QUASIIDENTIFIER);
            if (anonymization.getColumnProperties() == null) {
                anonymization.setColumnProperties(new HashSet<>());
            }

            anonymization.getColumnProperties().add(property);
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
        for (DefinitionColumn definitionColumn : definitionColumns) {
            ColumnProperty property = new ColumnProperty();
            property.setPosition(definitionColumn.getPosition());
            property.setName(definitionColumn.getName());
            property.setDataTyp(ColumnDataTyp.NUMBER);
            property.setTyp(ColumnTyp.QUASIIDENTIFIER);
            if (anonymization.getColumnProperties() == null) {
                anonymization.setColumnProperties(new HashSet<>());
            }
            anonymization.getColumnProperties().add(property);

            heading = heading + definitionColumn.getName();
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

        return anonymizationRepository.save(anonymization);
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

    public Anonymization saveProperties(Long id, ColumnPropertiesCommand command) throws IOException {
        Anonymization anonymization = null;
        Optional<Anonymization> anonymizationOptional = anonymizationRepository.findById(id);
        if (anonymizationOptional.isPresent()) {
            anonymization = anonymizationOptional.get();
        }

        if (anonymization != null) {
            List<ColumnProperty> propertyList = new LinkedList<>();
            for (ColumnPropertiesCommandProperties columnPropertiesCommandProperties : command.getPropertyList()) {
                ColumnProperty property = new ColumnProperty();
                property.setId(columnPropertiesCommandProperties.getId());
                property.setPosition(columnPropertiesCommandProperties.getPosition());
                property.setName(columnPropertiesCommandProperties.getName());
                property.setDataTyp(ColumnDataTyp.getByCode(columnPropertiesCommandProperties.getDataTyp()));
                property.setTyp(ColumnTyp.getByCode(columnPropertiesCommandProperties.getTyp()));
                if (columnPropertiesCommandProperties.getHierarchyFile() != null && !columnPropertiesCommandProperties.getHierarchyFile().isEmpty()) {
                    InputStream inputStream = columnPropertiesCommandProperties.getHierarchyFile().getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    String content = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        content = content + line;
                    }
                    Gson gson = new Gson();
                    CustomHierarchyNode node = gson.fromJson(content, CustomHierarchyNode.class);
                    property.setHierarchyRoot(node);
                } else {
                    property.setHierarchyRoot(null);
                }
                propertyList.add(property);

            }

            anonymization.getColumnProperties().clear();
            anonymization.getColumnProperties().addAll(propertyList);
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
