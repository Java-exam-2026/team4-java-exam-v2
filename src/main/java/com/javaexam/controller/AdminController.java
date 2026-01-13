package com.javaexam.controller;

import com.javaexam.dto.AllProgressDto;
import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.service.AdminService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/progress/export/tsv")
    public ResponseEntity<byte[]> exportProgressAsTsv() {
        List<AllProgressDto> progressList = adminService.getAllUsersProgress();
        
        // Create TSV content
        StringBuilder tsv = new StringBuilder();
        // Add header row
        tsv.append("ユーザー名\t表示名\tチャプターコード\tチャプタータイトル\tスコア\t合格\t最終受験日時\n");
        
        // Add data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (AllProgressDto progress : progressList) {
            tsv.append(escapeTsvValue(progress.getUsername())).append("\t");
            tsv.append(escapeTsvValue(progress.getDisplayName())).append("\t");
            tsv.append(escapeTsvValue(progress.getChapterCode())).append("\t");
            tsv.append(escapeTsvValue(progress.getTitle())).append("\t");
            tsv.append(progress.getScore()).append("%\t");
            tsv.append(progress.getPassed() ? "合格" : "不合格").append("\t");
            tsv.append(progress.getLastAttemptedAt().format(formatter)).append("\n");
        }
        
        // Convert to bytes with UTF-8 encoding (with BOM for Excel compatibility)
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = tsv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(content, 0, result, bom.length, content.length);
        
        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/tab-separated-values"));
        headers.setContentDispositionFormData("attachment", "answer-status.tsv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }
    
    /**
     * Escapes special characters in TSV values to prevent injection and formatting issues.
     * Replaces tabs with spaces and removes newlines and carriage returns.
     */
    private String escapeTsvValue(String value) {
        if (value == null) {
            return "";
        }
        // Replace tabs with spaces and remove newlines/carriage returns
        return value.replace("\t", " ")
                    .replace("\n", " ")
                    .replace("\r", "");
    }
}
