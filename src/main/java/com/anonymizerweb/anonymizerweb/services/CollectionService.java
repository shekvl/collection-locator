package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.EditCollectionCommand;
import com.anonymizerweb.anonymizerweb.commands.NewCollectionCommand;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.CollectionColumn;
import com.anonymizerweb.anonymizerweb.repositories.CollectionRepository;
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

            collectionColumnList.add(collectionColumn);
            cnt++;
        }

        collection.setName(command.getName());
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
        }
        return editCollectionCommand;
    }

    public Collection updateCollection(EditCollectionCommand editCollectionCommand, Long id) {
        Optional<Collection> collectionOptional = collectionRepository.findById(id);
        Collection save = null;
        if (collectionOptional.isPresent()) {
            Collection collection = collectionOptional.get();
            collection.setName(editCollectionCommand.getName());
            save = collectionRepository.save(collection);
        }
        return save;
    }

    public void delete(Long id) {
        collectionRepository.deleteById(id);
    }
}
