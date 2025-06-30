package com.anonymizerweb.anonymizerweb.commands;

import com.anonymizerweb.anonymizerweb.enums.CollectionUsageTyp;
import org.springframework.web.multipart.MultipartFile;

public class NewCollectionCommand {
    MultipartFile file;

    String name;

    private String usageTyp;

    private Integer targetK;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsageTyp() {
        return usageTyp;
    }

    public void setUsageTyp(String usageTyp) {
        this.usageTyp = usageTyp;
    }

    public Integer getTargetK() {
        return targetK;
    }

    public void setTargetK(Integer targetK) {
        this.targetK = targetK;
    }
}
