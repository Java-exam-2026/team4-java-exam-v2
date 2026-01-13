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
                        progress.getUser().getId(),
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

    /**
     * Deletes all progress records for a specific user.
     * @param userId the ID of the user whose progress should be deleted
     * @throws IllegalArgumentException if no progress records exist for the given userId
     */
    @Transactional
    public void deleteUserProgress(String userId) {
        int deletedCount = userProgressJdbcRepository.deleteByUserId(userId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No progress records found for userId: " + userId);
        }
    }

    /**
     * Deletes all user progress records in the system.
     * @return the number of records deleted
     */
    @Transactional
    public int deleteAllProgress() {
        return userProgressJdbcRepository.deleteAll();
    }

    /**
     * Creates a new question.
     * @param question the question to create
     */
    @Transactional
    public void createQuestion(Question question) {
        questionJdbcRepository.save(question);
    }

    /**
     * Updates an existing question.
     * @param question the question to update
     * @throws IllegalArgumentException if the question does not exist
     */
    @Transactional
    public void updateQuestion(Question question) {
        int updatedCount = questionJdbcRepository.save(question);
        if (updatedCount == 0) {
            throw new IllegalArgumentException("No question found with id: " + question.getId());
        }
    }

    /**
     * Deletes a question by ID.
     * @param questionId the ID of the question to delete
     * @throws IllegalArgumentException if the question does not exist
     */
    @Transactional
    public void deleteQuestion(String questionId) {
        int deletedCount = questionJdbcRepository.deleteById(questionId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No question found with id: " + questionId);
        }
    }
}
