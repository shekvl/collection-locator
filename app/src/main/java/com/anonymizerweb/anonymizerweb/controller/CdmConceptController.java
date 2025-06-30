package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.entities.cdm.CdmConcept;
import com.anonymizerweb.anonymizerweb.repositories.cdm.CdmConceptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CdmConceptController {
    CdmConceptRepository cdmConceptRepository;

    @Autowired
    public CdmConceptController (CdmConceptRepository cdmConceptRepository){
        this.cdmConceptRepository = cdmConceptRepository;
    }

    @GetMapping("/concepts")
    public String getCdmConceptByName (@RequestParam String name){
        CdmConcept cdmConcept = cdmConceptRepository.findCdmConceptByName(name);
        return cdmConcept.toString();
    }
}
