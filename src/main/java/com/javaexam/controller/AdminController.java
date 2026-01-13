package com.javaexam.controller;

import com.javaexam.dto.AllProgressDto;
import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.dto.QuestionFormDto;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.QuestionType;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ChapterJdbcRepository chapterJdbcRepository;
    private final QuestionJdbcRepository questionJdbcRepository;

    public AdminController(AdminService adminService, 
                           ChapterJdbcRepository chapterJdbcRepository,
                           QuestionJdbcRepository questionJdbcRepository) {
        this.adminService = adminService;
        this.chapterJdbcRepository = chapterJdbcRepository;
        this.questionJdbcRepository = questionJdbcRepository;
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
            tsv.append(progress.getLastAttemptedAt() != null ? progress.getLastAttemptedAt().format(formatter) : "").append("\n");
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

    @GetMapping("/questions/new")
    public String showCreateQuestionForm(Model model) {
        List<Chapter> chapters = chapterJdbcRepository.findAllOrdered();
        model.addAttribute("chapters", chapters);
        model.addAttribute("questionTypes", QuestionType.values());
        model.addAttribute("questionForm", new QuestionFormDto());
        model.addAttribute("isEdit", false);
        return "admin-question-form";
    }

    @PostMapping("/questions/new")
    public String createQuestion(@Valid QuestionFormDto questionForm,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Chapter> chapters = chapterJdbcRepository.findAllOrdered();
            model.addAttribute("chapters", chapters);
            model.addAttribute("questionTypes", QuestionType.values());
            model.addAttribute("isEdit", false);
            return "admin-question-form";
        }

        try {
            Chapter chapter = chapterJdbcRepository.findById(questionForm.getChapterId())
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

            Map<String, String> options = new HashMap<>();
            if (questionForm.getOptionA() != null && !questionForm.getOptionA().trim().isEmpty()) {
                options.put("A", questionForm.getOptionA());
            }
            if (questionForm.getOptionB() != null && !questionForm.getOptionB().trim().isEmpty()) {
                options.put("B", questionForm.getOptionB());
            }
            if (questionForm.getOptionC() != null && !questionForm.getOptionC().trim().isEmpty()) {
                options.put("C", questionForm.getOptionC());
            }
            if (questionForm.getOptionD() != null && !questionForm.getOptionD().trim().isEmpty()) {
                options.put("D", questionForm.getOptionD());
            }

            Question question = new Question();
            question.setChapter(chapter);
            question.setQuestionText(questionForm.getQuestionText());
            question.setQuestionType(questionForm.getQuestionType());
            question.setOptions(options);
            question.setCorrectAnswer(questionForm.getCorrectAnswer());

            adminService.createQuestion(question);
            redirectAttributes.addFlashAttribute("message", "問題を作成しました");
            return "redirect:/admin/questions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題の作成に失敗しました: " + e.getMessage());
            return "redirect:/admin/questions/new";
        }
    }

    @GetMapping("/questions/edit/{id}")
    public String showEditQuestionForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Question question = questionJdbcRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));

            QuestionFormDto questionForm = new QuestionFormDto();
            questionForm.setId(question.getId());
            questionForm.setChapterId(question.getChapter().getId());
            questionForm.setQuestionText(question.getQuestionText());
            questionForm.setQuestionType(question.getQuestionType());
            questionForm.setOptionA(question.getOptions().get("A"));
            questionForm.setOptionB(question.getOptions().get("B"));
            questionForm.setOptionC(question.getOptions().get("C"));
            questionForm.setOptionD(question.getOptions().get("D"));
            questionForm.setCorrectAnswer(question.getCorrectAnswer());

            List<Chapter> chapters = chapterJdbcRepository.findAllOrdered();
            model.addAttribute("chapters", chapters);
            model.addAttribute("questionTypes", QuestionType.values());
            model.addAttribute("questionForm", questionForm);
            model.addAttribute("isEdit", true);
            return "admin-question-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題が見つかりませんでした");
            return "redirect:/admin/questions";
        }
    }

    @PostMapping("/questions/edit/{id}")
    public String updateQuestion(@PathVariable String id,
                                  @Valid QuestionFormDto questionForm,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Chapter> chapters = chapterJdbcRepository.findAllOrdered();
            model.addAttribute("chapters", chapters);
            model.addAttribute("questionTypes", QuestionType.values());
            model.addAttribute("isEdit", true);
            return "admin-question-form";
        }

        try {
            Chapter chapter = chapterJdbcRepository.findById(questionForm.getChapterId())
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

            Map<String, String> options = new HashMap<>();
            if (questionForm.getOptionA() != null && !questionForm.getOptionA().trim().isEmpty()) {
                options.put("A", questionForm.getOptionA());
            }
            if (questionForm.getOptionB() != null && !questionForm.getOptionB().trim().isEmpty()) {
                options.put("B", questionForm.getOptionB());
            }
            if (questionForm.getOptionC() != null && !questionForm.getOptionC().trim().isEmpty()) {
                options.put("C", questionForm.getOptionC());
            }
            if (questionForm.getOptionD() != null && !questionForm.getOptionD().trim().isEmpty()) {
                options.put("D", questionForm.getOptionD());
            }

            Question question = new Question();
            question.setId(id);
            question.setChapter(chapter);
            question.setQuestionText(questionForm.getQuestionText());
            question.setQuestionType(questionForm.getQuestionType());
            question.setOptions(options);
            question.setCorrectAnswer(questionForm.getCorrectAnswer());

            adminService.updateQuestion(question);
            redirectAttributes.addFlashAttribute("message", "問題を更新しました");
            return "redirect:/admin/questions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題の更新に失敗しました: " + e.getMessage());
            return "redirect:/admin/questions/edit/" + id;
        }
    }

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteQuestion(id);
            redirectAttributes.addFlashAttribute("message", "問題を削除しました");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "問題が見つかりませんでした");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題の削除に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/questions";
    }
}
