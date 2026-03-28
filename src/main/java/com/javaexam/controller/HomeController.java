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
        // 1. ログインしているかチェック
        if (principal != null && authentication != null) {
            // 2. 「ADMIN」というバッジ（権限）を持っているか確認
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // 3. 管理者なら、問答無用で管理者用トップページへ飛ばす！
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }

            // 4. 一般ユーザーなら自分の進捗を準備
            model.addAttribute("progressMap", progressService.getProgressByUsername(principal.getName()));
        }

        // 5. 一般ユーザー用のクイズ一覧を準備して画面を出す
        model.addAttribute("chapters", chapterService.getAllChapters());
        return "dashboard";
    }
}
