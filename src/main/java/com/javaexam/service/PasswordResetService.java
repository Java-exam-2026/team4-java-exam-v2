package com.javaexam.service;

import com.javaexam.entity.PasswordResetToken;
import com.javaexam.repository.PasswordResetTokenJdbcRepository;
import com.javaexam.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenJdbcRepository tokenRepository;
    private final UserJdbcRepository userRepository;

    public PasswordResetService(
        PasswordResetTokenJdbcRepository tokenRepository,
        UserJdbcRepository userRepository) {
            this.tokenRepository = tokenRepository;
            this.userRepository = userRepository;
    }

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
