package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminQuestionDto {
    
    private String id;
    private String chapterCode;
    private String questionText;
    private Map<String, String> options;
    private String correctAnswer; // Included for admin
}
