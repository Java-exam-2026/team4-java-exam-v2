package com.javaexam.controller;

import com.javaexam.dto.AllProgressDto;
import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
