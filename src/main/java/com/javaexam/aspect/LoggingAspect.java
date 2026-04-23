package com.javaexam.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.javaexam.annotation.Log;
import com.javaexam.entity.ActionType;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.Question;
import com.javaexam.entity.TargetType;
import com.javaexam.entity.User;
import com.javaexam.repository.AuditLogJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.service.AuditLogChangeCalculator;
import com.javaexam.service.AuditLogChangeCalculatorRegistry;

/**
 * 監査ログをDBに保存するAspect
 * @Logアノテーションが付与されたメソッドの実行時にログを記録する
 * 記載内容:
 * - actor_user_id: 操作を行ったユーザーのID
 * - actor_username: 操作を行ったユーザーのユーザー名
 * - actor_display_name: 操作を行ったユーザーの表示名
 * - target_type: 操作対象のタイプ（例: QUESTION, USER）
 * - action_type: 操作の種類（例: CREATE, UPDATE, DELETE）
 * - action_status: 操作の成功/失敗
 * - target_id: 操作対象のID（例: QuestionのID）
 * - target_name: 操作対象の名前（例: Questionのテキスト）
 * - changes_json: 変更内容のJSON（UPDATEの場合のみ）
 * 変更内容のJSONは、変更前と変更後の値を含む形式で記録する
 */
@Aspect
@Component
public class LoggingAspect {
    private final Logger logger;
    private final AuditLogJdbcRepository auditLogRepository;
    private final AuditLogChangeCalculatorRegistry calculatorRegistry;
    private final QuestionJdbcRepository questionJdbcRepository;

    public LoggingAspect(AuditLogJdbcRepository auditLogRepository,
                         AuditLogChangeCalculatorRegistry calculatorRegistry,
                         QuestionJdbcRepository questionJdbcRepository) {
        this.logger = LoggerFactory.getLogger(LoggingAspect.class);
        this.auditLogRepository = auditLogRepository;
        this.calculatorRegistry = calculatorRegistry;
        this.questionJdbcRepository = questionJdbcRepository;
    }

    @Around("@annotation(log)")
    public Object logAdminAction(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Object[] args = joinPoint.getArgs();
        String targetId = extractTargetId(args, log.target());
        Question afterQuestion = extractQuestion(args);
        Object beforeTarget = loadBeforeTarget(log, targetId);

        Object result = joinPoint.proceed();

        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setActor_user_id(currentUser.getId());
            auditLog.setActor_username(currentUser.getUsername());
            auditLog.setActor_display_name(currentUser.getDisplayName());
            auditLog.setTarget_type(log.target());
            auditLog.setAction_type(log.action());
            auditLog.setAction_status(true);
            auditLog.setTarget_id(targetId);
            auditLog.setTarget_name(extractTargetName(log.target(), afterQuestion));
            auditLog.setChanges_json(calculateChanges(log, beforeTarget, afterQuestion));

            String auditLogId = auditLogRepository.save(auditLog);
            logger.info("Audit log saved: action={}, target={}, userId={}, auditLogId={}",
                    log.action(), log.target(), currentUser.getId(), auditLogId);
        } catch (Exception e) {
            logger.error("ログの取得に失敗しました。", e);
        }

        return result;
    }

    private Object loadBeforeTarget(Log log, String targetId) {
        if (log.target() == TargetType.QUESTION
                && log.action() == ActionType.UPDATE
                && targetId != null
                && !targetId.isBlank()) {
            return questionJdbcRepository.findById(targetId).orElse(null);
        }
        return null;
    }

    private String calculateChanges(Log log, Object before, Object after) {
        if (log.action() != ActionType.UPDATE) {
            return null;
        }

        AuditLogChangeCalculator calculator = calculatorRegistry.getCalculator(log.target().name());
        if (calculator == null) {
            return null;
        }

        return calculator.calculateChanges(before, after);
    }

    private String extractTargetId(Object[] args, TargetType targetType) {
        if (targetType == TargetType.QUESTION) {
            Question question = extractQuestion(args);
            if (question != null) {
                return question.getId();
            }
        }
        return null;
    }

    private String extractTargetName(TargetType targetType, Question question) {
        if (targetType == TargetType.QUESTION && question != null) {
            return question.getQuestionText();
        }
        return null;
    }

    private Question extractQuestion(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Question question) {
                return question;
            }
        }
        return null;
    }
}
