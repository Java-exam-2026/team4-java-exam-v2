package com.javaexam.service;

import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.dto.AllProgressDto;
import com.javaexam.entity.Question;
import com.javaexam.entity.UserProgress;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.repository.UserProgressJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserProgressJdbcRepository userProgressJdbcRepository;
    private final QuestionJdbcRepository questionJdbcRepository;

    public AdminService(UserProgressJdbcRepository userProgressJdbcRepository,
                        QuestionJdbcRepository questionJdbcRepository) {
        this.userProgressJdbcRepository = userProgressJdbcRepository;
        this.questionJdbcRepository = questionJdbcRepository;
    }

    /**
     * Retrieves all user progress records for admin review.
     * Note: Currently loads all records without pagination. For production systems with large datasets,
     * consider implementing pagination or filtering.
     */
    @Transactional(readOnly = true)
    public List<AllProgressDto> getAllUsersProgress() {
        List<UserProgress> allProgress = userProgressJdbcRepository.findAll();
        return allProgress.stream()
                .map(progress -> new AllProgressDto(
                        progress.getUser().getUsername(),
                        progress.getUser().getDisplayName(),
                        progress.getChapter().getChapterCode(),
                        progress.getChapter().getTitle(),
                        progress.getScore(),
                        progress.getPassed(),
                        progress.getLastAttemptedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all questions with their correct answers for admin review.
     * Note: Currently loads all records without pagination. For production systems with large datasets,
     * consider implementing pagination or filtering by chapter.
     */
    @Transactional(readOnly = true)
    public List<AdminQuestionDto> getAllQuestionsWithAnswers() {
        List<Question> allQuestions = questionJdbcRepository.findAll();
        return allQuestions.stream()
                .map(question -> new AdminQuestionDto(
                        question.getId(),
                        question.getChapter().getChapterCode(),
                        question.getQuestionText(),
                        question.getOptions(),
                        question.getCorrectAnswer()
                ))
                .collect(Collectors.toList());
    }
}
