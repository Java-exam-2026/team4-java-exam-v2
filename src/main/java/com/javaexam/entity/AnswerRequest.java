package com.javaexam.entity;

import lombok.Data;

@Data
public class AnswerRequest {
    private String id;
    private String questionId;
    private String selectedAnswer;
}
