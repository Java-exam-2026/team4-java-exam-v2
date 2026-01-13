package com.javaexam.repository;

import com.javaexam.entity.Chapter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChapterJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Chapter> chapterRowMapper = (rs, rowNum) -> new Chapter(
            rs.getString("id"),
            rs.getString("chapter_code"),
            rs.getString("title"),
            rs.getInt("sort_order"));

    public ChapterJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Chapter> findAllOrdered() {
        return jdbcTemplate.query("SELECT id, chapter_code, title, sort_order FROM chapters ORDER BY sort_order", chapterRowMapper);
    }

    public Optional<Chapter> findByChapterCode(String chapterCode) {
        List<Chapter> chapters = jdbcTemplate.query(
                "SELECT id, chapter_code, title, sort_order FROM chapters WHERE chapter_code = ?",
                chapterRowMapper,
                chapterCode);
        return chapters.stream().findFirst();
    }

    public Optional<Chapter> findById(String id) {
        List<Chapter> chapters = jdbcTemplate.query(
                "SELECT id, chapter_code, title, sort_order FROM chapters WHERE id = ?",
                chapterRowMapper,
                id);
        return chapters.stream().findFirst();
    }

    /**
     * Saves a chapter (insert or update).
     * @param chapter the chapter to save
     * @return the number of rows affected
     */
    public int save(Chapter chapter) {
        if (chapter.getId() == null || chapter.getId().isEmpty()) {
            // Insert new chapter
            chapter.setId(java.util.UUID.randomUUID().toString());
            return jdbcTemplate.update(
                    "INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES (?, ?, ?, ?)",
                    chapter.getId(),
                    chapter.getChapterCode(),
                    chapter.getTitle(),
                    chapter.getSortOrder()
            );
        } else {
            // Update existing chapter
            return jdbcTemplate.update(
                    "UPDATE chapters SET chapter_code = ?, title = ?, sort_order = ? WHERE id = ?",
                    chapter.getChapterCode(),
                    chapter.getTitle(),
                    chapter.getSortOrder(),
                    chapter.getId()
            );
        }
    }

    /**
     * Deletes a chapter by ID.
     * @param id the chapter ID
     * @return the number of rows affected
     */
    public int deleteById(String id) {
        return jdbcTemplate.update("DELETE FROM chapters WHERE id = ?", id);
    }
}
