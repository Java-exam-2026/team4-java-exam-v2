package com.javaexam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {

    private String id;
    private User user;
    private Chapter chapter;
    private Question question;
    private String selectedAnswer;
    private Boolean isCorrect;
    private LocalDateTime answeredAt;
}
