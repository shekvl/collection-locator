package com.anonymizerweb.anonymizerweb.commands;

import org.springframework.web.multipart.MultipartFile;

public class NewDefinitionCommand {
    MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
