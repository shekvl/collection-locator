package com.anonymizerweb.anonymizerweb.controller;

import com.anonymizerweb.anonymizerweb.commands.EditCollectionCommand;
import com.anonymizerweb.anonymizerweb.commands.NewCollectionCommand;
import com.anonymizerweb.anonymizerweb.entities.Collection;
import com.anonymizerweb.anonymizerweb.services.CollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/collections")
public class CollectionController {
    Logger logger = LoggerFactory.getLogger(CollectionController.class);

    @Autowired
    CollectionService collectionService;

    @GetMapping("")
    public String indexAll(Model model) {
        List<Collection> collections = collectionService.findAll();
        model.addAttribute("collections", collections);
        return "collections/index";
    }

    @GetMapping("/{id}")
    public String index(@PathVariable String id, Model model) {
        Collection collection = collectionService.findbyId(Long.valueOf(id));
        model.addAttribute("collection", collection);
        return "collections/show";
    }

    @GetMapping("/{id}/edit")
    public String editGet(@PathVariable String id, Model model) {
        EditCollectionCommand command = collectionService.getEditCommand(Long.valueOf(id));
        Collection collection = collectionService.findbyId(Long.valueOf(id));
        model.addAttribute("command", command);
        model.addAttribute("collection", collection);
        return "collections/edit";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@ModelAttribute EditCollectionCommand command, @PathVariable String id, Model model) {
        Collection collection = collectionService.updateCollection(command, Long.valueOf(id));
        model.addAttribute("collection", collection);
        return "redirect:/collections/" + collection.getId();
    }


    @GetMapping("/new")
    public String newGet(Model model) {
        NewCollectionCommand command = new NewCollectionCommand();
        model.addAttribute("command", command);
        return "collections/new";
    }

    @PostMapping("/new")
    public String newPost(@ModelAttribute NewCollectionCommand command, Model model) throws IOException {
        Collection save = collectionService.save(command);
        model.addAttribute("command", command);
        return "redirect:/collections/" + save.getId();
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable String id, Model model) {
        collectionService.delete(Long.valueOf(id));
        return "redirect:/collections";
    }
}
