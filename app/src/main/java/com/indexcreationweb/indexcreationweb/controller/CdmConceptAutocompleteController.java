package com.indexcreationweb.indexcreationweb.controller;

import com.indexcreationweb.indexcreationweb.dto.CdmConceptAutocompleteDto;
import com.indexcreationweb.indexcreationweb.services.CdmConceptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CdmConceptAutocompleteController {

    Logger logger = LoggerFactory.getLogger(CdmConceptAutocompleteController.class);

    @Autowired
    CdmConceptService cdmConceptService;

    @GetMapping("/cdmconceptcautocomplete")
    public List<CdmConceptAutocompleteDto> index(@RequestParam(value = "q", required = false) String query) {

        List<CdmConceptAutocompleteDto> forAutocomplete = cdmConceptService.findForAutocomplete(query);
        return forAutocomplete;
    }

}
