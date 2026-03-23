package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerByDateDto {
    
    private String userId;
    private String username;
    private String displayName;
    private String chapterId;
    private String chapterCode;
    private String chapterTitle;
    private Integer score;
    private Boolean passed;
    private LocalDateTime answeredAt;
}
