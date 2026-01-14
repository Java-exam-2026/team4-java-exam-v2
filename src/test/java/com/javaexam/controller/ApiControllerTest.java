package com.javaexam.controller;

import com.javaexam.JavaExamApplication;
import com.javaexam.dto.UserAnswerByDateDto;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.QuestionType;
import com.javaexam.entity.User;
import com.javaexam.entity.UserAnswer;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.repository.UserAnswerJdbcRepository;
import com.javaexam.repository.UserJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = JavaExamApplication.class)
@AutoConfigureMockMvc
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAnswerJdbcRepository userAnswerJdbcRepository;

    @Autowired
    private UserJdbcRepository userJdbcRepository;

    @Autowired
    private ChapterJdbcRepository chapterJdbcRepository;

    @Autowired
    private QuestionJdbcRepository questionJdbcRepository;

    @BeforeEach
    void setUp() {
        // Clean up test data
        userAnswerJdbcRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByAnswerDate_shouldReturnUsersWhoAnsweredOnSpecificDate() throws Exception {
        // Given: Create test data
        String testDate = "2026-01-14";
        
        // Find existing user and chapter from the database
        User existingUser = userJdbcRepository.findByUsername("testuser")
                .orElseThrow(() -> new RuntimeException("Test user not found"));
        
        Chapter existingChapter = chapterJdbcRepository.findAllOrdered().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Test chapter not found"));
        
        Question existingQuestion = questionJdbcRepository.findRandomByChapterId(existingChapter.getId(), 1).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Test question not found"));

        // Create a user answer with specific date
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(UUID.randomUUID().toString());
        userAnswer.setUser(existingUser);
        userAnswer.setChapter(existingChapter);
        userAnswer.setQuestion(existingQuestion);
        userAnswer.setSelectedAnswer("A");
        userAnswer.setIsCorrect(true);
        userAnswer.setAnsweredAt(LocalDateTime.of(2026, 1, 14, 10, 30, 0));
        
        userAnswerJdbcRepository.save(userAnswer);

        // When & Then: Call the API
        mockMvc.perform(get("/api/users/by-answer-date")
                        .param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(existingUser.getId()))
                .andExpect(jsonPath("$[0].username").value(existingUser.getUsername()))
                .andExpect(jsonPath("$[0].displayName").value(existingUser.getDisplayName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByAnswerDate_shouldReturnEmptyListWhenNoAnswersOnDate() throws Exception {
        // Given: A date with no answers
        String testDate = "2025-01-01";

        // When & Then: Call the API
        mockMvc.perform(get("/api/users/by-answer-date")
                        .param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsersByAnswerDate_shouldReturnForbiddenForNonAdminUsers() throws Exception {
        // When & Then: Call the API with USER role
        mockMvc.perform(get("/api/users/by-answer-date")
                        .param("date", "2026-01-14"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByAnswerDate_shouldReturnBadRequestForInvalidDateFormat() throws Exception {
        // When & Then: Call the API with invalid date format
        mockMvc.perform(get("/api/users/by-answer-date")
                        .param("date", "invalid-date"))
                .andExpect(status().isBadRequest());
    }
}
