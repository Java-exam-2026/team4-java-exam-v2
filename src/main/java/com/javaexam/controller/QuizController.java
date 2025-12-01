package com.javaexam.controller;

import com.javaexam.dto.SubmissionRequestDto;
import com.javaexam.dto.SubmissionResultDto;
import com.javaexam.service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

  private final QuizService quizService;

  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }

  @GetMapping("/{chapterCode}")
  public String getQuiz(@PathVariable String chapterCode, Model model) {
    Map<String, Object> quizData = quizService.getQuizForChapter(chapterCode);
    model.addAttribute("chapterTitle", quizData.get("chapterTitle"));
    model.addAttribute("questions", quizData.get("questions"));
    model.addAttribute("chapterCode", chapterCode);

    SubmissionRequestDto submission = new SubmissionRequestDto();
    submission.setChapterCode(chapterCode);
    model.addAttribute("submission", submission);

    return "quiz";
  }

  @PostMapping("/{chapterCode}")
  public String submitQuiz(@PathVariable String chapterCode,
      @ModelAttribute SubmissionRequestDto submission,
      Principal principal,
      Model model) {
    SubmissionResultDto result = quizService.submitQuiz(principal.getName(), chapterCode, submission);
    model.addAttribute("result", result);
    return "result";
  }
}
