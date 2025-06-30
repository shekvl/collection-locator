package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.OptionsCommand;
import com.anonymizerweb.anonymizerweb.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/options")
public class OptionsController {
    Logger logger = LoggerFactory.getLogger(OptionsController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    DefinitionService definitionService;

    @Autowired
    CombineService combineService;

    @Autowired
    OptionsService optionsService;

    @GetMapping("")
    public String indexGet(Model model) {
        OptionsCommand command = optionsService.getOptionsCommand();
        model.addAttribute("command", command);
        return "options/edit";
    }

    @PostMapping("")
    public String indexPost(@ModelAttribute OptionsCommand command, Model model) {
        optionsService.saveFromOptionsCommand(command);
        return "redirect:/index";
    }
}
