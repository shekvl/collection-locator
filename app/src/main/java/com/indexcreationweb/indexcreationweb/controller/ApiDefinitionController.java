package com.indexcreationweb.indexcreationweb.controller;

import com.indexcreationweb.indexcreationweb.dto.ApiDefinitionListDto;
import com.indexcreationweb.indexcreationweb.services.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;

@RestController
@RequestMapping("/api")
public class ApiDefinitionController {
    Logger logger = LoggerFactory.getLogger(ApiDefinitionController.class);

    @Autowired
    ApiService apiService;

    @GetMapping(value = "/definitions/get/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ApiDefinitionListDto indexAll(@PathVariable String id) throws JAXBException {
        return apiService.getDownloadDataXmlFromId(id);
    }
}
