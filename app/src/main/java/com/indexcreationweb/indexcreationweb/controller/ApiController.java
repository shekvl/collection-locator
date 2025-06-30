package com.indexcreationweb.indexcreationweb.controller;

import com.indexcreationweb.indexcreationweb.commands.DefinitionCommand;
import com.indexcreationweb.indexcreationweb.dto.ApiDefinitionListDto;
import com.indexcreationweb.indexcreationweb.dto.CdmConceptAutocompleteDto;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDto;
import com.indexcreationweb.indexcreationweb.entities.indexcreator.Definition;
import com.indexcreationweb.indexcreationweb.services.ApiService;
import com.indexcreationweb.indexcreationweb.services.CdmConceptService;
import com.indexcreationweb.indexcreationweb.services.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    ApiService apiService;
    @Autowired
    DefinitionService definitionService;
    @Autowired
    CdmConceptService cdmConceptService;

    @GetMapping(value = "/definitions/get/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ApiDefinitionListDto definitionGetXml(@PathVariable String id) {
        return apiService.getApiDataFromId(id);
    }

    @GetMapping(value = "/definitions/get/{id}/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiDefinitionListDto definitionGetJson(@PathVariable String id) {
        return apiService.getApiDataFromId(id);
    }

    @DeleteMapping(value = "/definitions/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDefinition(@PathVariable Long id) {
        apiService.deleteDefinitionById(id);
    }

    @PutMapping("/definitions/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveEdited(@PathVariable String id, @RequestBody DefinitionCommand command, Model model) throws IOException {
        Definition save = definitionService.edit(command, id);
        model.addAttribute("command", command);
    }

    @PostMapping("/definitions")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveNew(@RequestBody DefinitionCommand command, Model model) throws IOException {
        Definition save = definitionService.save(command);
        model.addAttribute("command", command);
    }

    @GetMapping(value = "/getXmlSchema",  produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlSchema() throws JAXBException, IOException {
        return apiService.getAnoXmlSchema();
    }

    @GetMapping(value = "/definitions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DefinitionDto> getAllDefinitions() {
        List<Definition> definitions = definitionService.findAll();
        List<DefinitionDto> dtoList = definitionService.getDtoListFromDefinitionList(definitions);
        return dtoList;
    }

    @GetMapping("/cdmconceptcautocomplete")
    public List<CdmConceptAutocompleteDto> index(@RequestParam(value = "q", required = false) String query) {

        List<CdmConceptAutocompleteDto> forAutocomplete = cdmConceptService.findForAutocomplete(query);
        return forAutocomplete;
    }


}
