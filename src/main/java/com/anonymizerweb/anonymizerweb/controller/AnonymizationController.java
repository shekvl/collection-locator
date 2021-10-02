package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.EditCommand;
import com.anonymizerweb.anonymizerweb.commands.NewCommand;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class AnonymizationController {
    Logger logger = LoggerFactory.getLogger(AnonymizationController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @GetMapping("/anonymization/{id}")
    public String anonymization(@PathVariable String id, Model model) {
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        model.addAttribute("anonymization", anonymization);
        return "anonymization";
    }

    @GetMapping("/anonymization/{id}/delete")
    public String delete(@PathVariable String id, Model model) {
        anonymizationService.delete(Long.valueOf(id));
        return "redirect:/index";
    }

    @GetMapping("/anonymization/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        EditCommand command = anonymizationService.getEditCommand(Long.valueOf(id));
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        model.addAttribute("command", command);
        model.addAttribute("anonymization", anonymization);
        return "edit";
    }

    @PostMapping("/anonymization/{id}/edit")
    public String edit_save(@PathVariable String id, @ModelAttribute EditCommand command, Model model) {
        Anonymization anonymization = anonymizationService.updateAnonymization(command, Long.valueOf(id));
        return "redirect:/anonymization/" + anonymization.getId();
    }
}
