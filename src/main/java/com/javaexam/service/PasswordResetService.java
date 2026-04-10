package com.javaexam.service;

import com.javaexam.entity.PasswordResetToken;
import com.javaexam.repository.PasswordResetTokenJdbcRepository;
import com.javaexam.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * パスワードリセット機能の処理を担当するサービスクラス
 * 
 * ＠author 川端理央
 */
@Service
public class PasswordResetService {

    /** パスワードリセットトークンのデータベース操作を行うリポジトリ */
    private final PasswordResetTokenJdbcRepository tokenRepository;

    /** ユーザ情報のデータベース操作を行うリポジトリ */
    private final UserJdbcRepository userRepository;

    /**
     * コンストラクタ
     * 
     * @param tokenRepository トークン操作用リポジトリ
     * @param userRepository ユーザー操作用リポジトリ
     */
    public PasswordResetService(
        PasswordResetTokenJdbcRepository tokenRepository,
        UserJdbcRepository userRepository) {
            this.tokenRepository = tokenRepository;
            this.userRepository = userRepository;
    }

    /**
     * パスワードリセット用トークンを生成してデータベースに保存する
     * ユーザーが存在しない場合は例外をスローする
     * 
     * @param userId　トークンを発行するユーザーのID
     * @throws RuntimeException 指定されたユーザーIDが存在しない場合
     */
    public void createToken(String userId) {

        boolean exists = userRepository.existsByUsername(userId);
        if (!exists) {
            throw new RuntimeException("このユーザーは存在しません");
        }

        String token = UUID.randomUUID().toString();

        String expriDate = LocalDateTime.now()
            .plusHours(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
         
        tokenRepository.save(userId, token, expriDate);
        
        System.out.println("リセットURL: /password/reset?token=" + token);
    }

    /**
     * トークンを確認してパスワードを更新する
     * トークンが無効または期限が切れ場合は例外をスローする
     * 
     * @param token　パスワードリセット用のトークン
     * @param newPassword　新しいパスワード
     * @throws RuntimeException トークンが無効または期限切れの場合
     */
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("無効なトークンです"));
        
        LocalDateTime expiry = LocalDateTime.parse(
            resetToken.getExpiryDate(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );    
        if (expiry.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("トークンの期限が切れています");
        }

        userRepository.updatePassword(resetToken.getUserId(), newPassword);
        tokenRepository.deleteByUserId(resetToken.getUserId());
    }
}
