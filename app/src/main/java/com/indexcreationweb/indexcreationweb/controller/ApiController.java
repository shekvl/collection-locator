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
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiController {
    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    ApiService apiService;

    @GetMapping(value = "/definitions/get/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ApiDefinitionListDto definitionGetXml(@PathVariable String id) {
        return apiService.getApiDataFromId(id);
    }

    @GetMapping(value = "/definitions/get/{id}/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiDefinitionListDto definitionGetJson(@PathVariable String id) {
        return apiService.getApiDataFromId(id);
    }

    @GetMapping(value = "/getXmlSchema",  produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlSchema() throws JAXBException, IOException {
        return apiService.getAnoXmlSchema();
    }
}
