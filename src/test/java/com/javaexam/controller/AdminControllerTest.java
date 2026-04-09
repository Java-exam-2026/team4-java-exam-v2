package com.javaexam.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.javaexam.service.AdminService;
import com.opencsv.exceptions.CsvValidationException;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.bytebuddy.asm.Advice;




/**
 * 
 * @param adminController
 * @throws Exception
 */
@WebMvcTest(AdminController.class)
publ class AAdminControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    private AdminService adminService;

    /**
     * CSVインポート成功時に問題一覧へリダイレクトするテスト
     */
    @Test
    void importProblemsCsv_shouldRedirectToQuestionsPage_whenSuccess() {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "problems.csv",
            "text/csv",
            csv.getBytes(StandardCharsets.UTF_8));
        
        mockMvc.perform(multipart("/admin/questions/import").file(file))
            .andExpect(status().is3xxRedirection());
        

        verify(adminService).importProblemsFromCsv(any());
    }

    /**
     * 問題のCSVファイルを受け取ったとき、CSVの形式が間違っている場合に正しい例外を投げるかどうかのテスト
     * 正しくIllegalExceptionが投げられるかどうかを検証
     */

    @Test
    void importProblemsCsv_shouldSetErrorMessage_whenIllegalArgumentException() throws Exception {

        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        doThrow(new IllegalArgumentException("csv error"))
                .when(adminService)
                .importProblemsFromCsv(any());

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    /**
     * 問題のCSVファイルを受け取ったとき、CSVファイル自体が破損していた場合のテスト
     * 正しくCSVValidationExceptionが投げられるかどうかを検証
     */
    @Test 
    void importProblemsCsv_shouldSetErrorMessage_whenCsvValidationException() throws Exception {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        doThrow(new CsvValidationException("csv error"))
            .when(adminService)
            .importProblemsFromCsv(any(MultipartFile.class));
        
        mockMvc.perform(multipart("/admin/questions/").file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/questions/"))
            .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    /**
     * 問題のCSVファイルを受け取ったときに、入出力エラーで例外が発生した場合のテスト
     * 正しくIOExceptionが投げられるかどうかを検証
     */


    @Test
    void importProblemsCsv_shouldSetErrorMessage_whenIOException() throws Exception {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        doThrow(new IOException("入出力 error"))
            .when(adminService)
            .importProblemsFromCsv(any(MultipartFile.class));
        
        mockMvc.perform(multipart("/admin/questions/").file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/questions/"))
            .andExpect(flash().attribute("error", "ファイルの読み込みに失敗しました"));
    }

    /**
     * 問題のCSVファイルを受け取ったときに、その他のエラーが発生した場合のテスト
     * 正しくExceptionが投げられるかどうかを検証
     */
    @Test
    void importProblemsCsv_shouldSetErrorMessage_whenException() throws Exception {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        doThrow(new Exception("その他のエラー"))
            .when(adminService)
            .importProblemsFromCsv(any(MultipartFile.class));
        
        mockMvc.perform(multipart("/admin/questions/").file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/questions/"))
            .andExpect(flash().attribute("error", "CSV取込中にエラーが発生しました"));
    }

    /**
     * ユーザーのCSVファイルを受け取ったとき、ユーザー一覧へリダイレクトするテスト
     */

    @Test
    void importUsersCsv_shouldRedirectToHomePage_whenSuccess() throws Exception {
        String csv ="user1,password,ユーザー1,ROLE_USER";

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "users.csv",
            "text/csv",
            csv.getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(multipart("/admin/users/import").file(file))
            .andExpect(status().is3xxRedirection());
        

        verify(adminService).importUsersFromCsv(any());
    }

    
}









