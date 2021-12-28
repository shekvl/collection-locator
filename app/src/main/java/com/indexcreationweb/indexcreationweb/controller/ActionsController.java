package com.indexcreationweb.indexcreationweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/actions")
public class ActionsController {
    Logger logger = LoggerFactory.getLogger(ActionsController.class);

    @GetMapping(value = "")
    public String index() {
        return "actions/index";
    }
}
