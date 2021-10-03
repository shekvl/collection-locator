package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import com.anonymizerweb.anonymizerweb.services.CollectionService;
import com.anonymizerweb.anonymizerweb.services.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    DefinitionService definitionService;

    @GetMapping("/index")
    public String index(Model model) {
        Integer numberOfAnonymizations = anonymizationService.findNumberOfAnonymizations();
        Integer numberOfCollections = collectionService.findNumberOfCollections();
        Integer numberOfDefinitions = definitionService.findNumberOfDefinitions();
        model.addAttribute("anonymizationsNr", numberOfAnonymizations);
        model.addAttribute("collectionsNr", numberOfCollections);
        model.addAttribute("definitionsNr", numberOfDefinitions);
        return "index";
    }
}
