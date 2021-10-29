package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.EditCollectionCommand;
import com.anonymizerweb.anonymizerweb.commands.NewCollectionCommand;
import com.anonymizerweb.anonymizerweb.commands.NewDefinitionCommand;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.Definition;
import com.anonymizerweb.anonymizerweb.services.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/definitions")
public class DefinitionController {
    Logger logger = LoggerFactory.getLogger(DefinitionController.class);

    @Autowired
    DefinitionService definitionService;

    @GetMapping("")
    public String indexAll(Model model) {
        List<Definition> definitions = definitionService.findAll();
        model.addAttribute("definitions", definitions);
        return "definitions/index";
    }

    @GetMapping("/{id}")
    public String index(@PathVariable String id, Model model) {
        Definition definition = definitionService.findbyId(Long.valueOf(id));
        model.addAttribute("definition", definition);
        return "definitions/show";
    }

    @GetMapping("/new")
    public String newGet(Model model) {
        NewDefinitionCommand command = new NewDefinitionCommand();
        model.addAttribute("command", command);
        return "definitions/new";
    }

    @PostMapping("/new")
    public String newPost(@ModelAttribute NewDefinitionCommand command, Model model) throws IOException {
        Definition save = definitionService.save(command);
        model.addAttribute("command", command);
        return "redirect:/definitions/" + save.getId();
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable String id, Model model) {
        definitionService.delete(Long.valueOf(id));
        return "redirect:/definitions";
    }
}
