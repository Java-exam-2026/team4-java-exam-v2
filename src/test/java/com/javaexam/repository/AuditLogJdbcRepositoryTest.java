package com.javaexam.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import com.javaexam.entity.ActionType;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.TargetType;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditLogJdbcRepository.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/audit-log-repository-test.db",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.sql.init.mode=never"
})
class AuditLogJdbcRepositoryTest {

    @Autowired
    private AuditLogJdbcRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS audit_logs (
                    id VARCHAR(36) PRIMARY KEY,
                    actor_user_id VARCHAR(36) NOT NULL,
                    actor_username VARCHAR(50) NOT NULL,
                    actor_display_name VARCHAR(100) NOT NULL,
                    target_type VARCHAR(20) NOT NULL CHECK (target_type IN ('USER', 'QUESTION')),
                    target_id VARCHAR(36) NOT NULL,
                    target_name TEXT NOT NULL,
                    action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('CREATE', 'UPDATE', 'DELETE')),
                    action_status BOOLEAN NOT NULL,
                    changes_json TEXT,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        jdbcTemplate.execute("DELETE FROM audit_logs");
    }

    @Test
    void save_shouldInsertAuditLogAndReturnGeneratedId() {
        AuditLog auditLog = new AuditLog();
        auditLog.setActor_user_id("user1");
        auditLog.setActor_username("testadmin1");
        auditLog.setActor_display_name("testadmin1");
        auditLog.setTarget_type(TargetType.QUESTION);
        auditLog.setTarget_id("question1");
        auditLog.setTarget_name("sample question");
        auditLog.setAction_type(ActionType.CREATE);
        auditLog.setAction_status(true);
        auditLog.setChanges_json("{\"before\":null,\"after\":\"testanswer\"}");

        String savedId = repository.save(auditLog);

        assertNotNull(savedId);

        List<AuditLog> auditLogs = repository.findAll();
        assertEquals(1, auditLogs.size());

        AuditLog savedLog = auditLogs.get(0);
        assertEquals(savedId, savedLog.getId());
        assertEquals("user1", savedLog.getActor_user_id());
        assertEquals("testadmin1", savedLog.getActor_username());
        assertEquals("testadmin1", savedLog.getActor_display_name());
        assertEquals(TargetType.QUESTION, savedLog.getTarget_type());
        assertEquals("question1", savedLog.getTarget_id());
        assertEquals("sample question", savedLog.getTarget_name());
        assertEquals(ActionType.CREATE, savedLog.getAction_type());
        assertEquals(true, savedLog.getAction_status());
        assertEquals("{\"before\":null,\"after\":\"testanswer\"}", savedLog.getChanges_json());
        assertNotNull(savedLog.getCreated_at());
    }
}
