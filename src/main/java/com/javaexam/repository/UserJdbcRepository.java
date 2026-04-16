package com.javaexam.repository;

import com.javaexam.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> mapUser(rs);

    public UserJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setDisplayName(rs.getString("display_name"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE username = ?",
                userRowMapper,
                username);
        return users.stream().findFirst();
    }

    public Optional<User> findById(String id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE id = ?",
                userRowMapper,
                id);
        return users.stream().findFirst();
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                username);
        return count != null && count > 0;
    }
    
    /**
     * システムに登録されている全ユーザーの総数を取得します。
     * データベースのusersテーブルに対してCOUNTクエリを実行し、
     * 登録済みのユーザーが何人いるかを数値（int）で返します。
     * * @return ユーザーの総数。データが存在しない場合は0を返します。
     */
    public int countUsers() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users",
                Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 新しいユーザー情報をデータベース（usersテーブル）に保存します。
     * IDが設定されていない場合は、UUID（重複しないランダムな文字列）を自動で発行して割り当てます。
     * パスワードや表示名などの基本情報に加え、権限はデフォルトで「ROLE_USER」として登録されます。
     * * @param user 保存したいユーザー情報のエンティティ。ID、ユーザー名、パスワード、表示名を含める必要があります。
     */
    public void save(User user) {
        // IDが空なら新しく発行する（簡易版）
        if (user.getId() == null) {
            user.setId(java.util.UUID.randomUUID().toString());
        }

        jdbcTemplate.update(
                "INSERT INTO users (id, username, password, display_name, role, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getDisplayName(),
                "ROLE_USER" // デフォルトの権限
        );
    }
}
