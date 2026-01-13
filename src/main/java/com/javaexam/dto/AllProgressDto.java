package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllProgressDto {
    
    private String userId;
    private String username;
    private String displayName;
    private String chapterCode;
    private String title;
    private Integer score;
    private Boolean passed;
    private LocalDateTime lastAttemptedAt;
}
