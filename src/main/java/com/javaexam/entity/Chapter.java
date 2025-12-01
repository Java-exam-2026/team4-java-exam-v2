package com.javaexam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {

    private String id;
    private String chapterCode;
    private String title;
    private Integer sortOrder;
}
