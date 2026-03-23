package com.javaexam.service;

import com.javaexam.entity.User;
import com.javaexam.entity.UserProgress;
import com.javaexam.repository.UserJdbcRepository;
import com.javaexam.repository.UserProgressJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final UserJdbcRepository userJdbcRepository;
    private final UserProgressJdbcRepository userProgressJdbcRepository;

    public ProgressService(UserJdbcRepository userJdbcRepository,
            UserProgressJdbcRepository userProgressJdbcRepository) {
        this.userJdbcRepository = userJdbcRepository;
        this.userProgressJdbcRepository = userProgressJdbcRepository;
    }

    public Map<String, UserProgress> getProgressByUsername(String username) {
        Optional<User> user = userJdbcRepository.findByUsername(username);
        if (user.isEmpty()) {
            return Collections.emptyMap();
        }

        return userProgressJdbcRepository.findByUser(user.get()).stream()
                .collect(Collectors.toMap(
                        progress -> progress.getChapter().getChapterCode(),
                        progress -> progress,
                        (existing, replacement) -> replacement));
    }
}

