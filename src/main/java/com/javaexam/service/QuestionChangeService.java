package com.javaexam.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.entity.Question;

@Component
public class QuestionChangeService implements AuditLogChangeCalculator {

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
        if (!Objects.equals(beforeQ.getOptionsJson(), afterQ.getOptionsJson())) {
            changes.put("options", Map.of(
                "before", beforeQ.getOptionsJson() != null ? beforeQ.getOptionsJson() : "null",
                "after", afterQ.getOptionsJson() != null ? afterQ.getOptionsJson() : "null"
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
}
