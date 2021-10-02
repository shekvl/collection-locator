package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.ColumnPropertiesCommand;
import com.anonymizerweb.anonymizerweb.commands.ColumnPropertiesCommandProperties;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.ColumnProperty;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class AnonymizationPropertiesService {
    public ColumnPropertiesCommand anonymizationToPropertiesCommand(Anonymization anonymization) {
        ColumnPropertiesCommand result = new ColumnPropertiesCommand();
        List<ColumnPropertiesCommandProperties> properties = new LinkedList<>();
        result.setAnonymizationId(String.valueOf(anonymization.getId()));
        for (ColumnProperty columnProperty : anonymization.getColumnProperties()) {
            ColumnPropertiesCommandProperties prop = new ColumnPropertiesCommandProperties();
            prop.setId(columnProperty.getId());
            prop.setPosition(columnProperty.getPosition());
            prop.setName(columnProperty.getName());
            prop.setTyp(columnProperty.getTyp().getCode());
            prop.setDataTyp(columnProperty.getDataTyp().getCode());
            properties.add(prop);
        }
        Collections.sort(properties);
        result.setPropertyList(properties);
        return result;
    }
}
