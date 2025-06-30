package com.anonymizerweb.anonymizerweb.entities.anonymizer;

import com.anonymizerweb.anonymizerweb.enums.AnonymizationTyp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Cacheable(false)
public class Anonymization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileName;

    private String heading;

    private String definitionUid;

    private String collectionUid;

    @Enumerated(EnumType.STRING)
    private AnonymizationTyp anonymizationTyp;

    @Column(name = "targetk")
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

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "anonymization_id")
    private Set<AnonymizationColumn> columns;

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

    public String getDefinitionUid() {
        return definitionUid;
    }

    public void setDefinitionUid(String definitionUid) {
        this.definitionUid = definitionUid;
    }

    public String getCollectionUid() {
        return collectionUid;
    }

    public void setCollectionUid(String collectionUid) {
        this.collectionUid = collectionUid;
    }

    public AnonymizationTyp getAnonymizationTyp() {
        return anonymizationTyp;
    }

    public void setAnonymizationTyp(AnonymizationTyp anonymizationTyp) {
        this.anonymizationTyp = anonymizationTyp;
    }

    public Set<AnonymizationColumn> getColumns() {
        return columns;
    }

    public void setColumns(Set<AnonymizationColumn> columnProperties) {
        this.columns = columnProperties;
    }
}


