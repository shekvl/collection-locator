package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import com.anonymizerweb.anonymizerweb.services.CollectionService;
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

    @GetMapping("/index")
    public String index(Model model) {
        Integer numberOfAnonymizations = anonymizationService.findNumberOfAnonymizations();
        Integer numberOfCollections = collectionService.findNumberOfCollections();
        model.addAttribute("anonymizationsNr", numberOfAnonymizations);
        model.addAttribute("collectionsNr", numberOfCollections);
        return "index";
    }
}
