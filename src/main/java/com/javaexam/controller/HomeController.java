package com.javaexam.controller;

import com.javaexam.entity.User;
import com.javaexam.entity.UserProgress;
import com.javaexam.repository.UserProgressRepository;
import com.javaexam.repository.UserRepository;
import com.javaexam.service.ChapterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ChapterService chapterService;
    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;

    public HomeController(ChapterService chapterService,
            UserProgressRepository userProgressRepository,
            UserRepository userRepository) {
        this.chapterService = chapterService;
        this.userProgressRepository = userProgressRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("chapters", chapterService.getAllChapters());

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                List<UserProgress> progressList = userProgressRepository.findByUser(user);
                Map<String, UserProgress> progressMap = progressList.stream()
                        .collect(Collectors.toMap(p -> p.getChapter().getChapterCode(), p -> p));
                model.addAttribute("progressMap", progressMap);
            }
        }

        return "dashboard";
    }
}
