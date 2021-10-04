package com.anonymizerweb.anonymizerweb.commands;

public class MatchColumnCommand implements Comparable<MatchColumnCommand>{
    private Integer definitionColumnPosition;

    private String definitionColumnName;

    private Integer collectionColumnPosition;

    public Integer getDefinitionColumnPosition() {
        return definitionColumnPosition;
    }

    public void setDefinitionColumnPosition(Integer definitionColumnPosition) {
        this.definitionColumnPosition = definitionColumnPosition;
    }

    public String getDefinitionColumnName() {
        return definitionColumnName;
    }

    public void setDefinitionColumnName(String definitionColumnName) {
        this.definitionColumnName = definitionColumnName;
    }

    public Integer getCollectionColumnPosition() {
        return collectionColumnPosition;
    }

    public void setCollectionColumnPosition(Integer collectionColumnPosition) {
        this.collectionColumnPosition = collectionColumnPosition;
    }

    @Override
    public int compareTo(MatchColumnCommand o) {
        return definitionColumnPosition.compareTo(o.definitionColumnPosition);
    }
}
