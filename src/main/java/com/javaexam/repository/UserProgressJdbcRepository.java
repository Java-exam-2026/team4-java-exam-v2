package com.javaexam.repository;

import com.javaexam.entity.Chapter;
import com.javaexam.entity.User;
import com.javaexam.entity.UserProgress;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Repository
public class UserProgressJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserJdbcRepository userJdbcRepository;
    private final ChapterJdbcRepository chapterJdbcRepository;

    public UserProgressJdbcRepository(JdbcTemplate jdbcTemplate,
                                      UserJdbcRepository userJdbcRepository,
                                      ChapterJdbcRepository chapterJdbcRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userJdbcRepository = userJdbcRepository;
        this.chapterJdbcRepository = chapterJdbcRepository;
    }

    private UserProgress mapUserProgress(ResultSet rs) throws SQLException {
        User user = userJdbcRepository.findById(rs.getString("user_id"))
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Chapter chapter = chapterJdbcRepository.findById(rs.getString("chapter_id"))
                .orElseThrow(() -> new IllegalStateException("Chapter not found"));
        UserProgress progress = new UserProgress();
        progress.setId(rs.getString("id"));
        progress.setUser(user);
        progress.setChapter(chapter);
        progress.setScore(rs.getInt("score"));
        progress.setPassed(rs.getBoolean("passed"));
        progress.setHasSubmitted(rs.getBoolean("has_submitted"));
        Timestamp timestamp = rs.getTimestamp("last_attempted_at");
        progress.setLastAttemptedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        return progress;
    }

    private final RowMapper<UserProgress> rowMapper = (rs, rowNum) -> mapUserProgress(rs);

    public List<UserProgress> findByUser(User user) {
        return jdbcTemplate.query(
                "SELECT * FROM user_progress WHERE user_id = ?",
                rowMapper,
                user.getId());
    }

    public Optional<UserProgress> findByUserAndChapter(String userId, String chapterId) {
        List<UserProgress> progressList = jdbcTemplate.query(
                "SELECT * FROM user_progress WHERE user_id = ? AND chapter_id = ?",
                rowMapper,
                userId,
                chapterId);
        return progressList.stream().findFirst();
    }

    public void save(UserProgress progress) {
        int updated = jdbcTemplate.update(
                "UPDATE user_progress SET score = ?, passed = ?, has_submitted = ?, last_attempted_at = ? WHERE id = ?",
                progress.getScore(),
                progress.getPassed(),
                progress.getHasSubmitted(),
                toTimestamp(progress.getLastAttemptedAt()),
                progress.getId());
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO user_progress (id, user_id, chapter_id, score, passed, has_submitted, last_attempted_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    progress.getId(),
                    progress.getUser().getId(),
                    progress.getChapter().getId(),
                    progress.getScore(),
                    progress.getPassed(),
                    progress.getHasSubmitted(),
                    toTimestamp(progress.getLastAttemptedAt()));
        }
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime.truncatedTo(ChronoUnit.MILLIS)) : null;
    }

    public List<UserProgress> findAll() {
        return jdbcTemplate.query("SELECT * FROM user_progress", rowMapper);
    }
}
