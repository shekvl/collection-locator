package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoList;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.services.ActionsService;
import com.anonymizerweb.anonymizerweb.services.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class ApiController {
    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    ActionsService actionsService;

    @GetMapping(value = "/sendAllData",  produces = MediaType.APPLICATION_XML_VALUE)
    public ApiAnonymizationDtoList sendAllData() throws InterruptedException, JAXBException, IOException, ExecutionException {
        actionsService.importDefinitions();
        actionsService.createAllAnonymizations();
        Future<List<Anonymization>> future = actionsService.anonymizeAll();
        return actionsService.sendAllAnonymizations(future.get());
    }

    @GetMapping(value = "/getXmlSchema",  produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlSchema() throws JAXBException, IOException {
        return actionsService.getAnoXmlSchema();
    }
}
