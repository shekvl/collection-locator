package com.anonymizerweb.anonymizerweb.structures;

public class MergeStep {
    private Double loss;
    private String newKey;

    public MergeStep() {
    }

    public Double getLoss() {
        return loss;
    }

    public void setLoss(Double loss) {
        this.loss = loss;
    }

    public String getNewKey() {
        return newKey;
    }

    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }
}
