package com.javaexam.service;

import com.javaexam.dto.ChapterDto;
import com.javaexam.entity.Chapter;
import com.javaexam.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {

  @Autowired
  private ChapterRepository chapterRepository;

  public List<ChapterDto> getAllChapters() {
    List<Chapter> chapters = chapterRepository.findAllByOrderBySortOrderAsc();

    return chapters.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  private ChapterDto convertToDto(Chapter chapter) {
    return new ChapterDto(
        chapter.getId(),
        chapter.getChapterCode(),
        chapter.getTitle(),
        chapter.getSortOrder());
  }
}
