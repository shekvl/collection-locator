package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.OptionsCommand;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Options;
import com.anonymizerweb.anonymizerweb.repositories.anonymizer.OptionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OptionsService {
    Logger logger = LoggerFactory.getLogger(OptionsService.class);

    @Autowired
    OptionsRepository optionsRepository;

    public Options getValueByKey(String key) {
        Options result = null;
        Optional<Options> option = optionsRepository.findById(key);

        if (option.isPresent()) {
            result = option.get();
        }

        return result;
    }

    public OptionsCommand getOptionsCommand() {
        OptionsCommand command = new OptionsCommand();

        Options indexGen = getValueByKey("indexGen");
        if (indexGen != null) {
            command.setIndexGenUrl(indexGen.getOptValue());
        } else {
            command.setIndexGenUrl("");
        }

        Options indexColl = getValueByKey("indexColl");
        if (indexGen != null) {
            command.setCollectUrl(indexColl.getOptValue());
        } else {
            command.setCollectUrl("");
        }


        Options biobankId = getValueByKey("biobankId");
        if (biobankId != null) {
            command.setBiobankId(biobankId.getOptValue());
        } else {
            command.setBiobankId("");
        }

        return command;
    }

    public void saveFromOptionsCommand(OptionsCommand command) {
        Options indexGen = getValueByKey("indexGen");
        if (indexGen == null) {
            indexGen = new Options();
            indexGen.setOptKey("indexGen");
        }
        indexGen.setOptValue(command.getIndexGenUrl());
        optionsRepository.save(indexGen);

        Options indexColl = getValueByKey("indexColl");
        if (indexColl == null) {
            indexColl = new Options();
            indexColl.setOptKey("indexColl");
        }
        indexColl.setOptValue(command.getCollectUrl());
        optionsRepository.save(indexColl);

        Options biobankId = getValueByKey("biobankId");
        if (biobankId == null) {
            biobankId = new Options();
            biobankId.setOptKey("biobankId");
        }
        biobankId.setOptValue(command.getBiobankId());
        optionsRepository.save(biobankId);
    }
}
