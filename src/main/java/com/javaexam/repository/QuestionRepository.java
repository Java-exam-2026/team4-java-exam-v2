package com.javaexam.repository;

import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByChapter(Chapter chapter);

    @Query(value = "SELECT * FROM questions WHERE chapter_id = :chapterId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByChapterId(@Param("chapterId") String chapterId, @Param("limit") int limit);

    Page<Question> findAll(Pageable pageable);

    long countByChapter(Chapter chapter);
}
