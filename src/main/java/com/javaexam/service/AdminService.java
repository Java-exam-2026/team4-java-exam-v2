package com.javaexam.service;

import com.javaexam.dto.AdminQuestionDto;
import com.javaexam.dto.AllProgressDto;
import com.javaexam.dto.UserAnswerDetailDto;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.UserAnswer;
import com.javaexam.entity.UserProgress;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.repository.UserAnswerJdbcRepository;
import com.javaexam.repository.UserProgressJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserProgressJdbcRepository userProgressJdbcRepository;
    private final QuestionJdbcRepository questionJdbcRepository;
    private final ChapterJdbcRepository chapterJdbcRepository;
    private final UserAnswerJdbcRepository userAnswerJdbcRepository;

    public AdminService(UserProgressJdbcRepository userProgressJdbcRepository,
                        QuestionJdbcRepository questionJdbcRepository,
                        ChapterJdbcRepository chapterJdbcRepository,
                        UserAnswerJdbcRepository userAnswerJdbcRepository) {
        this.userProgressJdbcRepository = userProgressJdbcRepository;
        this.questionJdbcRepository = questionJdbcRepository;
        this.chapterJdbcRepository = chapterJdbcRepository;
        this.userAnswerJdbcRepository = userAnswerJdbcRepository;
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
                        progress.getChapter().getId(),
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
        // Delete user answers first due to foreign key constraint
        userAnswerJdbcRepository.deleteByUserId(userId);
        int deletedCount = userProgressJdbcRepository.deleteByUserId(userId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No progress records found for userId: " + userId);
        }
    }

    /**
     * Deletes progress/answers for a specific user and chapter only.
     *
     * @throws IllegalArgumentException if no progress record exists for the given userId/chapterId
     */
    @Transactional
    public void deleteUserChapterProgress(String userId, String chapterId) {
        if (userId == null || userId.trim().isEmpty() || chapterId == null || chapterId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid userId/chapterId");
        }
        // Delete answers first due to foreign key constraint
        userAnswerJdbcRepository.deleteByUserIdAndChapterId(userId, chapterId);
        int deletedCount = userProgressJdbcRepository.deleteByUserIdAndChapterId(userId, chapterId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException(
                    "No progress record found for userId/chapterId: " + userId + "/" + chapterId);
        }
    }

    /**
     * Deletes all user progress records in the system.
     * @return the number of records deleted
     */
    @Transactional
    public int deleteAllProgress() {
        // Delete all user answers first due to foreign key constraint
        userAnswerJdbcRepository.deleteAll();
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

    /**
     * Retrieves all chapters.
     * @return list of all chapters
     */
    public List<Chapter> getAllChapters() {
        return chapterJdbcRepository.findAllOrdered();
    }

    /**
     * Creates a new chapter.
     * @param chapter the chapter to create
     */
    @Transactional
    public void createChapter(Chapter chapter) {
        chapterJdbcRepository.save(chapter);
    }

    /**
     * Updates an existing chapter.
     * @param chapter the chapter to update
     * @throws IllegalArgumentException if the chapter does not exist
     */
    @Transactional
    public void updateChapter(Chapter chapter) {
        int updatedCount = chapterJdbcRepository.save(chapter);
        if (updatedCount == 0) {
            throw new IllegalArgumentException("No chapter found with id: " + chapter.getId());
        }
    }

    /**
     * Deletes a chapter by ID.
     * @param chapterId the ID of the chapter to delete
     * @throws IllegalArgumentException if the chapter does not exist
     */
    @Transactional
    public void deleteChapter(String chapterId) {
        int deletedCount = chapterJdbcRepository.deleteById(chapterId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("No chapter found with id: " + chapterId);
        }
    }

    /**
     * Retrieves a chapter by ID.
     * @param chapterId the chapter ID
     * @return the chapter
     */
    public Chapter getChapterById(String chapterId) {
        return chapterJdbcRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));
    }

    /**
     * Retrieves detailed user answers for a specific user and chapter.
     * <p>
     * If the user has not submitted any answers for the given chapter, this method
     * returns an empty list rather than {@code null}.
     * 
     * @param userId    the user ID
     * @param chapterId the chapter ID
     * @return a list of user answer details; the list will be empty if no answers
     *         are found for the specified user and chapter
     */
    @Transactional(readOnly = true)
    public List<UserAnswerDetailDto> getUserAnswerDetails(String userId, String chapterId) {
        List<UserAnswer> userAnswers = userAnswerJdbcRepository.findByUserAndChapter(userId, chapterId);
        return userAnswers.stream()
                .map(answer -> new UserAnswerDetailDto(
                        answer.getQuestion().getId(),
                        answer.getQuestion().getQuestionText(),
                        answer.getQuestion().getOptions(),
                        answer.getSelectedAnswer(),
                        answer.getQuestion().getCorrectAnswer(),
                        answer.getIsCorrect(),
                        answer.getAnsweredAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves distinct users who answered questions on a specific date.
     * 
     * @param date the date in format YYYY-MM-DD
     * @return a list of users with their answer information from that date
     */
    @Transactional(readOnly = true)
    public List<com.javaexam.dto.UserAnswerByDateDto> getUsersByAnswerDate(String date) {
        List<UserAnswer> userAnswers = userAnswerJdbcRepository.findUsersByAnswerDate(date);
        
        // Group by user to get distinct users with their first answer time of the day
        return userAnswers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getUser().getId(),
                        answer -> answer,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .map(answer -> new com.javaexam.dto.UserAnswerByDateDto(
                        answer.getUser().getId(),
                        answer.getUser().getUsername(),
                        answer.getUser().getDisplayName(),
                        answer.getAnsweredAt()
                ))
                .collect(Collectors.toList());
    }
}
