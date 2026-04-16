package com.javaexam.controller;

import com.javaexam.service.AdminService;
import com.javaexam.service.ChapterService;
import com.javaexam.service.ProgressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import java.security.Principal;

@Controller
public class HomeController {

    private final ChapterService chapterService;
    private final ProgressService progressService;
    // private final AdminService adminService;

    public HomeController(ChapterService chapterService,
            ProgressService progressService) {
        this.chapterService = chapterService;
        this.progressService = progressService;
    }

    @GetMapping("/")
    public String dashboard(Model model, Principal principal) {
        // 1. Principal（名札）があれば、そこから名前を取って進捗を取得
        if (principal != null) {
            // principal.getName() で「アオイ」というIDが取れるので、
            // それを使って自分の進捗だけをDBから持ってくる
            model.addAttribute("progressMap", progressService.getProgressByUsername(principal.getName()));
        }

        model.addAttribute("chapters", chapterService.getAllChapters());
        return "dashboard";
    }
}
