package com.anonymizerweb.anonymizerweb.commands;

import org.springframework.web.multipart.MultipartFile;

public class NewCollectionCommand {
    MultipartFile file;
    String name;

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
}
