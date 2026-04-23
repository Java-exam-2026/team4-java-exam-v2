package com.javaexam.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.javaexam.entity.ActionType;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.TargetType;

@JdbcTest
@Import(AuditLogJdbcRepository.class)
class AuditLogJdbcRepositoryTest {

    @Autowired
    private AuditLogJdbcRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM audit_logs");
    }

    @Test
    void save_shouldInsertAuditLogAndReturnGeneratedId() {
        AuditLog auditLog = new AuditLog();
        auditLog.setActor_user_id("user-1");
        auditLog.setActor_username("admin");
        auditLog.setActor_display_name("管理者");
        auditLog.setTarget_type(TargetType.QUESTION);
        auditLog.setTarget_id("question-1");
        auditLog.setTarget_name("サンプル問題");
        auditLog.setAction_type(ActionType.CREATE);
        auditLog.setAction_status(true);
        auditLog.setChanges_json("{\"before\":null,\"after\":{}}");

        String savedId = repository.save(auditLog);

        assertNotNull(savedId);

        List<AuditLog> auditLogs = repository.findAll();
        assertEquals(1, auditLogs.size());

        AuditLog savedLog = auditLogs.get(0);
        assertEquals(savedId, savedLog.getId());
        assertEquals("user-1", savedLog.getActor_user_id());
        assertEquals("admin", savedLog.getActor_username());
        assertEquals("管理者", savedLog.getActor_display_name());
        assertEquals(TargetType.QUESTION, savedLog.getTarget_type());
        assertEquals("question-1", savedLog.getTarget_id());
        assertEquals("サンプル問題", savedLog.getTarget_name());
        assertEquals(ActionType.CREATE, savedLog.getAction_type());
        assertEquals(true, savedLog.getAction_status());
        assertEquals("{\"before\":null,\"after\":{}}", savedLog.getChanges_json());
        assertNotNull(savedLog.getCreated_at());
    }
}
