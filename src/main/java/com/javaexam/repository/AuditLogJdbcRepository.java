package com.javaexam.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class AuditLogJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditLogJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private final RowMapper<com.javaexam.entity.AuditLog> auditLogRowMapper = (rs, rowNum) -> {
        com.javaexam.entity.AuditLog log = new com.javaexam.entity.AuditLog();
        log.setId(rs.getString("id"));
        log.setActor_user_id(rs.getString("actor_user_id"));
        log.setActor_user_name(rs.getString("actor_user_name"));
        log.setActor_user_display_name(rs.getString("actor_user_display_name"));
        log.setTarget_type(com.javaexam.entity.TargetType.valueOf(rs.getString("target_type")));
        log.setTarget_id(rs.getString("target_id"));
        log.setTarget_name(rs.getString("target_name"));
        log.setAction_type(com.javaexam.entity.ActionType.valueOf(rs.getString("action_type")));
        log.setAction_status(rs.getBoolean("action_status"));
        log.setChanges_json(rs.getString("changes_json"));
        log.setAction_time(rs.getTimestamp("action_time").toLocalDateTime());
        return log;
    };

    /**
     * ログをデータベースに保存するメソッド。
     * @param log
     * @return 保存されたログのID
     */
    public String save(com.javaexam.entity.AuditLog log) {
        if (log.getId() == null || log.getId().isEmpty()) {
            log.setId(java.util.UUID.randomUUID().toString());
        }
        if (log.getAction_time() == null) {
            log.setAction_time(LocalDateTime.now());
        }

        jdbcTemplate.update(
                "INSERT INTO audit_logs (id, actor_user_id, actor_user_name, actor_user_display_name, " +
                "target_type, target_id, target_name, action_type, action_status, changes_json, action_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                log.getId(),
                log.getActor_user_id(),
                log.getActor_user_name(),
                log.getActor_user_display_name(),
                log.getTarget_type().name(),
                log.getTarget_id(),
                log.getTarget_name(),
                log.getAction_type().name(),
                log.getAction_status(),
                log.getChanges_json(),
                log.getAction_time()
        );
        return log.getId();
    }

    /**
     * 全ての監査ログを取得する
     */
    public List<com.javaexam.entity.AuditLog> findAll() {
        String sql = "SELECT * FROM audit_logs ORDER BY action_time DESC";
        return jdbcTemplate.query(sql, auditLogRowMapper);
    }

    /**
     * 指定されたターゲットIDの監査ログを取得する
     */
    public List<com.javaexam.entity.AuditLog> findByTargetId(String targetId) {
        String sql = "SELECT * FROM audit_logs WHERE target_id = ? ORDER BY action_time DESC";
        return jdbcTemplate.query(sql, new Object[]{targetId}, auditLogRowMapper);
    }

    /**
     * 指定されたIDの監査ログのchanges_jsonを更新する
     * @param auditLogId 監査ログのID
     * @param changesJson 変更内容のJSON
     */
    public void updateChangesJson(String auditLogId, String changesJson) {
        String sql = "UPDATE audit_logs SET changes_json = ? WHERE id = ?";
        jdbcTemplate.update(sql, changesJson, auditLogId);
    }
}

