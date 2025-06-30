package com.anonymizerweb.anonymizerweb.logic;

import com.anonymizerweb.anonymizerweb.controller.AnonymizationController;
import com.anonymizerweb.anonymizerweb.data.AnonymizationHierarchyNode;
import com.anonymizerweb.anonymizerweb.data.Cell;
import com.anonymizerweb.anonymizerweb.data.Column;
import com.anonymizerweb.anonymizerweb.data.Row;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.structures.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;

public class Anonymizer {
    Logger logger = LoggerFactory.getLogger(AnonymizationController.class);

    private Long anonymizationId;
    private Boolean fastMode;
    private Integer batchSize;
    private Integer targetK;
    private ConcurrentHashMap<String, Group> groups;
    private Map<Integer, Column> columns;
    private List<String> rawData;
    private DoubleAdder loss;
    private DoubleAdder steps;

    public Anonymizer() {
        groups = new ConcurrentHashMap<>();
        columns = new HashMap<>();
    }

    public Long getAnonymizationId() {
        return anonymizationId;
    }

    public void setAnonymizationId(Long anonymizationId) {
        this.anonymizationId = anonymizationId;
    }

    public Boolean getFastMode() {
        return fastMode;
    }

    public void setFastMode(Boolean fastMode) {
        this.fastMode = fastMode;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getTargetK() {
        return targetK;
    }

    public void setTargetK(Integer targetK) {
        this.targetK = targetK;
    }

    public Map<Integer, Column> getColumns() {
        return columns;
    }

    public void setColumns(Map<Integer, Column> columns) {
        this.columns = columns;
    }

    public List<String> getRawData() {
        return rawData;
    }

    public void setRawData(List<String> rawData) {
        this.rawData = rawData;
    }

    public ConcurrentHashMap<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(ConcurrentHashMap<String, Group> groups) {
        this.groups = groups;
    }

    public DoubleAdder getLoss() {
        return loss;
    }

    public void setLoss(DoubleAdder loss) {
        this.loss = loss;
    }

    public DoubleAdder getSteps() {
        return steps;
    }

    public void setSteps(DoubleAdder steps) {
        this.steps = steps;
    }

    public void startAnonymize() throws InterruptedException {
        for (String rawDatum : rawData) {
            initColumnBorders(rawDatum);
        }

        List<String> data = new LinkedList<>(rawData);
        if (fastMode) {
            List<List<String>> batches = new LinkedList<>();
            List<List<String>> okBatches = new LinkedList<>();
            List<String> list = new LinkedList<>();
            Integer batchSize = getBatchSize();
            for (String datum : data) {
                if (list.size() >= batchSize) {
                    okBatches.add(list);
                    list = new LinkedList<>();
                }
                list.add(datum);
            }
            if (list.size() > 0) {
                batches.add(list);
            }

            for (List<String> batch : batches) {
                if (batch.size() < (targetK * 5)) {
                    if (okBatches.size() > 0) {
                        okBatches.get(0).addAll(batch);
                    } else {
                        okBatches.add(batch);
                    }
                } else {
                    okBatches.add(batch);
                }
            }

            List<Thread> threadList = new LinkedList<>();
            List<AnonymizerThread> anonymizerThreadList = new LinkedList<>();
            Integer cnt = 1;
            for (List<String> batch : okBatches) {
                AnonymizerThread anonymizerThread = new AnonymizerThread();
                anonymizerThread.setAnonymizer(this);
                anonymizerThread.setPartialData(batch);
                anonymizerThread.setThreadId(cnt);
                cnt++;
                anonymizerThreadList.add(anonymizerThread);
            }

            for (AnonymizerThread anonymizerThread : anonymizerThreadList) {
                threadList.add(new Thread(anonymizerThread));
            }

            for (Thread thread : threadList) {
                thread.start();
            }
            for (Thread thread : threadList) {
                thread.join();
            }
        } else {
            AnonymizerThread anonymizerThread = new AnonymizerThread();
            anonymizerThread.setAnonymizer(this);
            anonymizerThread.setPartialData(data);
            anonymizerThread.setThreadId(1);
            Thread thread = new Thread(anonymizerThread);
            thread.start();
            thread.join();
        }

        logger.info("ALL THREADS DONE " + anonymizationId);
    }

    public List<String> outputAnonymizedData() {
        List<String> out = new LinkedList<>();
        for (Group group : groups.values()) {
            String[] split = group.getKey().split(";");
            for (Row row : group.getRows()) {
                String print = "";
                Integer c = 0;
                for (Cell cell : row.getCells()) {
                    if (columns.get(c + 1).getTyp().equals(ColumnTyp.QUASIIDENTIFIER)) {
                        if (columns.get(c + 1).getDataTyp().equals(ColumnDataTyp.NUMBER)) {
                            print += split[c];
                        } else if (columns.get(c + 1).getDataTyp().equals(ColumnDataTyp.STRUCTUREDNUMBER)) {
                            String from = split[c].replace('*', '0');
                            String to = split[c].replace('*', '9');
                            print += from + ":" + to;
                        } else if (columns.get(c + 1).getDataTyp().equals(ColumnDataTyp.CUSTOMHIERARCHY)) {
                            AnonymizationHierarchyNode hierarchyElementForValue = findHierarchyElementForValue(split[c], columns.get(c + 1).getHierarchyRootNode());
                            if(hierarchyElementForValue != null){
                                List<AnonymizationHierarchyNode> childrenOfHierarchyElement = findChildrenOfHierarchyElement(hierarchyElementForValue);
                                Boolean sortable = true;
                                for (AnonymizationHierarchyNode anonymizationHierarchyNode : childrenOfHierarchyElement) {
                                    if(anonymizationHierarchyNode.getSort() == null || anonymizationHierarchyNode.getSort().equals("")){
                                        sortable = false;
                                        break;
                                    }
                                }
                                if(sortable){
                                    Collections.sort(childrenOfHierarchyElement);



                                    print += childrenOfHierarchyElement.get(0).getValue() + ":" + childrenOfHierarchyElement.get(childrenOfHierarchyElement.size()-1).getValue();
                                }else {
                                    print += split[c] + ":" + split[c];
                                }
                            }else {
                                print += split[c] + ":" + split[c];
                            }
                        }
                    } else if (columns.get(c + 1).getTyp().equals(ColumnTyp.IDENTIFIER)) {
                        print += "*";
                    } else if (columns.get(c + 1).getTyp().equals(ColumnTyp.SECRET)) {
                        print += cell.getValue();
                    }
                    c++;
                    print += (c >= split.length ? "\n" : ";");
                }
                out.add(print);
            }
        }
        Collections.shuffle(out);

        return out;
    }


    private List<AnonymizationHierarchyNode> findChildrenOfHierarchyElement(AnonymizationHierarchyNode node) {
        List<AnonymizationHierarchyNode> leafs = new LinkedList<>();
        if (node.getChildren() == null || node.getChildren().size() == 0) {
            leafs.add(node);
        } else {
            for (AnonymizationHierarchyNode child : node.getChildren()) {
                List<AnonymizationHierarchyNode> list = findChildrenOfHierarchyElement(child);
                leafs.addAll(list);
            }
        }
        return leafs;
    }


    private AnonymizationHierarchyNode findHierarchyElementForValue(String search, AnonymizationHierarchyNode node) {
        if (node.getValue().equals(search)) {
            return node;
        } else {
            if (node.getChildren() == null || node.getChildren().size() == 0) {
                return null;
            } else {
                for (AnonymizationHierarchyNode child : node.getChildren()) {
                    AnonymizationHierarchyNode childNode = findHierarchyElementForValue(search, child);
                    if (childNode != null) {
                        return childNode;
                    }
                }
            }
        }
        return null;
    }

    private void initColumnBorders(String line) {
        String[] cells = line.split(";");
        Integer k = 0;
        for (String val : cells) {
            Cell cell = new Cell();
            cell.setPosition(k + 1);
            cell.setValue(val);
            if ((columns.get(k + 1).getTyp().equals(ColumnTyp.QUASIIDENTIFIER)) && (columns.get(k + 1).getDataTyp().equals(ColumnDataTyp.NUMBER)) && (!(val.equals("null") || val.equals("")))) {
                updateColumnBorders(k + 1, val);
            }
            k++;
        }
    }

    private void updateColumnBorders(Integer position, String val) {
        Double currentMin = columns.get(position).getMin();
        Double currentMax = columns.get(position).getMax();
        String[] split = val.split(":");
        if (split.length == 2) {
            if (Double.valueOf(split[0]) < currentMin) {
                columns.get(position).setMin(Double.valueOf(split[0]));
            }
            if (Double.valueOf(split[1]) > currentMax) {
                columns.get(position).setMax(Double.valueOf(split[1]));
            }
        } else {
            if (Double.valueOf(val) < currentMin) {
                columns.get(position).setMin(Double.valueOf(val));
            }
            if (Double.valueOf(val) > currentMax) {
                columns.get(position).setMax(Double.valueOf(val));
            }
        }
    }
}
