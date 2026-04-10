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
        
        // ★ status (String) を Enum に変換してセット
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            progress.setStatus(UserProgress.ProgressStatus.valueOf(statusStr));
        }

        progress.setLastAttemptedAt(toLocalDateTime(rs.getTimestamp("last_attempted_at")));
        progress.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at"))); // ★ 追加
        
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

    /**
     * 進捗状況を保存（挿入または更新）します。
     */
    public void save(UserProgress progress) {
        String sql = """
            INSERT INTO user_progress (
                id, user_id, chapter_id, score, passed, has_submitted, status, last_attempted_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(user_id, chapter_id) 
            DO UPDATE SET 
                score = EXCLUDED.score,
                passed = EXCLUDED.passed,
                has_submitted = EXCLUDED.has_submitted,
                status = EXCLUDED.status,
                last_attempted_at = EXCLUDED.last_attempted_at,
                updated_at = EXCLUDED.updated_at
            """;

        jdbcTemplate.update(
                sql,
                progress.getId(),
                progress.getUser().getId(),
                progress.getChapter().getId(),
                progress.getScore(),
                progress.getPassed(),
                progress.getHasSubmitted(),
                progress.getStatus().name(), // ★ Enumを文字列に
                toTimestamp(progress.getLastAttemptedAt()),
                toTimestamp(progress.getUpdatedAt()) // ★ 追加
        );
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime.truncatedTo(ChronoUnit.MILLIS)) : null;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    public List<UserProgress> findAll() {
        return jdbcTemplate.query("SELECT * FROM user_progress", rowMapper);
    }

    public int deleteByUserId(String userId) {
        return jdbcTemplate.update("DELETE FROM user_progress WHERE user_id = ?", userId);
    }

    public int deleteByUserIdAndChapterId(String userId, String chapterId) {
        return jdbcTemplate.update(
                "DELETE FROM user_progress WHERE user_id = ? AND chapter_id = ?",
                userId,
                chapterId);
    }

    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM user_progress");
    }
}