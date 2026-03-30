package com.javaexam.repository;


import com.javaexam.entity.PasswordResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class PasswordResetTokenJdbcRepository{
    
    private final JdbcTemplate jdbcTemplate;

    public PasswordResetTokenJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    public void save(String userId, String token, String expiryDate) {
        jdbcTemplate.update(
            "INSERT INTO password_reset_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)",
            userId,token,expiryDate
         );
    }
    public Optional<PasswordResetToken>findByToken(String token) {
        List<PasswordResetToken> result = jdbcTemplate.query(
            "SELECT * FROM password_reset_tokens WHERE token = ?",
            (rs, rowNum) -> {
                PasswordResetToken t = new PasswordResetToken();
                t.setUserId(rs.getString("user_id"));
                t.setToken(rs.getString("token"));
                t.setExpiryDate(rs.getString("expiry_date"));
                return t;
            },
            token
        );
        return result.stream().findFirst();
    }

    public void deleteByUserId(String userId) {
        jdbcTemplate.update(
            "DELETE FROM password_reset_tokens WHERE user_id = ?",
            userId   
        );
    }
}    
