package com.javaexam.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.entity.Question;

/**
 * Question エンティティの変更を計算するクラス
 * 変更前と変更後の Question オブジェクトを比較し、変更されたフィールドとその値を JSON 形式で返す
 * 変更がない場合は null を返す
 * 対象フィールド:
 * - chapter
 * - question_text
 * - question_type
 * - options
 * - answer
 * options は Map<String, String> として JSON に変換して比較する
 */
@Component
public class QuestionChangeCalculator implements AuditLogChangeCalculator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String calculateChanges(Object before, Object after) {
        if (before == null || after == null) {
            return null;
        }

        Question beforeQ = (Question) before;
        Question afterQ = (Question) after;
        
        Map<String, Map<String, String>> changes = new LinkedHashMap<>();

        // chapter の変更を確認
        if (!Objects.equals(beforeQ.getChapter(), afterQ.getChapter())) {
            changes.put("chapter", Map.of(
                "before", beforeQ.getChapter() != null ? beforeQ.getChapter().toString() : "null",
                "after", afterQ.getChapter() != null ? afterQ.getChapter().toString() : "null"
            ));
        }

        // question_text の変更を確認
        if (!Objects.equals(beforeQ.getQuestionText(), afterQ.getQuestionText())) {
            changes.put("question_text", Map.of(
                "before", beforeQ.getQuestionText() != null ? beforeQ.getQuestionText() : "null",
                "after", afterQ.getQuestionText() != null ? afterQ.getQuestionText() : "null"
            ));
        }

        // question_type の変更を確認
        if (!Objects.equals(beforeQ.getQuestionType(), afterQ.getQuestionType())) {
            changes.put("question_type", Map.of(
                "before", beforeQ.getQuestionType() != null ? beforeQ.getQuestionType().toString() : "null",
                "after", afterQ.getQuestionType() != null ? afterQ.getQuestionType().toString() : "null"
            ));
        }

        // options の変更を確認
        String beforeOptions = toJsonString(beforeQ.getOptions());
        String afterOptions = toJsonString(afterQ.getOptions());
        if (!Objects.equals(beforeOptions, afterOptions)) {
            changes.put("options", Map.of(
                "before", beforeOptions,
                "after", afterOptions
            ));
        }

        // correctAnswer の変更を確認
        if (!Objects.equals(beforeQ.getCorrectAnswer(), afterQ.getCorrectAnswer())) {
            changes.put("answer", Map.of(
                "before", beforeQ.getCorrectAnswer() != null ? beforeQ.getCorrectAnswer() : "null",
                "after", afterQ.getCorrectAnswer() != null ? afterQ.getCorrectAnswer() : "null"
            ));
        }

        return changes.isEmpty() ? null : toJson(changes);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private String toJsonString(Map<String, String> options) {
        String json = toJson(options != null ? options : Collections.emptyMap());
        return json != null ? json : "null";
    }
}
