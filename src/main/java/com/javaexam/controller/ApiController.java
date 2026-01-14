package com.javaexam.controller;

import com.javaexam.dto.UserAnswerByDateDto;
import com.javaexam.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        // Validate date format and validity
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        
        List<UserAnswerByDateDto> users = adminService.getUsersByAnswerDate(date);
        return ResponseEntity.ok(users);
    }
}
