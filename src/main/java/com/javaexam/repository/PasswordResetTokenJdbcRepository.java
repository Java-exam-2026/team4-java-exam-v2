package com.javaexam.repository;


import com.javaexam.entity.PasswordResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * パスワードリセットトークンデータベース操作を担当するリポジトリクラス
 * 
 * ＠author 川端理央
 */
@Repository
public class PasswordResetTokenJdbcRepository{
    
    /** データベース操作を行うJdbcTemplateのインスタンス */
    private final JdbcTemplate jdbcTemplate;

    /**
     * コンストラクタ
     * 
     * @param jdbcTemplate データベース操作に使用するJdbcTemplate
     */
    public PasswordResetTokenJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    /**
     * パスワードリセットトークンをデータベースに保存する
     * 
     * @param userId　トークンを発行するユーザーのID
     * @param token　生成したリセット用トークン
     * @param expiryDate　トークンの有効期限
     */
    public void save(String userId, String token, String expiryDate) {
        jdbcTemplate.update(
            "INSERT INTO password_reset_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)",
            userId,token,expiryDate
         );
    }

    /**
     * トークン文字列でパスワードリセットトークンを検索する
     * 
     * @param token　検索するトークン文字列
     * @return 見つかったトークン情報（存在しない場合はEmpty)
     */
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

    /**
     * ユーザーIDに紐づくパスワードリセットトークンを削除する
     * 
     * @param userId　削除対象のユーザーID
     */
    public void deleteByUserId(String userId) {
        jdbcTemplate.update(
            "DELETE FROM password_reset_tokens WHERE user_id = ?",
            userId   
        );
    }
}    
