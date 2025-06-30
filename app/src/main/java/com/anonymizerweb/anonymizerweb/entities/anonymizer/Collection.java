package com.anonymizerweb.anonymizerweb.entities.anonymizer;

import com.anonymizerweb.anonymizerweb.enums.CollectionUsageTyp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fileName;

    private String heading;

    @Column(name = "targetk")
    private Integer targetK;

    @Enumerated(EnumType.STRING)
    private CollectionUsageTyp usageTyp;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "collectionId")
    @OrderBy("position ASC")
    private Set<CollectionColumn> columns;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "collection_raw_data", joinColumns = @JoinColumn(name = "collection_id"))
    private List<String> rawData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CollectionUsageTyp getUsageTyp() {
        return usageTyp;
    }

    public void setUsageTyp(CollectionUsageTyp usageTyp) {
        this.usageTyp = usageTyp;
    }

    public Integer getTargetK() {
        return targetK;
    }

    public void setTargetK(Integer targetK) {
        this.targetK = targetK;
    }

    public Set<CollectionColumn> getColumns() {
        return columns;
    }

    public void setColumns(Set<CollectionColumn> columns) {
        this.columns = columns;
    }

    public List<String> getRawData() {
        return rawData;
    }

    public void setRawData(List<String> rawData) {
        this.rawData = rawData;
    }
}
