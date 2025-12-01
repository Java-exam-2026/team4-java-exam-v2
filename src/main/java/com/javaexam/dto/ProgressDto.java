package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {
    
    private String chapterCode;
    private String title;
    private Integer score;
    private Boolean passed;
    private LocalDateTime lastAttemptedAt;
}
