package com.javaexam.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.User;
import com.javaexam.entity.UserAnswer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class UserAnswerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserAnswerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    private UserAnswer mapUserAnswerWithJoin(ResultSet rs) throws SQLException {
        // Map User
        User user = new User();
        user.setId(rs.getString("user_id"));
        user.setUsername(rs.getString("username"));
        user.setDisplayName(rs.getString("display_name"));
        user.setRole(rs.getString("role"));
        
        // Map Chapter
        Chapter chapter = new Chapter();
        chapter.setId(rs.getString("chapter_id"));
        chapter.setChapterCode(rs.getString("chapter_code"));
        chapter.setTitle(rs.getString("chapter_title"));
        chapter.setSortOrder(rs.getInt("chapter_sort_order"));
        
        // Map Question
        Question question = new Question();
        question.setId(rs.getString("question_id"));
        question.setChapter(chapter);
        question.setQuestionText(rs.getString("question_text"));
        String optionsJson = rs.getString("options_json");
        question.setOptionsJson(optionsJson);
        question.setOptions(readOptions(optionsJson));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        
        // Map UserAnswer
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(rs.getString("id"));
        userAnswer.setUser(user);
        userAnswer.setChapter(chapter);
        userAnswer.setQuestion(question);
        userAnswer.setSelectedAnswer(rs.getString("selected_answer"));
        userAnswer.setIsCorrect(rs.getBoolean("is_correct"));
        Timestamp timestamp = rs.getTimestamp("answered_at");
        userAnswer.setAnsweredAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        return userAnswer;
    }

    private final RowMapper<UserAnswer> joinRowMapper = (rs, rowNum) -> mapUserAnswerWithJoin(rs);

    public List<UserAnswer> findByUserAndChapter(String userId, String chapterId) {
        String sql = """
            SELECT 
                ua.id, ua.user_id, ua.chapter_id, ua.question_id, 
                ua.selected_answer, ua.is_correct, ua.answered_at,
                u.username, u.display_name, u.role,
                c.chapter_code, c.title as chapter_title, c.sort_order as chapter_sort_order,
                q.question_text, q.options AS options_json, q.correct_answer
            FROM user_answers ua
            JOIN users u ON ua.user_id = u.id
            JOIN chapters c ON ua.chapter_id = c.id
            JOIN questions q ON ua.question_id = q.id
            WHERE ua.user_id = ? AND ua.chapter_id = ?
            ORDER BY ua.answered_at
            """;
        return jdbcTemplate.query(sql, joinRowMapper, userId, chapterId);
    }

    public void save(UserAnswer userAnswer) {
        jdbcTemplate.update(
                "INSERT INTO user_answers (id, user_id, chapter_id, question_id, selected_answer, is_correct, answered_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                userAnswer.getId(),
                userAnswer.getUser().getId(),
                userAnswer.getChapter().getId(),
                userAnswer.getQuestion().getId(),
                userAnswer.getSelectedAnswer(),
                userAnswer.getIsCorrect(),
                userAnswer.getAnsweredAt() != null ? Timestamp.valueOf(userAnswer.getAnsweredAt().truncatedTo(ChronoUnit.MILLIS)) : null);
    }

    public int deleteByUserId(String userId) {
        return jdbcTemplate.update("DELETE FROM user_answers WHERE user_id = ?", userId);
    }

    /**
     * Deletes all answers for a specific user and chapter.
     *
     * @return number of rows deleted
     */
    public int deleteByUserIdAndChapterId(String userId, String chapterId) {
        return jdbcTemplate.update(
                "DELETE FROM user_answers WHERE user_id = ? AND chapter_id = ?",
                userId,
                chapterId);
    }

    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM user_answers");
    }

    /**
     * Find distinct users who answered questions on a specific date.
     * 
     * @param date The date to search for (format: YYYY-MM-DD)
     * @return List of UserAnswer objects with user information
     */
    public List<UserAnswer> findUsersByAnswerDate(String date) {
        String sql = """
            SELECT DISTINCT
                ua.id, ua.user_id, ua.chapter_id, ua.question_id, 
                ua.selected_answer, ua.is_correct, ua.answered_at,
                u.username, u.display_name, u.role,
                c.chapter_code, c.title as chapter_title, c.sort_order as chapter_sort_order,
                q.question_text, q.options AS options_json, q.correct_answer
            FROM user_answers ua
            JOIN users u ON ua.user_id = u.id
            JOIN chapters c ON ua.chapter_id = c.id
            JOIN questions q ON ua.question_id = q.id
            WHERE DATE(ua.answered_at / 1000, 'unixepoch') = ?
            ORDER BY ua.answered_at DESC, u.username
            """;
        return jdbcTemplate.query(sql, joinRowMapper, date);
    }

    /**
     * Find users with their progress/scores who answered questions on a specific date.
     * Groups by user and chapter to get distinct user-chapter combinations.
     * 
     * @param date The date to search for (format: YYYY-MM-DD)
     * @return List of user answer data with score information
     */
    public List<Map<String, Object>> findUsersWithScoreByAnswerDate(String date) {
        String sql = """
            SELECT 
                u.id as user_id,
                u.username,
                u.display_name,
                c.id as chapter_id,
                c.chapter_code,
                c.title as chapter_title,
                up.score,
                up.passed,
                MIN(ua.answered_at) as answered_at
            FROM user_answers ua
            JOIN users u ON ua.user_id = u.id
            JOIN chapters c ON ua.chapter_id = c.id
            LEFT JOIN user_progress up ON ua.user_id = up.user_id AND ua.chapter_id = up.chapter_id
            WHERE DATE(ua.answered_at / 1000, 'unixepoch') = ?
            GROUP BY u.id, u.username, u.display_name, c.id, c.chapter_code, c.title, up.score, up.passed
            ORDER BY answered_at DESC, u.username
            """;
        return jdbcTemplate.queryForList(sql, date);
    }
}
