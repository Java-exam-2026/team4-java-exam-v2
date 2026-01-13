package com.javaexam.dto;

import com.javaexam.entity.QuestionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuestionFormValidator implements ConstraintValidator<ValidQuestionForm, QuestionFormDto> {

    @Override
    public boolean isValid(QuestionFormDto form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        // For SINGLE_CHOICE and MULTIPLE_CHOICE questions, validate correctAnswer references a non-empty option
        if (form.getQuestionType() == QuestionType.SINGLE_CHOICE || 
            form.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            
            String correctAnswer = form.getCorrectAnswer();
            if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
                return true; // Let @NotBlank handle this
            }

            // Check if correctAnswer references a provided option
            boolean validAnswer = false;
            String[] answers = correctAnswer.split(",");
            
            for (String answer : answers) {
                String trimmedAnswer = answer.trim().toUpperCase();
                String optionValue = null;
                
                switch (trimmedAnswer) {
                    case "A":
                        optionValue = form.getOptionA();
                        break;
                    case "B":
                        optionValue = form.getOptionB();
                        break;
                    case "C":
                        optionValue = form.getOptionC();
                        break;
                    case "D":
                        optionValue = form.getOptionD();
                        break;
                }
                
                if (optionValue == null || optionValue.trim().isEmpty()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                        "正解 '" + trimmedAnswer + "' に対応する選択肢が入力されていません")
                        .addPropertyNode("correctAnswer")
                        .addConstraintViolation();
                    return false;
                }
            }

            // Validate that at least 2 options are provided for choice questions
            int optionCount = 0;
            if (form.getOptionA() != null && !form.getOptionA().trim().isEmpty()) optionCount++;
            if (form.getOptionB() != null && !form.getOptionB().trim().isEmpty()) optionCount++;
            if (form.getOptionC() != null && !form.getOptionC().trim().isEmpty()) optionCount++;
            if (form.getOptionD() != null && !form.getOptionD().trim().isEmpty()) optionCount++;
            
            if (optionCount < 2) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "選択式問題には少なくとも2つの選択肢が必要です")
                    .addPropertyNode("optionA")
                    .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
