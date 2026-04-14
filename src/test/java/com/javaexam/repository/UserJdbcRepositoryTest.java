package com.javaexam.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.javaexam.entity.User;

@JdbcTest


public class UserJdbcRepositoryTest {
    @Autowired
    private UserJdbcRepository userJdbcRepository;

    @Autowired
    private JdbcTemplate JdbcTemplate;

    

    @BeforeEach
    void setUp() {
        JdbcTemplate.execute("DELETE FROM users");
        JdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        JdbcTemplate.execute("ALTER TABLE user_answers ALTER COLUMN id RESTART WITH 1");
        JdbcTemplate.execute("ALTER TABLE user_progress ALTER COLUMN id RESTART WITH 1");
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
