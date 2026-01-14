package com.javaexam.controller;

import com.javaexam.dto.UserAnswerByDateDto;
import com.javaexam.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AdminService adminService;

    public ApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get users who answered questions on a specific date.
     * 
     * @param date The date in format YYYY-MM-DD
     * @return List of users who answered on that date
     */
    @GetMapping("/users/by-answer-date")
    public ResponseEntity<List<UserAnswerByDateDto>> getUsersByAnswerDate(
            @RequestParam("date") String date) {
        // Validate date format (YYYY-MM-DD)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return ResponseEntity.badRequest().build();
        }
        
        List<UserAnswerByDateDto> users = adminService.getUsersByAnswerDate(date);
        return ResponseEntity.ok(users);
    }
}
