package com.javaexam.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private String id;
    private String actor_user_id;
    private String actor_username;
    private String actor_display_name;
    private TargetType target_type;
    private String target_id;
    private String target_name;
    private ActionType action_type;
    private Boolean action_status;
    private String changes_json;
    private LocalDateTime created_at;

}
