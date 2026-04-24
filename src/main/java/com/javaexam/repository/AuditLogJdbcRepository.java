package com.javaexam.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.javaexam.entity.ActionType;
import com.javaexam.entity.AuditLog;
import com.javaexam.entity.TargetType;
import com.javaexam.dto.AuditLogSearchForm;

@Repository
public class AuditLogJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuditLogJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AuditLog> auditLogRowMapper = (rs, rowNum) -> {
        AuditLog log = new AuditLog();
        log.setId(rs.getString("id"));
        log.setActor_user_id(rs.getString("actor_user_id"));
        log.setActor_username(rs.getString("actor_username"));
        log.setActor_display_name(rs.getString("actor_display_name"));
        log.setTarget_type(TargetType.valueOf(rs.getString("target_type")));
        log.setTarget_id(rs.getString("target_id"));
        log.setTarget_name(rs.getString("target_name"));
        log.setAction_type(ActionType.valueOf(rs.getString("action_type")));
        log.setAction_status(rs.getBoolean("action_status"));
        log.setChanges_json(rs.getString("changes_json"));
        String createdAt = rs.getString("created_at");
        if (createdAt != null) {
            // SQLite stores as TEXT; support both 'yyyy-MM-dd HH:mm:ss' and ISO-8601 'T' format
            log.setCreated_at(LocalDateTime.parse(
                createdAt.replace(" ", "T"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return log;
    };

    /**
     * ログをデータベースに保存するメソッド。
     * 
     * @param log
     * @return 保存されたログのID
     */
    public String save(AuditLog log) {
        if (log.getId() == null || log.getId().isEmpty()) {
            log.setId(java.util.UUID.randomUUID().toString());
        }
        if (log.getCreated_at() == null) {
            log.setCreated_at(LocalDateTime.now());
        }

        jdbcTemplate.update(
                "INSERT INTO audit_logs (id, actor_user_id, actor_username, actor_display_name, " +
                        "target_type, target_id, target_name, action_type, action_status, changes_json, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                log.getId(),
                log.getActor_user_id(),
                log.getActor_username(),
                log.getActor_display_name(),
                log.getTarget_type().name(),
                log.getTarget_id(),
                log.getTarget_name(),
                log.getAction_type().name(),
                log.getAction_status(),
                log.getChanges_json(),
                log.getCreated_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return log.getId();
    }

    /**
     * 全ての監査ログを取得する
     */
    public List<AuditLog> findAll() {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, auditLogRowMapper);
    }

    public List<AuditLog> findPage(int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        int offset = (safePage - 1) * safePageSize;

        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, auditLogRowMapper, safePageSize, offset);
    }

    public long countAll() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM audit_logs", Long.class);
        return count != null ? count : 0L;
    }

    public List<AuditLog> search(AuditLogSearchForm form) {
        QuerySpec spec = buildSearchSpec(form, false);
        return jdbcTemplate.query(spec.sql(), auditLogRowMapper, spec.args().toArray());
    }

    public long countBySearch(AuditLogSearchForm form) {
        QuerySpec spec = buildSearchSpec(form, true);
        Long count = jdbcTemplate.queryForObject(spec.sql(), Long.class, spec.args().toArray());
        return count != null ? count : 0L;
    }

    /**
     * 指定されたターゲットIDの監査ログを取得する
     */
    public List<AuditLog> findByTargetId(String targetId) {
        String sql = "SELECT * FROM audit_logs WHERE target_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new Object[] { targetId }, auditLogRowMapper);
    }

    /**
     * 指定されたIDの監査ログのchanges_jsonを更新する
     * 
     * @param auditLogId  監査ログのID
     * @param changesJson 変更内容のJSON
     */
    public void updateChangesJson(String auditLogId, String changesJson) {
        String sql = "UPDATE audit_logs SET changes_json = ? WHERE id = ?";
        jdbcTemplate.update(sql, changesJson, auditLogId);
    }

    private QuerySpec buildSearchSpec(AuditLogSearchForm form, boolean countQuery) {
        List<String> conditions = new ArrayList<>();
        List<Object> args = new ArrayList<>();

        addLikeCondition(conditions, args, "actor_user_id", form.getActorUserId());
        addLikeCondition(conditions, args, "actor_username", form.getActorUsername());
        addLikeCondition(conditions, args, "actor_display_name", form.getActorDisplayName());
        addExactCondition(conditions, args, "target_type", normalizeEnumValue(form.getTargetType()));
        addLikeCondition(conditions, args, "target_id", form.getTargetId());
        addLikeCondition(conditions, args, "target_name", form.getTargetName());
        addExactCondition(conditions, args, "action_type", normalizeEnumValue(form.getActionType()));

        if (form.getStartDate() != null) {
            conditions.add("date(created_at) >= date(?)");
            args.add(form.getStartDate().toString());
        }

        if (form.getEndDate() != null) {
            conditions.add("date(created_at) <= date(?)");
            args.add(form.getEndDate().toString());
        }

        StringBuilder sql = new StringBuilder();
        sql.append(countQuery ? "SELECT COUNT(*) FROM audit_logs" : "SELECT * FROM audit_logs");

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        if (!countQuery) {
            sql.append(" ORDER BY created_at DESC");
        }

        return new QuerySpec(sql.toString(), Collections.unmodifiableList(args));
    }

    private void addLikeCondition(List<String> conditions, List<Object> args, String column, String value) {
        if (value != null && !value.trim().isEmpty()) {
            conditions.add(column + " LIKE ?");
            args.add("%" + value.trim() + "%");
        }
    }

    private void addExactCondition(List<String> conditions, List<Object> args, String column, String value) {
        if (value != null && !value.trim().isEmpty()) {
            conditions.add(column + " = ?");
            args.add(value.trim());
        }
    }

    private String normalizeEnumValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private record QuerySpec(String sql, List<Object> args) {
    }
}
