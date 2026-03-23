package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDetailDto {
    
    private String questionId;
    private String questionText;
    private Map<String, String> options;
    private String selectedAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private LocalDateTime answeredAt;
}
