package com.javaexam.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.entity.User;

@Component
public class UserChangeCalculator implements AuditLogChangeCalculator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String calculateChanges(Object before, Object after) {
        if (before == null || after == null) {
            return null;
        }

        User beforeUser = (User) before;
        User afterUser = (User) after;
        
        Map<String, Map<String, String>> changes = new LinkedHashMap<>();

        // username の変更を確認
        if (!beforeUser.getUsername().equals(afterUser.getUsername())) {
            changes.put("username", Map.of(
                "before", beforeUser.getUsername(),
                "after", afterUser.getUsername()
            ));
        }

        // display_name の変更を確認
        if (!beforeUser.getDisplayName().equals(afterUser.getDisplayName())) {
            changes.put("display_name", Map.of(
                "before", beforeUser.getDisplayName(),
                "after", afterUser.getDisplayName()
            ));
        }

        // password の変更を確認（値は記録しない）
        if (!beforeUser.getPassword().equals(afterUser.getPassword())) {
            changes.put("password", Map.of("changed", "true"));
        }

        // role の変更を確認
        if (!beforeUser.getRole().equals(afterUser.getRole())) {
            changes.put("role", Map.of(
                "before", beforeUser.getRole().toString(),
                "after", afterUser.getRole().toString()
            ));
        }

        return changes.isEmpty() ? null : toJson(changes);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
