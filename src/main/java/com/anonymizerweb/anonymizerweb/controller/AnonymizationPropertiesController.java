package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.ColumnPropertiesCommand;
import com.anonymizerweb.anonymizerweb.commands.ColumnPropertiesCommandProperties;
import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.entities.ColumnProperty;
import com.anonymizerweb.anonymizerweb.enums.ColumnDataTyp;
import com.anonymizerweb.anonymizerweb.enums.ColumnTyp;
import com.anonymizerweb.anonymizerweb.services.AnonymizationPropertiesService;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
public class AnonymizationPropertiesController {
    Logger logger = LoggerFactory.getLogger(AnonymizationPropertiesController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    AnonymizationPropertiesService anonymizationPropertiesService;

    @GetMapping("/anonymization/{id}/properties")
    public String propertiesGet(@PathVariable String id, Model model) {
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        ColumnPropertiesCommand command = anonymizationPropertiesService.anonymizationToPropertiesCommand(anonymization);

        List<ColumnTyp> columnTyps = Arrays.asList(ColumnTyp.values());
        List<ColumnDataTyp> columnDataTyps = Arrays.asList(ColumnDataTyp.values());

        model.addAttribute("anonymization", anonymization);
        model.addAttribute("command", command);
        model.addAttribute("columnTyps", columnTyps);
        model.addAttribute("columnDataTyps", columnDataTyps);

        return "anonymizationproperties";
    }

    @PostMapping("/anonymization/{id}/properties")
    public String propertiesPost(@PathVariable String id, @ModelAttribute ColumnPropertiesCommand command, Model model) throws IOException {
        Anonymization anonymization = anonymizationService.saveProperties(Long.valueOf(id), command);
        return "redirect:/anonymization/" + anonymization.getId();
    }
}
