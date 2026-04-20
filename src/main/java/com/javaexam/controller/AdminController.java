package com.javaexam.controller;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.javaexam.annotation.Log;
import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.dto.AllProgressDto;
import com.javaexam.dto.ChapterFormDto;
import com.javaexam.dto.QuestionFormDto;
import com.javaexam.dto.UserAnswerDetailDto;
import com.javaexam.entity.ActionType;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.QuestionType;
import com.javaexam.entity.TargetType;
import com.javaexam.repository.AuditLogJdbcRepository;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.service.AdminService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
/**
 * 管理者機能の画面制御を行うコントローラー。
 * ダッシュボードの表示、問題やチャプターの管理、
 * 受験結果の統計表示および外部ファイル（TSV）出力などを担当します。
 */
public class AdminController {

    private final AdminService adminService;
    private final ChapterJdbcRepository chapterJdbcRepository;
    private final QuestionJdbcRepository questionJdbcRepository;
    private final AuditLogJdbcRepository auditLogJdbcRepository;

    public AdminController(AdminService adminService,
                           ChapterJdbcRepository chapterJdbcRepository,
                           QuestionJdbcRepository questionJdbcRepository,
                           AuditLogJdbcRepository auditLogJdbcRepository) {
        this.adminService = adminService;
        this.chapterJdbcRepository = chapterJdbcRepository;
        this.questionJdbcRepository = questionJdbcRepository;
        this.auditLogJdbcRepository = auditLogJdbcRepository;
    }
    /**
     * 管理者用ダッシュボードを表示します。
     * ユーザー総数、月間受験数、合格・不合格統計、チャプター別正答率などの
     * 統計情報を画面に渡します。
     * * @param model 画面にデータを渡すためのモデル
     * @return 管理者ダッシュボードのHTMLパス
     */
    @GetMapping("/dashboard") // これで /admin/dashboard になります
    public String adminDashboard(Model model) {
        // 管理者だけが使う「ユーザー数」をここで準備する
        model.addAttribute("userCount", adminService.getUserCount());

        // 2. ★ここを追加！「今月の受験数」をadminServiceから受け取ってHTMLに渡す
        model.addAttribute("monthlyCount", adminService.getMonthlyAttemptCount());
        
        // --- ★ここを追加！ 合格・不合格の統計データを渡す ---
        // adminService.getPassFailStats() が { "pass": 10, "fail": 5 } のようなMapを返します
        model.addAttribute("passFailStats", adminService.getPassFailStats());
        
        // ★ここを追加！ チャプターごとの正答率データをHTMLに渡す
        // adminServiceに「getChapterStats」というメソッドを作るイメージです
        model.addAttribute("chapterStats", adminService.getChapterStats());

        // さっき作った新しいHTML（admin-dashboard.html）を呼び出す
        return "admin-dashboard";
    }

    @GetMapping("/progress/detail/{userId}/{chapterId}")
    public String viewUserAnswerDetail(@PathVariable String userId,
            @PathVariable String chapterId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            List<UserAnswerDetailDto> answerDetails = adminService.getUserAnswerDetails(userId, chapterId);
            Chapter chapter = adminService.getChapterById(chapterId);

            model.addAttribute("answerDetails", answerDetails);
            model.addAttribute("chapterTitle", chapter.getTitle());
            model.addAttribute("userId", userId);
            model.addAttribute("chapterId", chapterId);

            return "admin-answer-detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "指定されたデータが見つかりませんでした");
            return "redirect:/admin/progress";
        }
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

    @PostMapping("/progress/delete/{userId}/{chapterId}")
    public String deleteUserChapterProgress(@PathVariable String userId,
            @PathVariable String chapterId,
            RedirectAttributes redirectAttributes) {
        if (userId == null || userId.trim().isEmpty() || chapterId == null || chapterId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "無効なユーザーID/チャプターIDです");
            return "redirect:/admin/progress";
        }

        try {
            adminService.deleteUserChapterProgress(userId, chapterId);
            redirectAttributes.addFlashAttribute("message", "指定チャプターの解答状況を削除しました");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "指定された解答状況が見つかりませんでした");
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
            tsv.append(progress.getLastAttemptedAt() != null ? progress.getLastAttemptedAt().format(formatter) : "")
                    .append("\n");
        }

        // Convert to bytes with UTF-8 encoding (with BOM for Excel compatibility)
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
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
     * Escapes special characters in TSV values to prevent injection and formatting
     * issues.
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
        populateFormAttributes(model, false);
        model.addAttribute("questionForm", new QuestionFormDto());
        return "admin-question-form";
    }

    @Log(action = ActionType.CREATE, target = TargetType.QUESTION)
    @PostMapping("/questions/new")
    public String createQuestion(@Valid QuestionFormDto questionForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormAttributes(model, false);
            return "admin-question-form";
        }

        try {
            Question question = buildQuestionFromForm(questionForm, null);
            adminService.createQuestion(question);
            redirectAttributes.addFlashAttribute("message", "問題を作成しました");
            return "redirect:/admin/questions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題の作成に失敗しました");
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

            // Safely extract options with null check
            Map<String, String> options = question.getOptions();
            if (options != null) {
                questionForm.setOptionA(options.get("A"));
                questionForm.setOptionB(options.get("B"));
                questionForm.setOptionC(options.get("C"));
                questionForm.setOptionD(options.get("D"));
            }

            questionForm.setCorrectAnswer(question.getCorrectAnswer());

            populateFormAttributes(model, true);
            model.addAttribute("questionForm", questionForm);
            return "admin-question-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "問題が見つかりませんでした");
            return "redirect:/admin/questions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題の読み込みに失敗しました");
            return "redirect:/admin/questions";
        }
    }S

    @Log(action = ActionType.EDIT, target = TargetType.QUESTION)
    @PostMapping("/questions/edit/{id}")
    public String updateQuestion(@PathVariable String id,
            @Valid QuestionFormDto questionForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormAttributes(model, true);
            return "admin-question-form";
        }

        try {
            Question question = buildQuestionFromForm(questionForm, id);
            adminService.updateQuestion(question);
            redirectAttributes.addFlashAttribute("message", "問題を更新しました");
            return "redirect:/admin/questions";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/questions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "問題の更新に失敗しました");
            return "redirect:/admin/questions/edit/" + id;
        }
    }

    @Log(action = ActionType.DELETE, target = TargetType.QUESTION)
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

    /**
     * Populates model attributes needed for the question form.
     */
    private void populateFormAttributes(Model model, boolean isEdit) {
        List<Chapter> chapters = chapterJdbcRepository.findAllOrdered();
        model.addAttribute("chapters", chapters);
        // Only allow SINGLE_CHOICE questions
        model.addAttribute("questionTypes", new QuestionType[] { QuestionType.SINGLE_CHOICE });
        model.addAttribute("isEdit", isEdit);
    }

    /**
     * Builds a Question entity from form data.
     */
    private Question buildQuestionFromForm(QuestionFormDto questionForm, String questionId) {
        Chapter chapter = chapterJdbcRepository.findById(questionForm.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        Map<String, String> options = buildOptionsMap(questionForm);

        Question question = new Question();
        if (questionId != null) {
            question.setId(questionId);
        }
        question.setChapter(chapter);
        question.setQuestionText(questionForm.getQuestionText());
        question.setQuestionType(questionForm.getQuestionType());
        question.setOptions(options);
        question.setCorrectAnswer(questionForm.getCorrectAnswer());

        return question;
    }

    /**
     * Map<>形式のoptionを作成するメソッド
     */
    private Map<String, String> buildOptionsMap(QuestionFormDto questionForm) {
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
        return options;
    }

    // Chapter management endpoints

    @GetMapping("/chapters")
    public String viewAllChapters(Model model) {
        List<Chapter> chapters = adminService.getAllChapters();
        model.addAttribute("chapters", chapters);
        return "admin-chapters";
    }

    @GetMapping("/chapters/new")
    public String showCreateChapterForm(Model model) {
        model.addAttribute("chapterForm", new ChapterFormDto());
        model.addAttribute("isEdit", false);
        return "admin-chapter-form";
    }

    @PostMapping("/chapters/new")
    public String createChapter(@Valid ChapterFormDto chapterForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin-chapter-form";
        }

        try {
            Chapter chapter = new Chapter();
            chapter.setChapterCode(chapterForm.getChapterCode());
            chapter.setTitle(chapterForm.getTitle());
            chapter.setSortOrder(chapterForm.getSortOrder());

            adminService.createChapter(chapter);
            redirectAttributes.addFlashAttribute("message", "チャプターを作成しました");
            return "redirect:/admin/chapters";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "チャプターの作成に失敗しました");
            return "redirect:/admin/chapters/new";
        }
    }

    @GetMapping("/chapters/edit/{id}")
    public String showEditChapterForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Chapter chapter = adminService.getChapterById(id);

            ChapterFormDto chapterForm = new ChapterFormDto();
            chapterForm.setId(chapter.getId());
            chapterForm.setChapterCode(chapter.getChapterCode());
            chapterForm.setTitle(chapter.getTitle());
            chapterForm.setSortOrder(chapter.getSortOrder());

            model.addAttribute("chapterForm", chapterForm);
            model.addAttribute("isEdit", true);
            return "admin-chapter-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "チャプターが見つかりませんでした");
            return "redirect:/admin/chapters";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "チャプターの読み込みに失敗しました");
            return "redirect:/admin/chapters";
        }
    }

    @PostMapping("/chapters/edit/{id}")
    public String updateChapter(@PathVariable String id,
            @Valid ChapterFormDto chapterForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin-chapter-form";
        }

        try {
            Chapter chapter = new Chapter();
            chapter.setId(id);
            chapter.setChapterCode(chapterForm.getChapterCode());
            chapter.setTitle(chapterForm.getTitle());
            chapter.setSortOrder(chapterForm.getSortOrder());

            adminService.updateChapter(chapter);
            redirectAttributes.addFlashAttribute("message", "チャプターを更新しました");
            return "redirect:/admin/chapters";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/chapters";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "チャプターの更新に失敗しました");
            return "redirect:/admin/chapters/edit/" + id;
        }
    }

    @PostMapping("/chapters/delete/{id}")
    public String deleteChapter(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteChapter(id);
            redirectAttributes.addFlashAttribute("message", "チャプターを削除しました");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "チャプターが見つかりませんでした");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "チャプターの削除に失敗しました");
        }
        return "redirect:/admin/chapters";
    }

    @GetMapping("/progress")
    public String viewAllProgress(Model model) {
        // 全ユーザーの進捗を取得してモデルに入れる
        List<AllProgressDto> progressList = adminService.getAllUsersProgress();
        model.addAttribute("progressList", progressList);

        // admin-progress.html を呼び出す
        return "admin-progress";
    }

    // Audit logs endpoint

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        List<AuditLog> auditLogs = auditLogJdbcRepository.findAll();
        model.addAttribute("auditLogs", auditLogs);
        return "admin-audit-logs";
    }
}
