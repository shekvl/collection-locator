package com.anonymizerweb.anonymizerweb.commands;

import org.springframework.web.multipart.MultipartFile;

public class EditCommand {
    Integer targetk;
    Boolean fast;
    Integer batch;
    String name;

    public Integer getTargetk() {
        return targetk;
    }

    public void setTargetk(Integer targetk) {
        this.targetk = targetk;
    }

    public Boolean getFast() {
        return fast;
    }

    public void setFast(Boolean fast) {
        this.fast = fast;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
