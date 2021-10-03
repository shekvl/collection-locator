package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.ColumnPropertiesCommand;
import com.anonymizerweb.anonymizerweb.commands.EditAnonymizationCommand;
import com.anonymizerweb.anonymizerweb.commands.NewAnonymizationCommand;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.services.AnonymizationPropertiesService;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/anonymizations")
public class AnonymizationController {
    Logger logger = LoggerFactory.getLogger(AnonymizationController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    AnonymizationPropertiesService anonymizationPropertiesService;

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
        return "redirect:/anonymizations/index";
    }

    @GetMapping("/new")
    public String newGet(Model model) {
        NewAnonymizationCommand command = new NewAnonymizationCommand();
        model.addAttribute("command", command);
        return "anonymizations/new";
    }

    @PostMapping("/new")
    public String newPost(@ModelAttribute NewAnonymizationCommand command, Model model) throws IOException {
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

    @GetMapping("/{id}/properties")
    public String propertiesGet(@PathVariable String id, Model model) {
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        ColumnPropertiesCommand command = anonymizationPropertiesService.anonymizationToPropertiesCommand(anonymization);

        List<ColumnTyp> columnTyps = Arrays.asList(ColumnTyp.values());
        List<ColumnDataTyp> columnDataTyps = Arrays.asList(ColumnDataTyp.values());

        model.addAttribute("anonymization", anonymization);
        model.addAttribute("command", command);
        model.addAttribute("columnTyps", columnTyps);
        model.addAttribute("columnDataTyps", columnDataTyps);

        return "anonymizations/anonymizationproperties";
    }

    @PostMapping("/{id}/properties")
    public String propertiesPost(@PathVariable String id, @ModelAttribute ColumnPropertiesCommand command, Model model) throws IOException {
        Anonymization anonymization = anonymizationService.saveProperties(Long.valueOf(id), command);
        return "redirect:/anonymizations/" + anonymization.getId();
    }
}
