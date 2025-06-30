package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.entities.anonymizer.Anonymization;
import com.anonymizerweb.anonymizerweb.services.AnonymizationService;
import com.anonymizerweb.anonymizerweb.services.AnonymizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/anonymize")
public class AnonymizeController {
    Logger logger = LoggerFactory.getLogger(AnonymizeController.class);

    @Autowired
    AnonymizationService anonymizationService;

    @Autowired
    AnonymizeService anonymizeService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void anonymization(@PathVariable String id, Model model) throws InterruptedException {
        logger.info("anonymization controller");
        Anonymization anonymization = anonymizationService.findbyId(Long.valueOf(id));
        try {
            anonymizeService.anonymize(anonymization.getId());
        }catch (Exception e){
            logger.info(e.getMessage());
        }
//        return "redirect:/anonymizations";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id, Model model) {
        String filename = "out.csv";
        InputStreamResource file = anonymizationService.outputAnonymizedData(Long.valueOf(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
