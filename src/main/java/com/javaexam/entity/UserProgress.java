package com.javaexam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    private String id;
    private User user;
    private Chapter chapter;
    private Integer score;
    private Boolean passed;
    private LocalDateTime lastAttemptedAt;
}
