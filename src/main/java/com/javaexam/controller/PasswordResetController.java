package com.javaexam.controller;

import com.javaexam.dto.PasswordResetRequestDto;
import com.javaexam.dto.PasswordResetDto;
import com.javaexam.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * パスワードリセット機能のリクエストを処理するコントローラークラス
 * 
 * ＠author 川端理央
 */
@Controller
@RequestMapping("/password")
public class PasswordResetController {

    /**　パスワードリセットの処理を担当するサービス */
    private final PasswordResetService passwordResetService;

    /** 
     * コンストラクタ
     * 
     * @param passwordResetServise パスワードリセット処理用サービス
     */
    public PasswordResetController(PasswordResetService passwordResetServise) {
        this.passwordResetService = passwordResetServise;
    }

    /**　パスワードリセット申請画面を表示する
     * 
     * @return forgot-password画面
     */
    @GetMapping("/forgot")
    public String forgotPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot")
    public String handleForgot(@ModelAttribute  PasswordResetRequestDto dto,
        Model model) {
            try {
                passwordResetService.createToken(dto.getUserId());
                return "redirect:/password/sent";
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
                return "forgot-password";
            }
        }
    
    @GetMapping("/sent")
    public String sentPage() {
        return "password-reset-sent";
    }

   @GetMapping("/reset")
    public String resetPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset")
    public String handleReset(@ModelAttribute PasswordResetDto dto) {
        passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());
        return "redirect:/login";
    }   
}

