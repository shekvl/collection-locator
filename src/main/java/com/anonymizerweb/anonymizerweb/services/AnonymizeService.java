package com.anonymizerweb.anonymizerweb.services;

import com.anonymizerweb.anonymizerweb.data.AnonymizationHierarchyNode;
import com.anonymizerweb.anonymizerweb.data.Column;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.ColumnProperty;
import com.anonymizerweb.anonymizerweb.entities.CustomHierarchyNode;
import com.anonymizerweb.anonymizerweb.logic.Anonymizer;
import com.anonymizerweb.anonymizerweb.repositories.AnonymizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.DoubleAdder;

@Service
public class AnonymizeService {
    Logger logger = LoggerFactory.getLogger(AnonymizeService.class);

    @Autowired
    AnonymizationRepository anonymizationRepository;

    @Async
    public void anonymize(Long id) throws InterruptedException {
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
            for (ColumnProperty columnProperty : anonymization.getColumnProperties()) {
                Column column = new Column();
                column.setMin(Double.MAX_VALUE);
                column.setMax(Double.MIN_VALUE);
                column.setName(columnProperty.getName());
                column.setPosition(columnProperty.getPosition());
                column.setDataTyp(columnProperty.getDataTyp());
                column.setTyp(columnProperty.getTyp());
                if(columnProperty.getHierarchyRoot() != null){
                    AnonymizationHierarchyNode anonymizationHierarchyNode = entityNodeToDataNode(columnProperty.getHierarchyRoot());
                    setParents(anonymizationHierarchyNode, null);
                    column.setHierarchyRootNode(anonymizationHierarchyNode);
                }else {
                    columnProperty.setHierarchyRoot(null);
                }
                cols.put(columnProperty.getPosition(), column);
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
            anonymization.setLoss(anonymizer.getLoss().sum() / anonymizer.getSteps().sum());
            anonymizationRepository.save(anonymization);
        }
    }

    private AnonymizationHierarchyNode entityNodeToDataNode(CustomHierarchyNode node) {
        if (node != null) {
            AnonymizationHierarchyNode anonymizationHierarchyNode = new AnonymizationHierarchyNode();
            anonymizationHierarchyNode.setChildren(new LinkedList<>());
            anonymizationHierarchyNode.setValue(node.getValue());
            anonymizationHierarchyNode.setDescendants(0);
            anonymizationHierarchyNode.setSort(node.getSort());
            for (CustomHierarchyNode child : node.getChildren()) {
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
