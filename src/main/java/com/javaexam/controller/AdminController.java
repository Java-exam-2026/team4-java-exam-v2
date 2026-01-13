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
        adminService.deleteUserProgress(userId);
        redirectAttributes.addFlashAttribute("message", "ユーザーの解答状況を削除しました");
        return "redirect:/admin/progress";
    }

    @PostMapping("/progress/delete-all")
    public String deleteAllProgress(RedirectAttributes redirectAttributes) {
        adminService.deleteAllProgress();
        redirectAttributes.addFlashAttribute("message", "すべての解答状況を削除しました");
        return "redirect:/admin/progress";
    }
}
