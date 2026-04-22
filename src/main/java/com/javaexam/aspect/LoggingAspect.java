package com.javaexam.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.javaexam.annotation.Log;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.User;
import com.javaexam.repository.AuditLogJdbcRepository;
import com.javaexam.service.AuditLogChangeCalculatorRegistry;


/**
 * 監査ログをDBに保存するAspect
 * @Logアノテーションが付与されたメソッドの実行時にログを記録する
 */
@Aspect
@Component
public class LoggingAspect {
    private final Logger logger;
    private final AuditLogJdbcRepository auditLogRepository;
    private final AuditLogChangeCalculatorRegistry calculatorRegistry;

    public LoggingAspect(AuditLogJdbcRepository auditLogRepository,
                         AuditLogChangeCalculatorRegistry calculatorRegistry) {
        this.logger = LoggerFactory.getLogger(LoggingAspect.class);
        this.auditLogRepository = auditLogRepository;
        this.calculatorRegistry = calculatorRegistry;
    }

    @After("@annotation(log)")
    public void logAdminAction(JoinPoint joinPoint, Log log) {
        try {
            // 現在のユーザー情報を取得
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();

            // AuditLogを作成
            AuditLog auditLog = new AuditLog();
            auditLog.setActor_user_id(currentUser.getId());
            auditLog.setActor_user_name(currentUser.getUsername());
            auditLog.setActor_user_display_name(currentUser.getDisplayName());
            auditLog.setTarget_type(log.target());
            auditLog.setAction_type(log.action());
            auditLog.setAction_status(true);

            Object[] args = joinPoint.getArgs();
            String targetId = extractTargetId(args, log.target());
            auditLog.setTarget_id(targetId);

            // DBに保存（changes_json はここでは null）
            String auditLogId = auditLogRepository.save(auditLog);

            logger.info("Audit log saved: action={}, target={}, userId={}, auditLogId={}",
                log.action(), log.target(), currentUser.getId(), auditLogId);
        } catch (Exception e) {
            logger.error("Failed to save audit log", e);
        }
    }

    /**
     * 引数からターゲットIDを抽出する
     */
    private String extractTargetId(Object[] args, Object target) {
        for (Object arg : args) {
            if (arg != null && arg.getClass().getSimpleName().equals("Question")) {
                try {
                    return (String) arg.getClass().getMethod("getId").invoke(arg);
                } catch (Exception e) {
                    logger.warn("Failed to extract Question ID", e);
                }
            }
        }
        return null;
    }
}
