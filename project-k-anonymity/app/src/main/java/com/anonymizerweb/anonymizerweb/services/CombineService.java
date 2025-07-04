package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.CombineCommand;
import com.anonymizerweb.anonymizerweb.commands.MatchColumnCommand;
import com.anonymizerweb.anonymizerweb.commands.MatchCommand;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Collection;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.CollectionColumn;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Definition;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.DefinitionColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class CombineService {
    Logger logger = LoggerFactory.getLogger(CombineService.class);

    @Autowired
    CollectionService collectionService;

    @Autowired
    DefinitionService definitionService;

    public MatchCommand getMatchCommandFromCombine(CombineCommand combineCommand){
        MatchCommand result = new MatchCommand();
        List<MatchColumnCommand> matchColumnList = new LinkedList<>();
        Definition definition = definitionService.findbyId(combineCommand.getDefinitionId());
        Collection collection = collectionService.findbyId(combineCommand.getCollectionId());
        result.setDefinitionId(combineCommand.getDefinitionId());
        result.setCollectionId(combineCommand.getCollectionId());
        result.setMatchable(true);

        for (DefinitionColumn definitionColumn : definition.getColumns()) {
            MatchColumnCommand matchColumnCommand = new MatchColumnCommand();
            matchColumnCommand.setDefinitionColumnPosition(definitionColumn.getPosition());
            matchColumnCommand.setDefinitionColumnName(definitionColumn.getName());
            matchColumnCommand.setDefinitionColumnCode(definitionColumn.getCode());
            matchColumnCommand.setMatched(false);
            for (CollectionColumn collectionColumn : collection.getColumns()) {
                if(collectionColumn.getCode() != null) {
                    if (collectionColumn.getCode().equals(definitionColumn.getCode())) {
                        matchColumnCommand.setCollectionColumnPosition(collectionColumn.getPosition());
                        matchColumnCommand.setCollectionColumnName(collectionColumn.getName());
                        matchColumnCommand.setCollectionColumnCode(collectionColumn.getCode());
                        matchColumnCommand.setMatched(true);
                        break;
                    }
                }
            }

            if(!matchColumnCommand.getMatched()){
                result.setMatchable(false);
            }
            matchColumnList.add(matchColumnCommand);
        }
        Collections.sort(matchColumnList);
        result.setColumns(matchColumnList);

        return result;
    }
}
