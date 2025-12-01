package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminQuestionDto {
    
    private UUID id;
    private String chapterCode;
    private String questionText;
    private Map<String, String> options;
    private String correctAnswer; // Included for admin
}
