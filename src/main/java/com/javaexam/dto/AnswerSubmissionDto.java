package com.javaexam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmissionDto {
    
    @NotNull(message = "Question ID is required")
    private UUID questionId;
    
    @NotBlank(message = "Selected answer is required")
    private String selectedAnswer;
}
