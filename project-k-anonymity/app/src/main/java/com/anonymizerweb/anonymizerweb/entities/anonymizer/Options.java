package com.anonymizerweb.anonymizerweb.entities.anonymizer;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Options {
    @Id
    private String optKey;

    private String optValue;

    public String getOptKey() {
        return optKey;
    }

    public void setOptKey(String key) {
        this.optKey = key;
    }

    public String getOptValue() {
        return optValue;
    }

    public void setOptValue(String value) {
        this.optValue = value;
    }
}
