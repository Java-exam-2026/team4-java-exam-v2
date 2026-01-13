package com.javaexam.repository;

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
import java.util.List;

@Repository
public class UserAnswerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserJdbcRepository userJdbcRepository;
    private final ChapterJdbcRepository chapterJdbcRepository;
    private final QuestionJdbcRepository questionJdbcRepository;

    public UserAnswerJdbcRepository(JdbcTemplate jdbcTemplate,
                                    UserJdbcRepository userJdbcRepository,
                                    ChapterJdbcRepository chapterJdbcRepository,
                                    QuestionJdbcRepository questionJdbcRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userJdbcRepository = userJdbcRepository;
        this.chapterJdbcRepository = chapterJdbcRepository;
        this.questionJdbcRepository = questionJdbcRepository;
    }

    private UserAnswer mapUserAnswer(ResultSet rs) throws SQLException {
        User user = userJdbcRepository.findById(rs.getString("user_id"))
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Chapter chapter = chapterJdbcRepository.findById(rs.getString("chapter_id"))
                .orElseThrow(() -> new IllegalStateException("Chapter not found"));
        Question question = questionJdbcRepository.findById(rs.getString("question_id"))
                .orElseThrow(() -> new IllegalStateException("Question not found"));
        
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

    private final RowMapper<UserAnswer> rowMapper = (rs, rowNum) -> mapUserAnswer(rs);

    public List<UserAnswer> findByUserAndChapter(String userId, String chapterId) {
        return jdbcTemplate.query(
                "SELECT * FROM user_answers WHERE user_id = ? AND chapter_id = ? ORDER BY answered_at",
                rowMapper,
                userId,
                chapterId);
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

    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM user_answers");
    }
}
