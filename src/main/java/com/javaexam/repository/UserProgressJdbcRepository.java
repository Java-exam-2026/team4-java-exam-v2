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
import java.util.Map;
import java.util.Optional;

/**
 * ユーザーの学習進捗データを管理するリポジトリクラスです。
 * データベース（user_progressテーブル）への保存、取得、削除などの操作を担当します。
 */
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

    /**
     * 【単品注文用】
     * 検索結果（ResultSet）を UserProgress エンティティに変換します。
     * このメソッドは JOIN していない単純な SQL（SELECT *）の結果を処理する際に使用します。
     * ユーザー名やチャプター名が必要な場合は、他のリポジトリを使って追加で取得します。
     */
    private UserProgress mapUserProgress(ResultSet rs) throws SQLException {
        // ユーザーIDを使って、ユーザー情報の「本体」をDBに探しに行く（おつかい1回目）
        User user = userJdbcRepository.findById(rs.getString("user_id"))
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // チャプターIDを使って、チャプター情報の「本体」をDBに探しに行く（おつかい2回目）
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

    /** 共通の変換ルール（単品用） */
    private final RowMapper<UserProgress> rowMapper = (rs, rowNum) -> mapUserProgress(rs);

    /**
     * 全ユーザーの学習進捗データを、ユーザー情報およびチャプター情報を含めて一括取得します。
     * * @return ユーザー情報とチャプター情報がセットされた UserProgress エンティティのリスト
     */
    public List<UserProgress> findAll() {
        // 【修正ポイント】具体的な実装の詳細（N+1問題の回避など）はここに書く
        // JOINを使用して1つのクエリで全ての関連データを取得することで、
        // ループ内で個別にデータを取り直す「N+1問題」を回避し、高速に動作させています。
        String sql = "SELECT up.*, u.username, u.display_name, c.chapter_code, c.title " +
                "FROM user_progress up " +
                "JOIN users u ON up.user_id = u.id " +
                "JOIN chapters c ON up.chapter_id = c.id";

        // 取得した「巨大な1行」から、Javaの各オブジェクトを組み立てる
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            // 1. ユーザー情報を組み立てる（JOINした列から直接取る）
            User user = new User();
            user.setId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setDisplayName(rs.getString("display_name"));

            // 2. チャプター情報を組み立てる（JOINした列から直接取る）
            Chapter chapter = new Chapter();
            chapter.setId(rs.getString("chapter_id"));
            chapter.setChapterCode(rs.getString("chapter_code"));
            chapter.setTitle(rs.getString("title"));

            // 3. 進捗情報を組み立てて、上に作った2つをセットする
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
        });
    }

    /** 指定されたユーザーの進捗一覧を取得します */
    public List<UserProgress> findByUser(User user) {
        return jdbcTemplate.query("SELECT * FROM user_progress WHERE user_id = ?", rowMapper, user.getId());
    }

    /** 特定のユーザーとチャプターに紐づく進捗を1件取得します */
    public Optional<UserProgress> findByUserAndChapter(String userId, String chapterId) {
        List<UserProgress> progressList = jdbcTemplate.query(
                "SELECT * FROM user_progress WHERE user_id = ? AND chapter_id = ?",
                rowMapper, userId, chapterId);
        return progressList.stream().findFirst();
    }

    /**
     * 進捗データを保存（または更新）します。
     * すでにIDが存在すればUPDATE、なければINSERTを行います。
     */
    public void save(UserProgress progress) {
        // まず更新（UPDATE）を試みる
        int updated = jdbcTemplate.update(
                "UPDATE user_progress SET score = ?, passed = ?, has_submitted = ?, last_attempted_at = ? WHERE id = ?",
                progress.getScore(), progress.getPassed(), progress.getHasSubmitted(),
                toTimestamp(progress.getLastAttemptedAt()), progress.getId());

        // 更新件数が0件なら、新規登録（INSERT）を行う
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO user_progress (id, user_id, chapter_id, score, passed, has_submitted, last_attempted_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    progress.getId(), progress.getUser().getId(), progress.getChapter().getId(),
                    progress.getScore(), progress.getPassed(), progress.getHasSubmitted(),
                    toTimestamp(progress.getLastAttemptedAt()));
        }
    }

    /** LocalDateTime を DB用の Timestamp 形式に変換します（ミリ秒以下は切り捨て） */
    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime.truncatedTo(ChronoUnit.MILLIS)) : null;
    }

    /** 指定ユーザーの全進捗データを削除します */
    public int deleteByUserId(String userId) {
        return jdbcTemplate.update("DELETE FROM user_progress WHERE user_id = ?", userId);
    }

    /** 特定の進捗データを削除します */
    public int deleteByUserIdAndChapterId(String userId, String chapterId) {
        return jdbcTemplate.update("DELETE FROM user_progress WHERE user_id = ? AND chapter_id = ?", userId, chapterId);
    }

    /** 全進捗データを削除します */
    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM user_progress");
    }

    /** 指定された期間内に更新された進捗データの件数をカウントします */
    public int countByLastAttemptedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM user_progress WHERE last_attempted_at >= ? AND last_attempted_at <= ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, toTimestamp(start), toTimestamp(end));
        // もし結果がnullだったら0を返す（これで落ちなくなる！）
        return count != null ? count : 0;
    }

    /** 合格、または不合格のデータの総数をカウントします */
    public int countByPassed(boolean passed) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_progress WHERE passed = ?",
                Integer.class, passed);
        return count != null ? count : 0; // ここも null ガード！
    }

    /**
     * チャプターごとの試験挑戦回数を集計します。
     * 
     * @return チャプタータイトルと件数のマップリスト
     */
    public List<Map<String, Object>> countAttemptsByChapter() {
        String sql = "SELECT c.title, COUNT(up.id) as attempt_count " +
                "FROM user_progress up " +
                "JOIN chapters c ON up.chapter_id = c.id " +
                "GROUP BY c.title";
        return jdbcTemplate.queryForList(sql);
    }
}