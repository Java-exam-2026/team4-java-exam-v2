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
import com.javaexam.dto.QuestionFormDto;
import com.javaexam.entity.ActionType;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.Question;
import com.javaexam.entity.TargetType;
import com.javaexam.entity.User;
import com.javaexam.repository.AuditLogJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.service.AuditLogChangeCalculator;
import com.javaexam.service.AuditLogChangeCalculatorRegistry;
import com.javaexam.security.CustomUserDetails;

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
 * - created_at: 操作の日時
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
        User currentUser = extractCurrentUser(auth);

        Object[] args = joinPoint.getArgs();
        String targetId = extractTargetId(args, log.target());
        Question afterQuestion = extractQuestion(args);
        QuestionFormDto questionForm = extractQuestionForm(args);
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
            auditLog.setTarget_id(resolveTargetId(log, targetId));
            auditLog.setTarget_name(resolveTargetName(log, afterQuestion, questionForm, beforeTarget));
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
                && (log.action() == ActionType.UPDATE || log.action() == ActionType.DELETE)
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

            QuestionFormDto questionForm = extractQuestionForm(args);
            if (questionForm != null && questionForm.getId() != null && !questionForm.getId().isBlank()) {
                return questionForm.getId();
            }

            for (Object arg : args) {
                if (arg instanceof String stringArg && !stringArg.isBlank()) {
                    return stringArg;
                }
            }
        }
        return null;
    }

    private String resolveTargetId(Log log, String targetId) {
        if (targetId != null && !targetId.isBlank()) {
            return targetId;
        }

        if (log.target() == TargetType.QUESTION && log.action() == ActionType.CREATE) {
            return "";
        }

        return "";
    }

    private String resolveTargetName(Log log, Question question, QuestionFormDto questionForm, Object beforeTarget) {
        if (log.target() != TargetType.QUESTION) {
            return "";
        }

        if (question != null && question.getQuestionText() != null) {
            return question.getQuestionText();
        }

        if (questionForm != null && questionForm.getQuestionText() != null) {
            return questionForm.getQuestionText();
        }

        if (beforeTarget instanceof Question beforeQuestion && beforeQuestion.getQuestionText() != null) {
            return beforeQuestion.getQuestionText();
        }

        return "";
    }

    private Question extractQuestion(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Question question) {
                return question;
            }
        }
        return null;
    }

    private QuestionFormDto extractQuestionForm(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof QuestionFormDto questionForm) {
                return questionForm;
            }
        }
        return null;
    }

    private User extractCurrentUser(Authentication auth) {
        if (auth == null) {
            throw new IllegalStateException("Authentication not found");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        if (principal instanceof User user) {
            return user;
        }

        throw new IllegalStateException("Unsupported authenticated principal: " + principal);
    }
}
