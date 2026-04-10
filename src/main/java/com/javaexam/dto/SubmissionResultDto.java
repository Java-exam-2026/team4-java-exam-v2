
package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResultDto {

    private String chapterCode;
    private Integer score;
    private Boolean passed;
    private Integer correctAnswers;
    private Integer totalQuestions;
}