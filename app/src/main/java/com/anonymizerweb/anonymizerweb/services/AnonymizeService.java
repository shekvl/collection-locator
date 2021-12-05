package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.commands.CombineCommand;
import com.anonymizerweb.anonymizerweb.commands.MatchCommand;
import com.anonymizerweb.anonymizerweb.data.AnonymizationHierarchyNode;
import com.anonymizerweb.anonymizerweb.data.Column;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDto;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoColumn;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoHierarchyNodeDto;
import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoList;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.AnonymizationColumn;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.CollectionHierarchyNode;
import com.anonymizerweb.anonymizerweb.entities.Definition;
import com.anonymizerweb.anonymizerweb.logic.Anonymizer;
import com.anonymizerweb.anonymizerweb.repositories.AnonymizationRepository;
import com.google.gson.Gson;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.DoubleAdder;

@Service
public class AnonymizeService {
    Logger logger = LoggerFactory.getLogger(AnonymizeService.class);

    @Autowired
    AnonymizationRepository anonymizationRepository;

    @Async
    public Future<String> anonymize(Long id) throws InterruptedException {
        Optional<Anonymization> optionalAnonymization = anonymizationRepository.findById(id);
        Anonymization anonymization = null;
        Instant start = Instant.now();
        if (optionalAnonymization.isPresent()) {
            anonymization = optionalAnonymization.get();
            Anonymizer anonymizer = new Anonymizer();
            anonymizer.setAnonymizationId(anonymization.getId());
            anonymizer.setRawData(anonymization.getRawData());
            anonymizer.setFastMode(anonymization.getFast());
            anonymizer.setBatchSize(anonymization.getBatch());
            anonymizer.setLoss(new DoubleAdder());
            anonymizer.setSteps(new DoubleAdder());
            Map<Integer, Column> cols = new HashMap<>();
            for (AnonymizationColumn anonymizationColumn : anonymization.getColumns()) {
                Column column = new Column();
                column.setMin(Double.MAX_VALUE);
                column.setMax(Double.MIN_VALUE);
                column.setName(anonymizationColumn.getName());
                column.setPosition(anonymizationColumn.getPosition());
                column.setDataTyp(anonymizationColumn.getDataTyp());
                column.setTyp(anonymizationColumn.getTyp());
                if(anonymizationColumn.getHierarchyRoot() != null){
                    AnonymizationHierarchyNode anonymizationHierarchyNode = entityNodeToDataNode(anonymizationColumn.getHierarchyRoot());
                    setParents(anonymizationHierarchyNode, null);
                    column.setHierarchyRootNode(anonymizationHierarchyNode);
                }else {
                    anonymizationColumn.setHierarchyRoot(null);
                }
                cols.put(anonymizationColumn.getPosition(), column);
            }

            anonymizer.setColumns(cols);
            anonymizer.setTargetK(anonymization.getTargetK());
            anonymization.setAnonymized(false);
            anonymization.setRunning(true);
            anonymizationRepository.save(anonymization);
            logger.info("ANONYMIZE " + anonymization.getName());
            anonymizer.startAnonymize();
            logger.info("DONE");
            anonymization.setOutputData(anonymizer.outputAnonymizedData());
            anonymization.setRunning(false);
            anonymization.setAnonymized(true);
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            Integer minutes = Math.toIntExact((timeElapsed / 1000) / 60);
            Integer seconds = Math.toIntExact((timeElapsed / 1000) % 60);
            anonymization.setDuration(minutes + " Minutes and " + seconds + " Seconds");

            try {
                Double parseDouble = Double.parseDouble(String.valueOf((anonymizer.getLoss().sum() / anonymizer.getSteps().sum())));
                if(parseDouble.isNaN()){
                    anonymization.setLoss(0.0);
                }else {
                    anonymization.setLoss(anonymizer.getLoss().sum() / anonymizer.getSteps().sum());
                }
            } catch (NumberFormatException e) {
                anonymization.setLoss(0.0);
            }

            anonymizationRepository.save(anonymization);
        }

        return new AsyncResult<>("done");
    }

    private AnonymizationHierarchyNode entityNodeToDataNode(com.anonymizerweb.anonymizerweb.entities.AnonymizationHierarchyNode node) {
        if (node != null) {
            AnonymizationHierarchyNode anonymizationHierarchyNode = new AnonymizationHierarchyNode();
            anonymizationHierarchyNode.setChildren(new LinkedList<>());
            anonymizationHierarchyNode.setValue(node.getValue());
            anonymizationHierarchyNode.setDescendants(0);
            anonymizationHierarchyNode.setSort(node.getSort());
            for (com.anonymizerweb.anonymizerweb.entities.AnonymizationHierarchyNode child : node.getChildren()) {
                anonymizationHierarchyNode.getChildren().add(entityNodeToDataNode(child));
            }
            return anonymizationHierarchyNode;
        }
        return null;
    }

    private void setParents(AnonymizationHierarchyNode node, AnonymizationHierarchyNode parent) {
        if (node.getChildren() == null || node.getChildren().size() == 0) {
            node.setParent(parent);
        } else {
            for (AnonymizationHierarchyNode child : node.getChildren()) {
                setParents(child, node);
            }
            node.setParent(parent);
            for (AnonymizationHierarchyNode child : node.getChildren()) {
                node.setDescendants(node.getDescendants() + child.getDescendants() + 1);
            }
        }
    }
}
