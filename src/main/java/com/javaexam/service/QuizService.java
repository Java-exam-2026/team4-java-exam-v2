package com.javaexam.service;

import com.javaexam.entity.*;
import com.javaexam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private UserAnswerJdbcRepository userAnswerRepository;

    @Autowired
    private UserProgressJdbcRepository userProgressRepository;

    @Autowired
    private QuestionJdbcRepository questionRepository;

    /**
     * 章に紐づくクイズ一覧を取得
     * ※QuestionJdbcRepositoryにfindByChapterIdがない場合はfindAllからフィルタリング
     */
    public List<Question> getQuizForChapter(String chapterId) {
        return questionRepository.findAll().stream()
                .filter(q -> q.getChapter() != null && chapterId.equals(q.getChapter().getId()))
                .collect(Collectors.toList());
    }

    /**
     * 保存済みの回答を取得（再開用）
     */
    public List<UserAnswer> getSavedAnswers(String userId, String chapterId) {
        return userAnswerRepository.findByUserAndChapter(userId, chapterId);
    }

    /**
     * 一時保存
     * 回答を保存し、進捗を IN_PROGRESS にする
     */
    @Transactional
    public void saveTemporary(String userId, String chapterId, List<AnswerRequest> answerRequests) {
        checkIfAlreadyCompleted(userId, chapterId);

        for (AnswerRequest req : answerRequests) {
            UserAnswer ua = mapToEntity(userId, chapterId, req);
            // エラー修正: 命名規則を Java の標準（setHasSubmitted）に合わせる
            ua.setHasSubmitted(false); 
            userAnswerRepository.save(ua);
        }

        updateUserProgress(userId, chapterId, UserProgress.ProgressStatus.IN_PROGRESS, null, false);
    }

    /**
     * 正式提出
     * 採点を行い、進捗を COMPLETED にする
     */
    @Transactional
    public void submitQuiz(String userId, String chapterId, List<AnswerRequest> answerRequests) {
        checkIfAlreadyCompleted(userId, chapterId);

        int correctCount = 0;
        for (AnswerRequest req : answerRequests) {
            UserAnswer ua = mapToEntity(userId, chapterId, req);
            
            // 正誤判定
            boolean isCorrect = checkAnswer(req.getQuestionId(), req.getSelectedAnswer());
            ua.setIsCorrect(isCorrect);
            ua.setHasSubmitted(true); // 提出済みに設定
            
            if (isCorrect) correctCount++;
            userAnswerRepository.save(ua);
        }

        double score = (answerRequests.isEmpty()) ? 0 : ((double) correctCount / answerRequests.size()) * 100;
        boolean isPassed = score >= 80;

        updateUserProgress(userId, chapterId, UserProgress.ProgressStatus.COMPLETED, (int)score, isPassed);
    }

    /**
     * 提出済みかどうかを確認
     */
    public boolean hasUserSubmitted(String userId, String chapterId) {
        return userProgressRepository.findByUserAndChapter(userId, chapterId)
                .map(p -> p.getStatus() == UserProgress.ProgressStatus.COMPLETED)
                .orElse(false);
    }

    /**
     * 回答詳細の取得
     */
    public List<UserAnswer> getUserAnswerDetails(String userId, String chapterId) {
        return userAnswerRepository.findByUserAndChapter(userId, chapterId);
    }

    // --- Private Helper Methods ---

    private void checkIfAlreadyCompleted(String userId, String chapterId) {
        userProgressRepository.findByUserAndChapter(userId, chapterId).ifPresent(p -> {
            if (p.getStatus() == UserProgress.ProgressStatus.COMPLETED) {
                throw new IllegalStateException("このチャプターは既に提出済みのため、変更できません。");
            }
        });
    }

    private boolean checkAnswer(String questionId, String selectedAnswer) {
        return questionRepository.findById(questionId)
                .map(q -> q.getCorrectAnswer() != null && q.getCorrectAnswer().equals(selectedAnswer))
                .orElse(false);
    }

    private UserAnswer mapToEntity(String userId, String chapterId, AnswerRequest req) {
        UserAnswer ua = new UserAnswer();
        ua.setId(req.getId() != null ? req.getId() : UUID.randomUUID().toString());
        
        User user = new User(); user.setId(userId);
        ua.setUser(user);
        
        Chapter chapter = new Chapter(); chapter.setId(chapterId);
        ua.setChapter(chapter);
        
        Question question = new Question(); question.setId(req.getQuestionId());
        ua.setQuestion(question);
        
        ua.setSelectedAnswer(req.getSelectedAnswer());
        ua.setAnsweredAt(LocalDateTime.now());
        return ua;
    }

    private void updateUserProgress(String userId, String chapterId, UserProgress.ProgressStatus status, Integer score, boolean isPassed) {
        UserProgress progress = userProgressRepository.findByUserAndChapter(userId, chapterId)
                .orElseGet(() -> {
                    UserProgress newProgress = new UserProgress();
                    newProgress.setId(UUID.randomUUID().toString());
                    User u = new User(); u.setId(userId);
                    Chapter c = new Chapter(); c.setId(chapterId);
                    newProgress.setUser(u);
                    newProgress.setChapter(c);
                    return newProgress;
                });
        
        progress.setStatus(status);
        if (score != null) progress.setScore(score);
        progress.setPassed(isPassed);
        progress.setUpdatedAt(LocalDateTime.now());
        progress.setLastAttemptedAt(LocalDateTime.now());
        
        // COMPLETED の場合は Entity の hasSubmitted フラグも同期させる
        if (status == UserProgress.ProgressStatus.COMPLETED) {
            progress.setHasSubmitted(true);
        }
        
        userProgressRepository.save(progress);
    }
}