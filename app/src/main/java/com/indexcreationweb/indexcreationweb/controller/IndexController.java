package com.indexcreationweb.indexcreationweb.controller;

import com.indexcreationweb.indexcreationweb.services.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @Autowired
    DefinitionService definitionService;

    @GetMapping("/index")
    public String index(Model model) {
        Integer numberOfDefinitions = definitionService.findNumberOfDefinitions();
        model.addAttribute("definitionsNr", numberOfDefinitions);
        return "index";
    }
}
