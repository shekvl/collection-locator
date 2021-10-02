package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.entities.Anonymization;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    AnonymizationService anonymizationService;

    @GetMapping("/index")
    public String index(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        List<Anonymization> all = anonymizationService.findAll();

        model.addAttribute("all", all);
        return "index";
    }
}
