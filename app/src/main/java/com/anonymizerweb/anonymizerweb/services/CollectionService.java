package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.EditCollectionCommand;
import com.anonymizerweb.anonymizerweb.commands.EditCollectionCommandColumn;
import com.anonymizerweb.anonymizerweb.commands.NewCollectionCommand;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.CollectionColumn;
import com.anonymizerweb.anonymizerweb.entities.CollectionHierarchyNode;
import com.anonymizerweb.anonymizerweb.entities.Loinc;
import com.anonymizerweb.anonymizerweb.enums.CollectionUsageTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.repositories.CollectionRepository;
import com.anonymizerweb.anonymizerweb.repositories.LoincRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CollectionService {
    Logger logger = LoggerFactory.getLogger(CollectionService.class);

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    LoincRepository loincRepository;

    public Collection findbyId(Long id) {
        Optional<Collection> byId = collectionRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<Collection> findAll() {
        List<Collection> collections = new LinkedList<>();
        for (Collection collection : collectionRepository.findAll()) {
            collections.add(collection);
        }

        return collections;
    }

    public Integer findNumberOfCollections() {
        return ((java.util.Collection<?>) collectionRepository.findAll()).size();
    }

    public Collection save(NewCollectionCommand command) throws IOException {
        Collection collection = new Collection();
        String line;
        List<String> data = new LinkedList<>();
        InputStream inputStream = command.getFile().getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        line = bufferedReader.readLine();
        collection.setHeading(line);
        String[] split = line.split(";");
        Integer cnt = 1;
        Set<CollectionColumn> collectionColumnList = new HashSet<>();
        for (String s : split) {
            CollectionColumn collectionColumn = new CollectionColumn();
            collectionColumn.setPosition(cnt);
            collectionColumn.setName(s);
            collectionColumn.setCode("");
            collectionColumn.setTyp(ColumnTyp.QUASIIDENTIFIER);
            collectionColumn.setDataTyp(ColumnDataTyp.NUMBER);
            collectionColumn.setHierarchyRoot(null);

            collectionColumnList.add(collectionColumn);
            cnt++;
        }

        collection.setName(command.getName());
        collection.setTargetK(command.getTargetK());
        collection.setUsageTyp(CollectionUsageTyp.getByCode(command.getUsageTyp()));
        collection.setFileName(command.getFile().getOriginalFilename());
        collection.setColumns(collectionColumnList);

        while ((line = bufferedReader.readLine()) != null) {
            data.add(line);
        }

        collection.setRawData(data);
        return collectionRepository.save(collection);

    }

    public EditCollectionCommand getEditCommand(Long id) {
        EditCollectionCommand editCollectionCommand = new EditCollectionCommand();
        Optional<Collection> collectionOptional = collectionRepository.findById(id);
        if (collectionOptional.isPresent()) {
            Collection collection = collectionOptional.get();
            editCollectionCommand.setName(collection.getName());
            editCollectionCommand.setTargetK(collection.getTargetK());
            editCollectionCommand.setUsageTyp(collection.getUsageTyp().getCode());
            List<CollectionColumn> collectionColumns = new LinkedList<>(collection.getColumns());
            Collections.sort(collectionColumns);
            List<EditCollectionCommandColumn> editCollectionCommandColumns = new LinkedList<>();
            for (CollectionColumn collectionColumn : collectionColumns) {
                EditCollectionCommandColumn editCollectionCommandColumn = new EditCollectionCommandColumn();
                editCollectionCommandColumn.setId(collectionColumn.getId());
                editCollectionCommandColumn.setPosition(collectionColumn.getPosition());
                editCollectionCommandColumn.setName(collectionColumn.getName());
                editCollectionCommandColumn.setCode(collectionColumn.getCode());
                Optional<Loinc> byId = loincRepository.findById(collectionColumn.getCode());
                if(byId.isPresent()){
                    Loinc loinc = byId.get();
                    editCollectionCommandColumn.setCodeText(loinc.getLong_common_name() + " (" + loinc.getLoinc_num() + ")");
                }
                editCollectionCommandColumn.setTyp(collectionColumn.getTyp().getCode());
                editCollectionCommandColumn.setDataTyp(collectionColumn.getDataTyp().getCode());
                editCollectionCommandColumns.add(editCollectionCommandColumn);
            }
            editCollectionCommand.setColumns(editCollectionCommandColumns);
        }
        return editCollectionCommand;
    }

    public Collection updateCollection(EditCollectionCommand editCollectionCommand, Long id) throws IOException {
        Optional<Collection> collectionOptional = collectionRepository.findById(id);
        Collection save = null;
        if (collectionOptional.isPresent()) {
            Collection collection = collectionOptional.get();
            collection.setName(editCollectionCommand.getName());
            collection.setTargetK(editCollectionCommand.getTargetK());
            collection.setUsageTyp(CollectionUsageTyp.getByCode(editCollectionCommand.getUsageTyp()));
            Set<CollectionColumn> collectionColumns = collection.getColumns();
            collectionColumns.clear();
            List<CollectionColumn> collectionColumnList = new LinkedList<>();
            for (EditCollectionCommandColumn editCollectionCommandColumn : editCollectionCommand.getColumns()) {
                CollectionColumn collectionColumn = new CollectionColumn();
                collectionColumn.setId(editCollectionCommandColumn.getId());
                collectionColumn.setPosition(editCollectionCommandColumn.getPosition());
                collectionColumn.setName(editCollectionCommandColumn.getName());
                collectionColumn.setCode(editCollectionCommandColumn.getCode());
                collectionColumn.setTyp(ColumnTyp.getByCode(editCollectionCommandColumn.getTyp()));
                collectionColumn.setDataTyp(ColumnDataTyp.getByCode(editCollectionCommandColumn.getDataTyp()));
                if (editCollectionCommandColumn.getHierarchyFile() != null && !editCollectionCommandColumn.getHierarchyFile().isEmpty()) {
                    InputStream inputStream = editCollectionCommandColumn.getHierarchyFile().getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    String content = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        content = content + line;
                    }
                    Gson gson = new Gson();
                    CollectionHierarchyNode node = gson.fromJson(content, CollectionHierarchyNode.class);
                    collectionColumn.setHierarchyRoot(node);
                } else {
                    collectionColumn.setHierarchyRoot(null);
                }
                collectionColumnList.add(collectionColumn);
            }
            collectionColumns.addAll(collectionColumnList);
            save = collectionRepository.save(collection);
        }
        return save;
    }

    public void delete(Long id) {
        collectionRepository.deleteById(id);
    }
}
