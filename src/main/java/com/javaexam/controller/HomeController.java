package com.javaexam.controller;

import com.javaexam.service.ChapterService;
import com.javaexam.service.ProgressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    private final ChapterService chapterService;
    private final ProgressService progressService;

    public HomeController(ChapterService chapterService,
            ProgressService progressService) {
        this.chapterService = chapterService;
        this.progressService = progressService;
    }

    @GetMapping("/")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("chapters", chapterService.getAllChapters());

        if (principal != null) {
            model.addAttribute("progressMap", progressService.getProgressByUsername(principal.getName()));
        }

        return "dashboard";
    }
}
