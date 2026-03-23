package com.javaexam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequestDto {
    
    @NotBlank(message = "Chapter code is required")
    private String chapterCode;
    
    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<AnswerSubmissionDto> answers;
}
