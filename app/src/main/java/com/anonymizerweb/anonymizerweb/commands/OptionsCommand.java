package com.anonymizerweb.anonymizerweb.commands;

public class OptionsCommand {

    private String biobankId;

    private String indexGenUrl;

    private String collectUrl;

    public String getBiobankId() {
        return biobankId;
    }

    public void setBiobankId(String biobankId) {
        this.biobankId = biobankId;
    }

    public String getIndexGenUrl() {
        return indexGenUrl;
    }

    public void setIndexGenUrl(String indexGenUrl) {
        this.indexGenUrl = indexGenUrl;
    }

    public String getCollectUrl() {
        return collectUrl;
    }

    public void setCollectUrl(String collectUrl) {
        this.collectUrl = collectUrl;
    }
}
