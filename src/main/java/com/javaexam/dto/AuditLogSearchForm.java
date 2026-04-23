package com.javaexam.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class AuditLogSearchForm {
    private String actorUserId;
    private String actorUsername;
    private String actorDisplayName;
    private String targetType;
    private String targetId;
    private String targetName;
    private String actionType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    public boolean hasSearchCondition() {
        return StringUtils.hasText(actorUserId)
                || StringUtils.hasText(actorUsername)
                || StringUtils.hasText(actorDisplayName)
                || StringUtils.hasText(targetType)
                || StringUtils.hasText(targetId)
                || StringUtils.hasText(targetName)
                || StringUtils.hasText(actionType)
                || startDate != null
                || endDate != null;
    }

    public void normalize() {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
    }
}
