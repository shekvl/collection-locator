package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.CombineCommand;
import com.anonymizerweb.anonymizerweb.commands.MatchCommand;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Collection;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.CollectionColumn;
import com.anonymizerweb.anonymizerweb.entities.anonymizer.Definition;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import com.anonymizerweb.anonymizerweb.services.CollectionService;
import com.anonymizerweb.anonymizerweb.services.CombineService;
import com.anonymizerweb.anonymizerweb.services.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/combine")
public class CombineController {
    Logger logger = LoggerFactory.getLogger(CombineController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    DefinitionService definitionService;

    @Autowired
    CombineService combineService;

    @GetMapping("")
    public String indexGet(Model model) {
        List<Definition> definitions = definitionService.findAll();
        List<Collection> collections = collectionService.findAll();
        CombineCommand command = new CombineCommand();
        model.addAttribute("definitions", definitions);
        model.addAttribute("collections", collections);
        model.addAttribute("command", command);
        return "combine/combine";
    }

    @PostMapping("")
    public String indexPost(@ModelAttribute CombineCommand command, Model model) {
        MatchCommand matchCommandFromCombine = combineService.getMatchCommandFromCombine(command);
        Definition definition = definitionService.findbyId(command.getDefinitionId());
        Collection collection = collectionService.findbyId(command.getCollectionId());
        List<CollectionColumn> collectionColumns = new LinkedList<>(collection.getColumns());
        Collections.sort(collectionColumns);
        model.addAttribute("definition", definition);
        model.addAttribute("collection", collection);
        model.addAttribute("collectionColumns", collectionColumns);
        model.addAttribute("command", matchCommandFromCombine);
        return "combine/match";
    }

    @PostMapping("/match")
    public String matchPost(@ModelAttribute MatchCommand command, Model model) {
        Anonymization anonymization = anonymizationService.saveFromMatch(command);
        return "redirect:/anonymizations/" + anonymization.getId();
    }
}
