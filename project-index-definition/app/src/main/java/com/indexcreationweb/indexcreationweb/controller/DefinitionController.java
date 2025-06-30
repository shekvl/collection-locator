package com.indexcreationweb.indexcreationweb.controller;

import com.indexcreationweb.indexcreationweb.commands.DefinitionCommand;
import com.indexcreationweb.indexcreationweb.dto.DefinitionDto;
import com.indexcreationweb.indexcreationweb.entities.Definition;
import com.indexcreationweb.indexcreationweb.services.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/definitions")
public class DefinitionController {
    Logger logger = LoggerFactory.getLogger(DefinitionController.class);

    @Autowired
    DefinitionService definitionService;

    @GetMapping("")
    public String indexAll(Model model) {
        List<Definition> definitions = definitionService.findAll();
        List<DefinitionDto> dtoList = definitionService.getDtoListFromDefinitionList(definitions);
        model.addAttribute("definitions", dtoList);
        return "definitions/index";
    }

    @GetMapping("/new")
    public String newGet(Model model) {
        DefinitionCommand command = new DefinitionCommand();
        model.addAttribute("command", command);
        return "definitions/new";
    }

    @PostMapping("/new")
    public String newPost(@ModelAttribute DefinitionCommand command, Model model) {
        Definition save = definitionService.save(command);
        model.addAttribute("command", command);
        return "redirect:/definitions";
    }

    @GetMapping("{id}/edit")
    public String editGet(@PathVariable String id, Model model) {
        DefinitionCommand command = definitionService.getCommandFromId(id);
        model.addAttribute("command", command);
        model.addAttribute("definitionid", id);
        return "definitions/edit";
    }

    @PostMapping("{id}/edit")
    public String editPost(@PathVariable String id, @ModelAttribute DefinitionCommand command, Model model) throws IOException {
        Definition save = definitionService.edit(command, id);
        model.addAttribute("command", command);
        return "redirect:/definitions";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable String id, Model model) {
        definitionService.delete(Long.valueOf(id));
        return "redirect:/definitions";
    }


    @GetMapping("{id}/download/json")
    public ResponseEntity<InputStreamResource> downloadGetJson(@PathVariable String id, Model model) {
        DefinitionCommand command = definitionService.getCommandFromId(id);
        String filename = command.getName() + ".json";
        InputStreamResource file = definitionService.getDownloadDataJsonFromId(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/json"))
                .body(file);
    }

    @GetMapping("{id}/download/xml")
    public ResponseEntity<InputStreamResource> downloadGetXml(@PathVariable String id, Model model) throws JAXBException {
        DefinitionCommand command = definitionService.getCommandFromId(id);
        String filename = command.getName() + ".xml";
        InputStreamResource file = definitionService.getDownloadDataXmlFromId(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/xml"))
                .body(file);
    }
}
