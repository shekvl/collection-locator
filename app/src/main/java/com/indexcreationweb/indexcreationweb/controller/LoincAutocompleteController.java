package com.indexcreationweb.indexcreationweb.controller;

import com.indexcreationweb.indexcreationweb.dto.LoincAutocompleteDto;
import com.indexcreationweb.indexcreationweb.services.LoincService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoincAutocompleteController {
    Logger logger = LoggerFactory.getLogger(LoincAutocompleteController.class);

    @Autowired
    LoincService loincService;

    @GetMapping("/loincautocomplete")
    public List<LoincAutocompleteDto> index(@RequestParam(value = "q", required = false) String query) {

        return loincService.findForAutocomplete(query);
    }
}
