package com.javaexam.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.javaexam.entity.User;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserJdbcRepository.class)
class UserJdbcRepositoryTest {
    @Autowired
    private UserJdbcRepository userJdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM user_answers");
        jdbcTemplate.execute("DELETE FROM user_progress");
        jdbcTemplate.execute("DELETE FROM users");
    }

    

    /**
     * UserをDBにセーブするメソッドのテストメソッド。
     * 正しくセーブされることを確認する。
     */
    @Test
    void testSave() {
        User user = new User();
        user.setUsername("user1");
        user.setDisplayName("ユーザー1");
        user.setPassword("hashed-password");
        user.setRole("ROLE_USER");

        userJdbcRepository.save(user);

        User savedUser = userJdbcRepository.findByUsername("user1").orElseThrow();
        assertEquals("user1", savedUser.getUsername());
        assertEquals("ユーザー1", savedUser.getDisplayName());
        assertEquals("hashed-password", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
    }
}
