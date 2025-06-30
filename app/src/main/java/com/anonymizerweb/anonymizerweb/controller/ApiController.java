package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.dto.ApiAnonymizationDtoList;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Anonymization;
import com.anonymizerweb.anonymizerweb.services.ActionsService;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class ApiController {
    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    ActionsService actionsService;

    @Autowired
    AnonymizationService anonymizationService;

    @GetMapping(value = "/sendAllData/xml",  produces = MediaType.APPLICATION_XML_VALUE)
    public ApiAnonymizationDtoList sendAllDataXml() throws InterruptedException, JAXBException, IOException, ExecutionException {
        actionsService.importDefinitions();
        actionsService.createAllAnonymizations();
        Future<List<Anonymization>> future = actionsService.anonymizeAll();
        return actionsService.sendAllAnonymizationsXml(future.get());
    }

    @GetMapping(value = "/sendAllData/json",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiAnonymizationDtoList sendAllDataJson() throws InterruptedException, ExecutionException {
        actionsService.importDefinitions();
        actionsService.createAllAnonymizations();
        Future<List<Anonymization>> future = actionsService.anonymizeAll();
        return actionsService.sendAllAnonymizationsJson(future.get());
    }

    @GetMapping(value = "/sendData/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiAnonymizationDtoList sendData(@PathVariable String id) throws InterruptedException, JAXBException, IOException, ExecutionException {
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        return actionsService.sendAllAnonymizationsJson(Collections.singletonList(anonymization));
    }

    @GetMapping(value = "/getXmlSchema",  produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlSchema() throws JAXBException, IOException {
        return actionsService.getAnoXmlSchema();
    }
}
