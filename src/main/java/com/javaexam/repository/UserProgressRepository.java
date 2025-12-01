package com.javaexam.repository;

import com.javaexam.entity.Chapter;
import com.javaexam.entity.User;
import com.javaexam.entity.UserProgress;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, String> {

    @EntityGraph(attributePaths = { "chapter" })
    List<UserProgress> findByUser(User user);

    Optional<UserProgress> findByUserAndChapter(User user, Chapter chapter);

    @EntityGraph(attributePaths = { "chapter", "user" })
    List<UserProgress> findAll();
}
