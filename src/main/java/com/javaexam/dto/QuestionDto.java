package com.javaexam.dto;

import com.javaexam.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private UUID id;
    private String chapterCode;
    private String questionText;
    private QuestionType questionType;
    private Map<String, String> options;
    // correctAnswer is NOT included for security
}
