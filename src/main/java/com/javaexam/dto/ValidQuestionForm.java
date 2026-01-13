package com.javaexam.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = QuestionFormValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidQuestionForm {
    String message() default "Invalid question form";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
