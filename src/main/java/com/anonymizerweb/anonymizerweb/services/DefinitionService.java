package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.NewDefinitionCommand;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.Definition;
import com.anonymizerweb.anonymizerweb.repositories.DefinitionRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class DefinitionService {
    Logger logger = LoggerFactory.getLogger(DefinitionService.class);

    @Autowired
    DefinitionRepository definitionRepository;

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

    public Definition save(NewDefinitionCommand command) throws IOException {
        String line;
        String content = "";
        InputStream inputStream = command.getFile().getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((line = bufferedReader.readLine()) != null) {
            content = content + line;
        }
        Gson gson = new Gson();
        Definition definition = gson.fromJson(content, Definition.class);
        definition.setFileName(command.getFile().getOriginalFilename());

        return definitionRepository.save(definition);

    }

    public void delete(Long id) {
        definitionRepository.deleteById(id);
    }
}
