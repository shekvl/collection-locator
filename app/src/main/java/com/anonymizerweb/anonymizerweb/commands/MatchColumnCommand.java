package com.anonymizerweb.anonymizerweb.commands;

public class MatchColumnCommand implements Comparable<MatchColumnCommand>{
    private String definitionColumnName;

    private String collectionColumnName;

    private Integer definitionColumnPosition;

    private Integer collectionColumnPosition;

    private String definitionColumnCode;

    private String collectionColumnCode;

    private Boolean isMatched;

    public String getDefinitionColumnName() {
        return definitionColumnName;
    }

    public void setDefinitionColumnName(String definitionColumnName) {
        this.definitionColumnName = definitionColumnName;
    }

    public String getCollectionColumnName() {
        return collectionColumnName;
    }

    public void setCollectionColumnName(String collectionColumnName) {
        this.collectionColumnName = collectionColumnName;
    }

    public Integer getDefinitionColumnPosition() {
        return definitionColumnPosition;
    }

    public void setDefinitionColumnPosition(Integer definitionColumnPosition) {
        this.definitionColumnPosition = definitionColumnPosition;
    }

    public Integer getCollectionColumnPosition() {
        return collectionColumnPosition;
    }

    public void setCollectionColumnPosition(Integer collectionColumnPosition) {
        this.collectionColumnPosition = collectionColumnPosition;
    }

    public String getDefinitionColumnCode() {
        return definitionColumnCode;
    }

    public void setDefinitionColumnCode(String definitionColumnCode) {
        this.definitionColumnCode = definitionColumnCode;
    }

    public String getCollectionColumnCode() {
        return collectionColumnCode;
    }

    public void setCollectionColumnCode(String collectionColumnCode) {
        this.collectionColumnCode = collectionColumnCode;
    }

    public Boolean getMatched() {
        return isMatched;
    }

    public void setMatched(Boolean matched) {
        isMatched = matched;
    }

    @Override
    public int compareTo(MatchColumnCommand o) {
        return definitionColumnPosition.compareTo(o.definitionColumnPosition);
    }
}
