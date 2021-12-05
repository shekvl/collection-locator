package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.services.ActionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/actions")
public class ActionsController {
    Logger logger = LoggerFactory.getLogger(ActionsController.class);

    @Autowired
    ActionsService actionsService;

    @GetMapping(value = "")
    public String index() {
        return "actions/index";
    }

    @GetMapping(value = "/importDefinitions")
    public String importDefinition() {
        actionsService.importDefinitions();
        return "redirect:/actions";
    }

    @GetMapping(value = "/createAnonymizations")
    public String createAnonymizations() {
        actionsService.createAllAnonymizations();
        return "redirect:/actions";
    }

    @GetMapping(value = "/anonymizeAll")
    public String anonymizeAll() throws InterruptedException, ExecutionException {
        Future<String> stringFuture = actionsService.anonymizeAll();
        stringFuture.get();
        return "redirect:/actions";
    }

    @GetMapping(value = "/sendAnonymization")
    public String sendAnonymization() throws JAXBException, IOException {
        actionsService.sendAllAnonymizations();
        return "redirect:/actions";
    }
}
