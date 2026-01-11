package com.javaexam.service;

import com.javaexam.dto.*;
import com.javaexam.entity.*;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.repository.UserJdbcRepository;
import com.javaexam.repository.UserProgressJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

  @Autowired
  private QuestionJdbcRepository questionJdbcRepository;

  @Autowired
  private ChapterJdbcRepository chapterJdbcRepository;

  @Autowired
  private UserProgressJdbcRepository userProgressJdbcRepository;

  @Autowired
  private UserJdbcRepository userJdbcRepository;

  @Transactional(readOnly = true)
  public Map<String, Object> getQuizForChapter(String chapterCode) {
    Chapter chapter = chapterJdbcRepository.findByChapterCode(chapterCode)
        .orElseThrow(() -> new RuntimeException("Chapter not found"));

    List<Question> questions = questionJdbcRepository.findRandomByChapterId(chapter.getId(), 20);

    List<QuestionDto> questionDtos = questions.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("chapterTitle", chapter.getTitle());
    response.put("questions", questionDtos);

    return response;
  }

  @Transactional(readOnly = true)
  public boolean hasUserSubmitted(String username, String chapterCode) {
    User user = userJdbcRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Chapter chapter = chapterJdbcRepository.findByChapterCode(chapterCode)
        .orElseThrow(() -> new RuntimeException("Chapter not found"));

    UserProgress progress = userProgressJdbcRepository.findByUserAndChapter(user.getId(), chapter.getId())
        .orElse(null);

    return progress != null && progress.getHasSubmitted() != null && progress.getHasSubmitted();
  }

  @Transactional
  public SubmissionResultDto submitQuiz(String username, String chapterCode, SubmissionRequestDto submission) {
    User user = userJdbcRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Chapter chapter = chapterJdbcRepository.findByChapterCode(chapterCode)
        .orElseThrow(() -> new RuntimeException("Chapter not found"));

    // Check if user has already submitted for this chapter
    UserProgress existingProgress = userProgressJdbcRepository.findByUserAndChapter(user.getId(), chapter.getId())
        .orElse(null);
    
    if (existingProgress != null && existingProgress.getHasSubmitted() != null && existingProgress.getHasSubmitted()) {
      throw new RuntimeException("You have already submitted answers for this chapter");
    }

    int totalQuestions = submission.getAnswers().size();
    int correctCount = 0;

    for (AnswerSubmissionDto answerDto : submission.getAnswers()) {
      Question question = questionJdbcRepository.findById(answerDto.getQuestionId().toString())
          .orElse(null);

      if (question != null) {
        if (checkAnswer(question, answerDto.getSelectedAnswer())) {
          correctCount++;
        }
      }
    }

    int score = totalQuestions > 0 ? (int) (((double) correctCount / totalQuestions) * 100) : 0;
    boolean passed = score >= 80; // Assuming 80% pass rate

    // Update progress
    UserProgress progress = existingProgress != null ? existingProgress : new UserProgress();

    if (progress.getId() == null) {
      progress.setId(UUID.randomUUID().toString());
      progress.setUser(user);
      progress.setChapter(chapter);
      progress.setPassed(false);
      progress.setScore(0);
      progress.setHasSubmitted(false);
    }

    progress.setScore(score);
    progress.setPassed(passed);
    progress.setHasSubmitted(true);
    progress.setLastAttemptedAt(java.time.LocalDateTime.now());

    userProgressJdbcRepository.save(progress);

    return new SubmissionResultDto(chapterCode, score, passed, correctCount, totalQuestions);
  }

  private boolean checkAnswer(Question question, String userAnswer) {
    if (userAnswer == null)
      return false;

    String correct = question.getCorrectAnswer();

    if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
      // Sort both and compare
      // Assuming comma separated
      List<String> userParts = Arrays.asList(userAnswer.split(","));
      List<String> correctParts = Arrays.asList(correct.split(","));
      Collections.sort(userParts);
      Collections.sort(correctParts);
      return userParts.equals(correctParts);
    } else if (question.getQuestionType() == QuestionType.FILL_IN_THE_BLANK) {
      return correct.trim().equalsIgnoreCase(userAnswer.trim());
    } else {
      // Single choice
      return correct.equals(userAnswer);
    }
  }

  private QuestionDto convertToDto(Question question) {
    return new QuestionDto(
        UUID.fromString(question.getId()),
        question.getChapter().getChapterCode(),
        question.getQuestionText(),
        question.getQuestionType(),
        question.getOptions());
  }
}
