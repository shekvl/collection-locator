package com.anonymizerweb.anonymizerweb.logic;

import com.anonymizerweb.anonymizerweb.data.AnonymizationHierarchyNode;
import com.anonymizerweb.anonymizerweb.data.Cell;
import com.anonymizerweb.anonymizerweb.data.Column;
import com.anonymizerweb.anonymizerweb.data.Row;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.structures.Group;
import com.anonymizerweb.anonymizerweb.structures.Interval;
import com.anonymizerweb.anonymizerweb.structures.MergeColumn;
import com.anonymizerweb.anonymizerweb.structures.MergeStep;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnonymizerThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(AnonymizerThread.class);

    private Integer threadId;

    private Anonymizer anonymizer;

    private Map<String, Group> groups;

    private List<String> partialData;

    private Double loss;

    private Integer steps;

    private Map<Integer, Map<String, List<AnonymizationHierarchyNode>>> pathCache;

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public Anonymizer getAnonymizer() {
        return anonymizer;
    }

    public void setAnonymizer(Anonymizer anonymizer) {
        this.anonymizer = anonymizer;
    }

    public Map<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }

    public List<String> getPartialData() {
        return partialData;
    }

    public void setPartialData(List<String> partialData) {
        this.partialData = partialData;
    }

    public Double getLoss() {
        return loss;
    }

    public void setLoss(Double loss) {
        this.loss = loss;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Map<Integer, Map<String, List<AnonymizationHierarchyNode>>> getPathCache() {
        return pathCache;
    }

    public void setPathCache(Map<Integer, Map<String, List<AnonymizationHierarchyNode>>> pathCache) {
        this.pathCache = pathCache;
    }

    public Boolean existsGroup(String key) {
        return groups.containsKey(key);
    }

    public void addGroup(Group group) {
        groups.put(group.getKey(), group);
    }

    public void addRowToGroup(String key, Row row) {
        Group group = groups.get(key);
        group.addRow(row);
    }

    @Override
    public void run() {
        loss = 0.0d;
        steps = 0;
        groups = new HashMap<>();
        pathCache = new HashMap<>();
        anonymizeAll();
        for (Map.Entry<String, Group> entry : groups.entrySet()) {
            anonymizer.getGroups().merge(entry.getKey(), entry.getValue(), (group, group2) -> {
                for (Row row : group.getRows()) {
                    group.addRow(row);
                }
                return group;
            });
        }
        anonymizer.getLoss().add(loss);
        anonymizer.getSteps().add(steps);
    }

    private void initRow(String line) {
        Row row = new Row();
        String key = "";
        String[] cells = line.split(";");
        Integer k = 0;
        for (String val : cells) {
            String[] split = val.split(":");
            Cell cell = new Cell();
            cell.setPosition(k + 1);
            cell.setValue(val);
            if (anonymizer.getColumns().get(k + 1).getTyp().equals(ColumnTyp.QUASIIDENTIFIER)) {
                if (anonymizer.getColumns().get(k + 1).getDataTyp().equals(ColumnDataTyp.NUMBER)) {
                    if (val.equals("null") || val.equals("")) {
                        key += "NULL:NULL";
                    } else {
                        if (split.length == 2) {
                            key += val;
                        } else {
                            key += val + ":" + val;
                        }
                    }
                } else if (anonymizer.getColumns().get(k + 1).getDataTyp().equals(ColumnDataTyp.STRUCTUREDNUMBER)) {
                    if (val.equals("null") || val.equals("")) {
                        key += "NULL";
                    } else {
                        key += val;
                    }
                } else if (anonymizer.getColumns().get(k + 1).getDataTyp().equals(ColumnDataTyp.CUSTOMHIERARCHY)) {
                    if (val.equals("null") || val.equals("")) {
                        key += "NULL";
                    } else {
                        key += val;
                    }
                }
            } else {
                key += "-";
            }
            key += (k < cells.length - 1 ? ";" : "");
            row.addCell(cell);
            k++;
        }
        if (!existsGroup(key)) {
            Group group = new Group();
            group.setKey(key);
            group.addRow(row);
            addGroup(group);
        } else {
            addRowToGroup(key, row);
        }
    }

    public void anonymizationStep() {
        List<Group> groups = new LinkedList<>(this.groups.values());
        Group mergeGroup = null;
        Group candidateGroup = null;
        MergeStep currentMergeStep = new MergeStep();
        currentMergeStep.setNewKey("");
        currentMergeStep.setLoss(Double.MAX_VALUE);
        for (Group mgroup : groups) {
            if (mgroup.getRows().size() < anonymizer.getTargetK()) {
                mergeGroup = mgroup;
            }
        }
        for (Group group : groups) {
            if (mergeGroup != group) {
                MergeStep mergeStep = null;
                try {
                    mergeStep = calcLossOfMergeStep(mergeGroup, group);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mergeStep.getLoss() < currentMergeStep.getLoss()) {
                    candidateGroup = group;
                    currentMergeStep = mergeStep;
                    if (currentMergeStep.getLoss() == 0.0f) {
                        break;
                    }
                }
            }
        }

        this.groups.remove(mergeGroup.getKey());
        this.groups.remove(candidateGroup.getKey());
        Group newGroup = new Group();
        newGroup.setKey(currentMergeStep.getNewKey());
        newGroup.setRows(mergeGroup.getRows());
        newGroup.getRows().addAll(candidateGroup.getRows());
        this.groups.put(currentMergeStep.getNewKey(), newGroup);

        loss = loss + currentMergeStep.getLoss();
        steps++;
    }

    public void anonymizeAll() {
        Hibernate.initialize(getPartialData());
        for (String partialDatum : partialData) {
            initRow(partialDatum);
        }
        double target = partialData.size() - (partialData.size() / anonymizer.getTargetK());
        while (!isAnonymized()) {
            double cur = partialData.size() - groups.size();
            double pro = (cur / target) * 100;
            BigDecimal bd = BigDecimal.valueOf(pro);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            pro = bd.doubleValue();
            if (pro <= 100) {
                logger.info("Anonymization " + anonymizer.getAnonymizationId() +
                        " || AnonymizerThead " + this.threadId + " || Progress: " + pro + " %");
            }
            anonymizationStep();
        }
    }

    public Boolean isAnonymized() {
        for (Group group : groups.values()) {
            if (group.getRows().size() < anonymizer.getTargetK()) {
                return false;
            }
        }
        return true;
    }

    private MergeStep calcLossOfMergeStep(Group mergeGroup, Group candidateGroup) {
        String newKey = "";
        MergeStep mergeStep = new MergeStep();
        Double loss = 0.0;
        Integer i = 0;
        while (i < anonymizer.getColumns().size()) {
            MergeColumn mergeColumn = new MergeColumn();
            if (anonymizer.getColumns().get(i + 1).getTyp().equals(ColumnTyp.QUASIIDENTIFIER)) {
                if (anonymizer.getColumns().get(i + 1).getDataTyp().equals(ColumnDataTyp.NUMBER)) {
                    mergeColumn = calculateLossOfNumberColumn(anonymizer.getColumns().get(i + 1), mergeGroup, candidateGroup, i);
                }

                if (anonymizer.getColumns().get(i + 1).getDataTyp().equals(ColumnDataTyp.STRUCTUREDNUMBER)) {
                    mergeColumn = calculateLossOfStructuredNumberColumn(anonymizer.getColumns().get(i + 1), mergeGroup, candidateGroup, i);
                }

                if (anonymizer.getColumns().get(i + 1).getDataTyp().equals(ColumnDataTyp.CUSTOMHIERARCHY)) {
                    mergeColumn = calculateLossOfCustomHierarchyColumn(anonymizer.getColumns().get(i + 1), mergeGroup, candidateGroup, i);
                }

                loss += mergeColumn.getLoss();
                newKey += mergeColumn.getNewKey();
            } else {
                newKey += "-";
            }
            newKey += (i < anonymizer.getColumns().size() - 1 ? ";" : "");
            i++;
        }
        mergeStep.setLoss(loss);
        mergeStep.setNewKey(newKey);
        return mergeStep;
    }

    private MergeColumn calculateLossOfNumberColumn(Column column, Group mergeGroup, Group candidateGroup, Integer columnNr) {
        MergeColumn result = new MergeColumn();
        result.setLoss(0.0);
        result.setNewKey("");
        String[] mergeKeySplit = mergeGroup.getKey().split(";");
        String[] candidateKeySplit = candidateGroup.getKey().split(";");
        Double range = column.getMax() - column.getMin();
        if (mergeKeySplit[columnNr].contains("NULL") && candidateKeySplit[columnNr].contains("NULL")) {
            result.setLoss(0.0);
            result.setNewKey("NULL:NULL");
        } else if ((mergeKeySplit[columnNr].contains("NULL") && !candidateKeySplit[columnNr].contains("NULL")) ||
                (!mergeKeySplit[columnNr].contains("NULL") && candidateKeySplit[columnNr].contains("NULL"))) {
            result.setLoss(Double.valueOf((mergeGroup.getRows().size()) + (candidateGroup.getRows().size())));
            result.setNewKey("ALL:ALL");
        } else if (mergeKeySplit[columnNr].contains("ALL") && candidateKeySplit[columnNr].contains("ALL")) {
            result.setLoss(0.0);
            result.setNewKey("ALL:ALL");
        } else if ((mergeKeySplit[columnNr].contains("ALL") && !candidateKeySplit[columnNr].contains("ALL")) ||
                (!mergeKeySplit[columnNr].contains("ALL") && candidateKeySplit[columnNr].contains("ALL"))) {
            result.setLoss(Double.valueOf((mergeGroup.getRows().size()) + (candidateGroup.getRows().size())));
            result.setNewKey("ALL:ALL");
        } else {
            Interval newInterval = calculateNewInterval(mergeKeySplit[columnNr], candidateKeySplit[columnNr]);
            Interval mergeInterval = calculateInterval(mergeKeySplit[columnNr]);
            Interval candInterval = calculateInterval(mergeKeySplit[columnNr]);
            result.setLoss((Math.abs(newInterval.getValue() - mergeInterval.getValue()) / range) * mergeGroup.getRows().size() +
                    (Math.abs(newInterval.getValue() - candInterval.getValue()) / range) * candidateGroup.getRows().size());
            result.setNewKey(newInterval.getText());
        }
        return result;
    }

    private MergeColumn calculateLossOfStructuredNumberColumn(Column column, Group mergeGroup, Group candidateGroup, Integer columnNr) {
        MergeColumn result = new MergeColumn();
        result.setLoss(0.0);
        result.setNewKey("");
        String[] mergeKeySplit = mergeGroup.getKey().split(";");
        String[] candidateKeySplit = candidateGroup.getKey().split(";");
        if (mergeKeySplit[columnNr].contains("NULL") && candidateKeySplit[columnNr].contains("NULL")) {
            result.setLoss(0.0);
            result.setNewKey("NULL");
        } else if ((mergeKeySplit[columnNr].contains("NULL") && !candidateKeySplit[columnNr].contains("NULL")) ||
                (!mergeKeySplit[columnNr].contains("NULL") && candidateKeySplit[columnNr].contains("NULL"))) {
            result.setLoss(Double.valueOf((mergeGroup.getRows().size()) + (candidateGroup.getRows().size())));
            result.setNewKey("ALL");
        } else if (mergeKeySplit[columnNr].contains("ALL") && candidateKeySplit[columnNr].contains("ALL")) {
            result.setLoss(0.0);
            result.setNewKey("ALL");
        } else if ((mergeKeySplit[columnNr].contains("ALL") && !candidateKeySplit[columnNr].contains("ALL")) ||
                (!mergeKeySplit[columnNr].contains("ALL") && candidateKeySplit[columnNr].contains("ALL"))) {
            result.setLoss(Double.valueOf((mergeGroup.getRows().size()) + (candidateGroup.getRows().size())));
            result.setNewKey("ALL");
        } else {
            String mergeKey = mergeKeySplit[columnNr];
            String candidateKey = candidateKeySplit[columnNr];
            while (mergeKey.length() != candidateKey.length()) {
                if (mergeKey.length() < candidateKey.length()) {
                    StringBuilder sb = new StringBuilder(mergeKey);
                    sb.insert(0, "0");
                    mergeKey = sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder(candidateKey);
                    sb.insert(0, "0");
                    candidateKey = sb.toString();
                }
            }

            while (!mergeKey.equals(candidateKey)) {
                char[] mergeKeyChars = mergeKey.toCharArray();
                char[] candidateKeyChars = candidateKey.toCharArray();
                for (Integer i = mergeKeyChars.length - 1; i >= 0; i--) {
                    Boolean replaced = false;
                    if (mergeKeyChars[i] != '*' && mergeKeyChars[i] != '.') {
                        mergeKeyChars[i] = '*';
                        replaced = true;
                    }
                    if (candidateKeyChars[i] != '*' && candidateKeyChars[i] != '.') {
                        candidateKeyChars[i] = '*';
                        replaced = true;
                    }

                    if (replaced) {
                        break;
                    }
                }
                mergeKey = String.valueOf(mergeKeyChars);
                candidateKey = String.valueOf(candidateKeyChars);
            }

            char[] mergeKeyArr = mergeKey.toCharArray();
            Integer numbers = 0;
            Integer supressed = 0;
            for (Integer i = 0; i < mergeKeyArr.length; i++) {
                numbers++;
                if (mergeKeyArr[i] == '*') {
                    supressed++;
                }
            }

            result.setLoss(Double.valueOf((supressed / numbers) * mergeGroup.getRows().size() +
                    (supressed / numbers) * candidateGroup.getRows().size()));
            result.setNewKey(mergeKey);
        }
        return result;
    }

    private MergeColumn calculateLossOfCustomHierarchyColumn(Column column, Group mergeGroup, Group candidateGroup, Integer columnNr) {
        MergeColumn result = new MergeColumn();
        result.setLoss(0.0);
        result.setNewKey("");
        String[] mergeKeySplit = mergeGroup.getKey().split(";");
        String[] candidateKeySplit = candidateGroup.getKey().split(";");
        if (mergeKeySplit[columnNr].contains("NULL") && candidateKeySplit[columnNr].contains("NULL")) {
            result.setLoss(0.0);
            result.setNewKey("NULL");
        } else if ((mergeKeySplit[columnNr].contains("NULL") && !candidateKeySplit[columnNr].contains("NULL")) ||
                (!mergeKeySplit[columnNr].contains("NULL") && candidateKeySplit[columnNr].contains("NULL"))) {
            result.setLoss(Double.valueOf((mergeGroup.getRows().size()) + (candidateGroup.getRows().size())));
            result.setNewKey("ALL");
        } else if (mergeKeySplit[columnNr].contains("ALL") && candidateKeySplit[columnNr].contains("ALL")) {
            result.setLoss(0.0);
            result.setNewKey("ALL");
        } else if ((mergeKeySplit[columnNr].contains("ALL") && !candidateKeySplit[columnNr].contains("ALL")) ||
                (!mergeKeySplit[columnNr].contains("ALL") && candidateKeySplit[columnNr].contains("ALL"))) {
            result.setLoss(Double.valueOf((mergeGroup.getRows().size()) + (candidateGroup.getRows().size())));
            result.setNewKey("ALL");
        } else {
            String mergeKey = mergeKeySplit[columnNr];
            String candidateKey = candidateKeySplit[columnNr];

            AnonymizationHierarchyNode mergeNode = new AnonymizationHierarchyNode();
            mergeNode.setValue(mergeKey);
            AnonymizationHierarchyNode candidateNode = new AnonymizationHierarchyNode();
            candidateNode.setValue(candidateKey);
            AnonymizationHierarchyNode generalizationForStep = findGeneralizationForStep(column.getPosition(), mergeNode, candidateNode, column.getHierarchyRootNode());

            if (generalizationForStep != null) {
                result.setLoss(Double.valueOf((generalizationForStep.getDescendants() / column.getHierarchyRootNode().getDescendants()) * mergeGroup.getRows().size()) +
                        Double.valueOf((generalizationForStep.getDescendants() / column.getHierarchyRootNode().getDescendants()) * mergeGroup.getRows().size()));
                result.setNewKey(generalizationForStep.getValue());
            } else {
                result.setLoss((1.1 * mergeGroup.getRows().size()) + (1.1 * mergeGroup.getRows().size()));
                result.setNewKey("ALL");
            }
        }
        return result;
    }

    private Interval calculateNewInterval(String mergeKey, String candidateKey) {
        Interval interval = new Interval();
        String[] newInter = new String[2];
        String[] mergeVals = mergeKey.split(":");
        String[] candVals = candidateKey.split(":");

        newInter[0] = (Double.parseDouble(mergeVals[0]) < Double.parseDouble(candVals[0])) ? mergeVals[0] : candVals[0];
        newInter[1] = (Double.parseDouble(mergeVals[1]) > Double.parseDouble(candVals[1])) ? mergeVals[1] : candVals[1];

        interval.setValue(Math.abs(Double.parseDouble(newInter[1]) - Double.parseDouble(newInter[0])));
        interval.setText(newInter[0] + ":" + newInter[1]);

        return interval;
    }


    private Interval calculateInterval(String key) {
        Interval interval = new Interval();

        String[] vals = key.split(":");
        Double val = Math.abs(Double.parseDouble(vals[1]) - Double.parseDouble(vals[0]));

        interval.setValue(val);
        interval.setText(key);

        return interval;
    }


    private AnonymizationHierarchyNode findGeneralizationForStep(Integer columnPosition, AnonymizationHierarchyNode search1, AnonymizationHierarchyNode search2, AnonymizationHierarchyNode root) {
        List<AnonymizationHierarchyNode> path1 = getPathFromCacheOrCalculate(columnPosition, search1, root);
        List<AnonymizationHierarchyNode> path2 = getPathFromCacheOrCalculate(columnPosition, search2, root);

        if (path1 != null && path2 != null) {
            List<AnonymizationHierarchyNode> common = new LinkedList<>(path1);
            common.retainAll(path2);

            AnonymizationHierarchyNode best = null;
            for (AnonymizationHierarchyNode anonymizationHierarchyNode : common) {
                if (best != null) {
                    if (best.getDescendants() > anonymizationHierarchyNode.getDescendants()) {
                        best = anonymizationHierarchyNode;
                    }
                } else {
                    best = anonymizationHierarchyNode;
                }
            }
            return best;
        } else {
            return null;
        }

    }

    private List<AnonymizationHierarchyNode> getPathFromCacheOrCalculate(Integer columnPosition, AnonymizationHierarchyNode search, AnonymizationHierarchyNode root) {
        List<AnonymizationHierarchyNode> path;
        if(pathCache.containsKey(columnPosition) && pathCache.get(columnPosition).containsKey(search.getValue())){
            path = pathCache.get(columnPosition).get(search.getValue());
        }else {
            path = findPath(search, root);
            if(pathCache.containsKey(columnPosition)){
                pathCache.get(columnPosition).put(search.getValue(), path);
            }else {
                Map<String, List<AnonymizationHierarchyNode>> map = new HashMap<>();
                map.put(search.getValue(), path);
                pathCache.put(columnPosition, map);
            }
        }
        return path;
    }

    private List<AnonymizationHierarchyNode> findPath(AnonymizationHierarchyNode search, AnonymizationHierarchyNode node) {
        if (node.getValue().equals(search.getValue())) {
            LinkedList<AnonymizationHierarchyNode> node2s = new LinkedList<>();
            node2s.add(node);
            return node2s;
        } else {
            if (node.getChildren() == null || node.getChildren().size() == 0) {
                return null;
            } else {
                for (AnonymizationHierarchyNode child : node.getChildren()) {
                    List<AnonymizationHierarchyNode> list = findPath(search, child);
                    if (list != null) {
                        list.add(node);
                        return list;
                    }
                }
            }
        }
        return null;
    }
}
