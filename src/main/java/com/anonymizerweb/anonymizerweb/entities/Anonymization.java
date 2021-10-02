package com.anonymizerweb.anonymizerweb.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Anonymization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileName;

    private String heading;

    private Integer targetK;

    private Boolean isFast;

    private Integer batch;

    private Double loss;

    private String duration;

    private Boolean isRunning;

    private Boolean isAnonymized;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> rawData;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> outputData;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL},  orphanRemoval=true)
    private Set<ColumnProperty> columnProperties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTargetK() {
        return targetK;
    }

    public void setTargetK(Integer targetK) {
        this.targetK = targetK;
    }

    public Boolean getFast() {
        return isFast;
    }

    public void setFast(Boolean fast) {
        isFast = fast;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Double getLoss() {
        return loss;
    }

    public void setLoss(Double loss) {
        this.loss = loss;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Boolean getRunning() {
        return isRunning;
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    public Boolean getAnonymized() {
        return isAnonymized;
    }

    public void setAnonymized(Boolean anonymized) {
        isAnonymized = anonymized;
    }

    public List<String> getRawData() {
        return rawData;
    }

    public void setRawData(List<String> rawData) {
        this.rawData = rawData;
    }

    public List<String> getOutputData() {
        return outputData;
    }

    public void setOutputData(List<String> outputData) {
        this.outputData = outputData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Set<ColumnProperty> getColumnProperties() {
        return columnProperties;
    }

    public void setColumnProperties(Set<ColumnProperty> columnProperties) {
        this.columnProperties = columnProperties;
    }
}
