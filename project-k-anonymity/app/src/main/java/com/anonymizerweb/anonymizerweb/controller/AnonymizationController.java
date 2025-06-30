package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.AnonymizationColumnsCommand;
import com.anonymizerweb.anonymizerweb.commands.CombineCommand;
import com.anonymizerweb.anonymizerweb.commands.EditAnonymizationCommand;
import com.anonymizerweb.anonymizerweb.commands.NewAnonymizationCommand;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.entities.Definition;
import com.anonymizerweb.anonymizerweb.entities.Loinc;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.repositories.LoincRepository;
import com.anonymizerweb.anonymizerweb.services.AnonymizationColumnsService;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import com.anonymizerweb.anonymizerweb.services.CollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/anonymizations")
public class AnonymizationController {
    Logger logger = LoggerFactory.getLogger(AnonymizationController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    AnonymizationColumnsService anonymizationColumnsService;

    @Autowired
    CollectionService collectionService;

    @GetMapping("")
    public String index(Model model) {
        List<Anonymization> all = anonymizationService.findAll();
        model.addAttribute("all", all);
        return "anonymizations/index";
    }

    @GetMapping("/{id}")
    public String anonymization(@PathVariable String id, Model model) {
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        model.addAttribute("anonymization", anonymization);
        return "anonymizations/show";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable String id, Model model) {
        anonymizationService.delete(Long.valueOf(id));
        return "redirect:/anonymizations";
    }

    @GetMapping("/new")
    public String newGet(Model model) {
        List<Collection> collections = collectionService.findAll();
        NewAnonymizationCommand command = new NewAnonymizationCommand();
        model.addAttribute("collections", collections);
        model.addAttribute("command", command);
        return "anonymizations/new";
    }

    @PostMapping("/new")
    public String newPost(@ModelAttribute NewAnonymizationCommand command, Model model) {
        Anonymization save = anonymizationService.save(command);
        model.addAttribute("command", command);
        return "redirect:/anonymizations/" + save.getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        EditAnonymizationCommand command = anonymizationService.getEditCommand(Long.valueOf(id));
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        model.addAttribute("command", command);
        model.addAttribute("anonymization", anonymization);
        return "anonymizations/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit_save(@PathVariable String id, @ModelAttribute EditAnonymizationCommand command, Model model) {
        Anonymization anonymization = anonymizationService.updateAnonymization(command, Long.valueOf(id));
        return "redirect:/anonymizations/" + anonymization.getId();
    }

    @GetMapping("/{id}/columns")
    public String columnsGet(@PathVariable String id, Model model) {
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        AnonymizationColumnsCommand command = anonymizationColumnsService.anonymizationToColumnsCommand(anonymization);

        List<ColumnTyp> columnTyps = Arrays.asList(ColumnTyp.values());
        List<ColumnDataTyp> columnDataTyps = Arrays.asList(ColumnDataTyp.values());

        model.addAttribute("anonymization", anonymization);
        model.addAttribute("command", command);
        model.addAttribute("columnTyps", columnTyps);
        model.addAttribute("columnDataTyps", columnDataTyps);

        return "anonymizations/anonymizationColumns";
    }

    @PostMapping("/{id}/columns")
    public String columnsPost(@PathVariable String id, @ModelAttribute AnonymizationColumnsCommand command, Model model) throws IOException {
        Anonymization anonymization = anonymizationService.saveColumns(Long.valueOf(id), command);
        return "redirect:/anonymizations/" + anonymization.getId();
    }
}
