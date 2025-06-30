package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.data.AnonymizationHierarchyNode;
import com.anonymizerweb.anonymizerweb.data.Column;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.AnonymizationColumn;
import com.anonymizerweb.anonymizerweb.logic.Anonymizer;
import com.anonymizerweb.anonymizerweb.repositories.anonymizer.AnonymizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

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
    public Future<Anonymization> anonymize(Long id) throws InterruptedException {
        try {

            logger.info("START ANONYMIZATION");
            Anonymization save = null;
            Optional<Anonymization> optionalAnonymization = anonymizationRepository.findById(id);
            Anonymization anonymization = null;
            Instant start = Instant.now();
            if (optionalAnonymization.isPresent()) {
                logger.info("FOUND ANONYMIZATION");
                anonymization = optionalAnonymization.get();
                logger.info("GET ANONYMIZATION");
                Anonymizer anonymizer = new Anonymizer();
                anonymizer.setAnonymizationId(anonymization.getId());
                logger.info("SET ANONYMIZATION ID");
                anonymizer.setRawData(anonymization.getRawData());
                logger.info("SET ANONYMIZATION RAWDATA");
                anonymizer.setFastMode(anonymization.getFast());
                logger.info("SET ANONYMIZATION FASTMODE");
                anonymizer.setBatchSize(anonymization.getBatch());
                logger.info("SET ANONYMIZATION BATCHSIZE");
                anonymizer.setLoss(new DoubleAdder());
                logger.info("SET ANONYMIZATION LOSS");
                anonymizer.setSteps(new DoubleAdder());
                logger.info("SET ANONYMIZATION STEPS");
                Map<Integer, Column> cols = new HashMap<>();
                logger.info("TRY PRINT COLUMNS SIZE");
                if(anonymization.getColumns() == null || anonymization.getColumns().size()==0){
                    logger.info("COLUMNS ARE NULL");
                }
                for (AnonymizationColumn anonymizationColumn : anonymization.getColumns()) {
                    logger.info("START OF COLUMN LOOP");
                    Column column = new Column();
                    column.setMin(Double.MAX_VALUE);
                    column.setMax(Double.MIN_VALUE);
                    column.setName(anonymizationColumn.getName());
                    column.setPosition(anonymizationColumn.getPosition());
                    column.setDataTyp(anonymizationColumn.getDataTyp());
                    column.setTyp(anonymizationColumn.getTyp());
                    logger.info("ATTEMPT TO GET HIERARCHY ROOT");
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
                logger.info("TRY TO SAVE");
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

                save = anonymizationRepository.save(anonymization);
            }
            else{
                logger.info("ANONYMIZATION NOT FOUND");
            }

            return new AsyncResult<Anonymization>(save);
        } catch (Throwable t) {
            logger.error("FUCK: " + t.getMessage());
            throw t;
        }
    }

    private AnonymizationHierarchyNode entityNodeToDataNode(com.anonymizerweb.anonymizerweb.entities.anonymizer.AnonymizationHierarchyNode node) {
        if (node != null) {
            AnonymizationHierarchyNode anonymizationHierarchyNode = new AnonymizationHierarchyNode();
            anonymizationHierarchyNode.setChildren(new LinkedList<>());
            anonymizationHierarchyNode.setValue(node.getValue());
            anonymizationHierarchyNode.setDescendants(0);
            anonymizationHierarchyNode.setSort(node.getSort());
            for (com.anonymizerweb.anonymizerweb.entities.anonymizer.AnonymizationHierarchyNode child : node.getChildren()) {
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
