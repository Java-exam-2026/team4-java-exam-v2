package com.javaexam.controller;

import com.javaexam.service.AdminService;
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
    private final AdminService adminService;

    public HomeController(ChapterService chapterService,
            ProgressService progressService,
            AdminService adminService) {
        this.chapterService = chapterService;
        this.progressService = progressService;
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("chapters", chapterService.getAllChapters());

        model.addAttribute("userCount", adminService.getUserCount()); // ←これ追加

        if (principal != null) {
            model.addAttribute("progressMap", progressService.getProgressByUsername(principal.getName()));
        }

        return "dashboard";
    }
}
