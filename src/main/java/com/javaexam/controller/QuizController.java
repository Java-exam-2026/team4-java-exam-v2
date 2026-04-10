package com.javaexam.controller;

import com.javaexam.entity.AnswerRequest;
import com.javaexam.entity.UserAnswer;
import com.javaexam.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * 章ごとの問題一覧を取得
     */
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<?> getQuestions(@PathVariable String chapterId) {
        return ResponseEntity.ok(quizService.getQuizForChapter(chapterId));
    }

    /**
     * 保存済みの回答を取得（再開用）
     * userId と chapterId が Long で送られてくる場合を想定し String に変換
     */
    @GetMapping("/saved-answers")
    public ResponseEntity<List<UserAnswer>> getSavedAnswers(
            @RequestParam Long userId, 
            @RequestParam Long chapterId) {
        
        List<UserAnswer> answers = quizService.getSavedAnswers(
                String.valueOf(userId), 
                String.valueOf(chapterId)
        );
        return ResponseEntity.ok(answers);
    }

    /**
     * 一時保存
     */
    @PostMapping("/save-temporary")
    public ResponseEntity<?> saveTemporary(
            @RequestParam Long userId,
            @RequestParam Long chapterId,
            @RequestBody List<AnswerRequest> answers) {

        quizService.saveTemporary(
                String.valueOf(userId), 
                String.valueOf(chapterId), 
                answers
        );
        return ResponseEntity.ok(Map.of("message", "一時保存しました"));
    }

    /**
     * 正式提出
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(
            @RequestParam Long userId,
            @RequestParam Long chapterId,
            @RequestBody List<AnswerRequest> answers) {

        quizService.submitQuiz(
                String.valueOf(userId), 
                String.valueOf(chapterId), 
                answers
        );
        return ResponseEntity.ok(Map.of("message", "提出が完了しました"));
    }

    /**
     * 提出済みかどうかを確認
     */
    @GetMapping("/status")
    public ResponseEntity<Boolean> checkStatus(
            @RequestParam String userId,
            @RequestParam String chapterId) {
        return ResponseEntity.ok(quizService.hasUserSubmitted(userId, chapterId));
    }

    /**
     * 回答結果の詳細を取得
     */
    @GetMapping("/details")
    public ResponseEntity<List<UserAnswer>> getDetails(
            @RequestParam String userId,
            @RequestParam String chapterId) {
        return ResponseEntity.ok(quizService.getUserAnswerDetails(userId, chapterId));
    }
}