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
    //private final AdminService adminService;

    public HomeController(ChapterService chapterService,
            ProgressService progressService) {
        this.chapterService = chapterService;
        this.progressService = progressService;
    }

    @GetMapping("/")
    public String dashboard(Model model, Principal principal, Authentication authentication) {
    if (principal != null && authentication != null) {
        // 【修正ポイント】
        // isAdmin かどうかに関わらず、ログインしているなら進捗データを準備する
        model.addAttribute("progressMap", progressService.getProgressByUsername(principal.getName()));
    }

    model.addAttribute("chapters", chapterService.getAllChapters());
    return "dashboard"; // 全員「一般画面」に到着！
    }
}
