package com.javaexam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private Integer score = 0;
    private Boolean passed = false;

    // ★★★ これが重要：この変数を定義することで get/setHasSubmitted が作られます ★★★
    private Boolean hasSubmitted = false; 

    @Enumerated(EnumType.STRING)
    private ProgressStatus status = ProgressStatus.IN_PROGRESS;

    private LocalDateTime lastAttemptedAt;
    private LocalDateTime updatedAt;

    public enum ProgressStatus {
        IN_PROGRESS,
        COMPLETED
    }
}