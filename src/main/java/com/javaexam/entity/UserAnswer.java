package com.javaexam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ユーザーの回答情報を保持するエンティティ
 * QuizService, AdminService, UserAnswerJdbcRepository のすべてで使用されます
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {

    private String id;
    
    // AdminService が ua.getUser() で参照するため
    private User user;
    
    // AdminService が ua.getChapter() で参照するため
    private Chapter chapter;
    
    // AdminService が ua.getQuestion() で参照するため
    private Question question;

    // AdminService が getSelectedAnswer() として参照するため
    // (以前の answerContent から名称変更)
    private String selectedAnswer;

    // AdminService が getIsCorrect() として参照するため
    private Boolean isCorrect;

    private Boolean hasSubmitted;

    // AdminService が getAnsweredAt() として参照するため
    private LocalDateTime answeredAt;

    /**
     * 【互換性のための追加】
     * もし QuizService などで setAnswerContent(string) を呼んでいる箇所があっても
     * エラーにならないようにエイリアスメソッドを作成しておきます。
     */
    public void setAnswerContent(String content) {
        this.selectedAnswer = content;
    }

    public String getAnswerContent() {
        return this.selectedAnswer;
    }
}