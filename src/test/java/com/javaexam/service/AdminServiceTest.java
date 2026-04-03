package com.javaexam.service;

import com.javaexam.entity.Chapter;
import com.javaexam.entity.User;
import com.javaexam.entity.UserProgress;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.UserJdbcRepository;
import com.javaexam.repository.UserProgressJdbcRepository;
import com.javaexam.service.AdminService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional // テストが終わったら掃除してくれる魔法
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserJdbcRepository userRepository;

    @Autowired
    private ChapterJdbcRepository chapterRepository;

    @Autowired
    private UserProgressJdbcRepository progressRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCalculateStatsCorrectlyWithTwoPassesAndOneFail() {
        // Repositoryを直すのが面倒なときは、直接SQLで消すのが早いです！
        jdbcTemplate.execute("DELETE FROM user_progress");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM chapters");
        // チャプターコードを毎回違うもの（例：TEST_AOI）に変えてみる
        Chapter ch = new Chapter();
        ch.setChapterCode("TEST_AOI_" + System.currentTimeMillis()); // 実行するたびに違う名前になる
        ch.setTitle("テストチャプター");
        ch.setSortOrder(1);
        chapterRepository.save(ch);

        // 3人のユーザーが進捗を作ったことにする
        setupProgress("user1", ch, 80, true); // 合格
        setupProgress("user2", ch, 90, true); // 合格
        setupProgress("user3", ch, 50, false); // 不合格

        // 2. 【実行】集計メソッドを呼ぶ！
        Map<String, Integer> stats = adminService.getPassFailStats();

        // 3. 【検証】計算結果は合ってるかな？
        assertEquals(2, stats.get("pass"), "合格者は2人のはず");
        assertEquals(1, stats.get("fail"), "不合格者は1人のはず");
    }

    private void setupProgress(String username, Chapter ch, int score, boolean passed) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pass");
        // ★ここを追加！ 表示名が空だとデータベースに怒られることが多いです
        user.setDisplayName(username + "さん");
        userRepository.save(user);

        UserProgress progress = new UserProgress();
        progress.setUser(user);
        progress.setChapter(ch);
        progress.setScore(score);
        progress.setPassed(passed);
        progress.setLastAttemptedAt(LocalDateTime.now());
        progress.setHasSubmitted(true);
        // ここも！ progressRepository.insert(progress); など
        progressRepository.save(progress);
    }

    @Test
    @DisplayName("統計データが0件の時、エラーにならずに0が返ること")
    void testGetStatsWhenDataIsEmpty() {
        // 1. 準備：確実に「0件」の状態にする
        // 外部キー制約（親子関係）がある場合、子（progress）から先に消すのがルール！
        jdbcTemplate.execute("DELETE FROM user_progress");
        jdbcTemplate.execute("DELETE FROM users");

        // 2. 実行：統計取得メソッドを呼ぶ
        Map<String, Integer> stats = adminService.getPassFailStats();
        int monthlyCount = adminService.getMonthlyAttemptCount();

        // 3. 検証：結果がちゃんと「0」になっているか
        assertEquals(0, stats.get("pass"), "合格数は0であること");
        assertEquals(0, stats.get("fail"), "不合格数は0であること");
        assertEquals(0, monthlyCount, "今月の受験数は0であること");
    }

    @Test
    @DisplayName("10人中8人合格のとき、合格率が80パーセントになること")
    void testCalculatePassRate() {
        // 1. 準備：10人分のデータを作成
        jdbcTemplate.execute("DELETE FROM user_progress");
        jdbcTemplate.execute("DELETE FROM users");

        // チャプターを1つ用意
        Chapter ch = new Chapter();
        ch.setChapterCode("RATE_TEST_" + System.currentTimeMillis());
        ch.setTitle("計算テスト");
        ch.setSortOrder(1); // ★ここを追加！適当な数字でOKです
        chapterRepository.save(ch);

        // 8人合格させる
        for (int i = 1; i <= 8; i++) {
            setupProgress("pass_user" + i, ch, 100, true);
        }
        // 2人不合格にする
        for (int i = 1; i <= 2; i++) {
            setupProgress("fail_user" + i, ch, 30, false);
        }

        // 2. 実行：統計を取る
        Map<String, Integer> stats = adminService.getPassFailStats();
        int pass = stats.get("pass");
        int fail = stats.get("fail");
        int total = pass + fail;

        // 3. 検証：ここが重要！
        // (double) をつけて「小数」として計算しないと、Javaでは 8 / 10 = 0 になっちゃいます
        double passRate = (double) pass / total * 100;

        assertEquals(80.0, passRate, "合格率は80.0%であること");
    }
}