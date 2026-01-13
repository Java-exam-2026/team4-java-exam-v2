package com.javaexam.controller;

import com.javaexam.dto.AllProgressDto;
import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/progress")
    public String viewAllProgress(Model model) {
        List<AllProgressDto> progressList = adminService.getAllUsersProgress();
        model.addAttribute("progressList", progressList);
        return "admin-progress";
    }

    @GetMapping("/questions")
    public String viewAllQuestions(Model model) {
        List<AdminQuestionDto> questions = adminService.getAllQuestionsWithAnswers();
        model.addAttribute("questions", questions);
        return "admin-questions";
    }

    @PostMapping("/progress/delete/{userId}")
    public String deleteUserProgress(@PathVariable String userId, RedirectAttributes redirectAttributes) {
        if (userId == null || userId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "無効なユーザーIDです");
            return "redirect:/admin/progress";
        }
        
        try {
            adminService.deleteUserProgress(userId);
            redirectAttributes.addFlashAttribute("message", "ユーザーの解答状況を削除しました");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "指定されたユーザーの解答状況が見つかりませんでした");
        }
        return "redirect:/admin/progress";
    }

    @PostMapping("/progress/delete-all")
    public String deleteAllProgress(RedirectAttributes redirectAttributes) {
        int deletedCount = adminService.deleteAllProgress();
        if (deletedCount > 0) {
            redirectAttributes.addFlashAttribute("message", "すべての解答状況を削除しました (" + deletedCount + "件)");
        } else {
            redirectAttributes.addFlashAttribute("message", "削除する解答状況がありませんでした");
        }
        return "redirect:/admin/progress";
    }
}
