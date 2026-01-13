package com.javaexam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterFormDto {
    
    private String id;
    
    @NotBlank(message = "チャプターコードは必須です")
    private String chapterCode;
    
    @NotBlank(message = "タイトルは必須です")
    private String title;
    
    @NotNull(message = "ソート順は必須です")
    private Integer sortOrder;
}
