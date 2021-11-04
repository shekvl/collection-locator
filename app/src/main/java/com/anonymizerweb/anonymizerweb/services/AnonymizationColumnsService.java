package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.AnonymizationColumnsCommand;
import com.anonymizerweb.anonymizerweb.commands.AnonymizationColumnsCommandPropertiesCommand;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.AnonymizationColumn;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class AnonymizationColumnsService {
    public AnonymizationColumnsCommand anonymizationToColumnsCommand(Anonymization anonymization) {
        AnonymizationColumnsCommand result = new AnonymizationColumnsCommand();
        List<AnonymizationColumnsCommandPropertiesCommand> properties = new LinkedList<>();
        result.setAnonymizationId(String.valueOf(anonymization.getId()));
        for (AnonymizationColumn anonymizationColumn : anonymization.getColumns()) {
            AnonymizationColumnsCommandPropertiesCommand prop = new AnonymizationColumnsCommandPropertiesCommand();
            prop.setId(anonymizationColumn.getId());
            prop.setPosition(anonymizationColumn.getPosition());
            prop.setName(anonymizationColumn.getName());
            prop.setTyp(anonymizationColumn.getTyp().getCode());
            prop.setDataTyp(anonymizationColumn.getDataTyp().getCode());
            properties.add(prop);
        }
        Collections.sort(properties);
        result.setColumns(properties);
        return result;
    }
}
