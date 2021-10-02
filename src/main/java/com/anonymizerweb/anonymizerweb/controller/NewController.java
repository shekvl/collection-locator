package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.NewCommand;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class NewController {
    Logger logger = LoggerFactory.getLogger(NewController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @GetMapping("/new")
    public String newGet(Model model) {
        NewCommand command = new NewCommand();
        model.addAttribute("command", command);
        return "new";
    }

    @PostMapping("/new")
    public String newPost(@ModelAttribute NewCommand command, Model model) throws IOException {
        Anonymization save = anonymizationService.save(command);
        model.addAttribute("command", command);
        return "redirect:/anonymization/" + save.getId();
    }
}
