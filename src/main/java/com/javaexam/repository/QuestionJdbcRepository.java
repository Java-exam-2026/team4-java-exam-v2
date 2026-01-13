package com.javaexam.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.QuestionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class QuestionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ChapterJdbcRepository chapterJdbcRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionJdbcRepository(JdbcTemplate jdbcTemplate, ChapterJdbcRepository chapterJdbcRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.chapterJdbcRepository = chapterJdbcRepository;
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Chapter chapter = chapterJdbcRepository.findById(rs.getString("chapter_id"))
                .orElseThrow(() -> new IllegalStateException("Chapter not found for question"));
        Question question = new Question();
        question.setId(rs.getString("id"));
        question.setChapter(chapter);
        question.setQuestionText(rs.getString("question_text"));
        question.setQuestionType(QuestionType.valueOf(rs.getString("question_type")));
        String optionsJson = rs.getString("options");
        question.setOptionsJson(optionsJson);
        question.setOptions(readOptions(optionsJson));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        question.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return question;
    }

    private Map<String, String> readOptions(String json) {
        if (json == null) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize options", e);
        }
    }

    private final RowMapper<Question> questionRowMapper = (rs, rowNum) -> mapQuestion(rs);

    public List<Question> findRandomByChapterId(String chapterId, int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM questions WHERE chapter_id = ? ORDER BY created_at ASC LIMIT ?",
                questionRowMapper,
                chapterId,
                limit);
    }

    public Optional<Question> findById(String id) {
        List<Question> questions = jdbcTemplate.query(
                "SELECT * FROM questions WHERE id = ?",
                questionRowMapper,
                id);
        return questions.stream().findFirst();
    }

    public List<Question> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM questions ORDER BY created_at ASC",
                questionRowMapper);
    }

    /**
     * Saves a question (insert or update).
     * @param question the question to save
     * @return the number of rows affected
     */
    public int save(Question question) {
        try {
            String optionsJson = objectMapper.writeValueAsString(question.getOptions());
            
            if (question.getId() == null || question.getId().isEmpty()) {
                // Insert new question
                question.setId(java.util.UUID.randomUUID().toString());
                return jdbcTemplate.update(
                        "INSERT INTO questions (id, chapter_id, question_text, options, question_type, correct_answer, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                        question.getId(),
                        question.getChapter().getId(),
                        question.getQuestionText(),
                        optionsJson,
                        question.getQuestionType().name(),
                        question.getCorrectAnswer()
                );
            } else {
                // Update existing question
                return jdbcTemplate.update(
                        "UPDATE questions SET chapter_id = ?, question_text = ?, options = ?, question_type = ?, " +
                        "correct_answer = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                        question.getChapter().getId(),
                        question.getQuestionText(),
                        optionsJson,
                        question.getQuestionType().name(),
                        question.getCorrectAnswer(),
                        question.getId()
                );
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize options", e);
        }
    }

    /**
     * Deletes a question by ID.
     * @param id the ID of the question to delete
     * @return the number of rows affected
     */
    public int deleteById(String id) {
        return jdbcTemplate.update("DELETE FROM questions WHERE id = ?", id);
    }
}
