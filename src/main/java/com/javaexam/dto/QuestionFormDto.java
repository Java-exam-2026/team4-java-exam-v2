package com.javaexam.dto;

import com.javaexam.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidQuestionForm
public class QuestionFormDto {
    
    private String id;
    
    @NotBlank(message = "チャプターIDは必須です")
    private String chapterId;
    
    @NotBlank(message = "問題文は必須です")
    private String questionText;
    
    @NotNull(message = "問題タイプは必須です")
    private QuestionType questionType;
    
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    
    @NotBlank(message = "正解は必須です")
    private String correctAnswer;
}
