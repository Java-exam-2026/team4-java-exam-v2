package com.javaexam.repository;

import com.javaexam.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {

    Optional<Chapter> findByChapterCode(String chapterCode);

    List<Chapter> findAllByOrderBySortOrderAsc();
}
