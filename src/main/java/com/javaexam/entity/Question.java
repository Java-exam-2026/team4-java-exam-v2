package com.javaexam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    private String id;
    private Chapter chapter;
    private String questionText;
    private QuestionType questionType;
    private Map<String, String> options;
    private String optionsJson;
    private String correctAnswer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
