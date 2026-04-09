package com.javaexam.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.service.AdminService;
import com.opencsv.exceptions.CsvValidationException;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private ChapterJdbcRepository chapterJdbcRepository;

    @MockBean
    private QuestionJdbcRepository questionJdbcRepository;

    @MockBean
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldRedirectToQuestionsPage_whenSuccess() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("message", "CSV取り込みが完了しました"));

        verify(adminService).importProblemsFromCsv(any(MultipartFile.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenIllegalArgumentException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new IllegalArgumentException("CSVの形式が不正です"))
                .when(adminService)
                .importProblemsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenCsvValidationException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new CsvValidationException("csv error"))
                .when(adminService)
                .importProblemsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenIOException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new IOException("read error"))
                .when(adminService)
                .importProblemsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "ファイル読み込みに失敗しました"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenUnexpectedException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new RuntimeException("unexpected error"))
                .when(adminService)
                .importProblemsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSV取込中にエラーが発生しました"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importUsersCsv_shouldRedirectToHomePage_whenSuccess() throws Exception {
        MockMultipartFile file = createUsersCsvFile();

        mockMvc.perform(multipart("/admin/import/csv/users").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "CSV取り込みが完了しました"));

        verify(adminService).importUsersFromCsv(any(MultipartFile.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importUsersCsv_shouldSetErrorMessage_whenIllegalArgumentException() throws Exception {
        MockMultipartFile file = createUsersCsvFile();

        doThrow(new IllegalArgumentException("CSVの形式が不正です"))
                .when(adminService)
                .importUsersFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/users").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importUsersCsv_shouldSetErrorMessage_whenCsvValidationException() throws Exception {
        MockMultipartFile file = createUsersCsvFile();

        doThrow(new CsvValidationException("csv error"))
                .when(adminService)
                .importUsersFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/users").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importUsersCsv_shouldSetErrorMessage_whenIOException() throws Exception {
        MockMultipartFile file = createUsersCsvFile();

        doThrow(new IOException("read error"))
                .when(adminService)
                .importUsersFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/users").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "ファイル読み込みに失敗しました"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importUsersCsv_shouldSetErrorMessage_whenUnexpectedException() throws Exception {
        MockMultipartFile file = createUsersCsvFile();

        doThrow(new RuntimeException("unexpected error"))
                .when(adminService)
                .importUsersFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/users").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "CSV取込中にエラーが発生しました"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importUsersCsv_shouldNotCallService_whenFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                new byte[0]);

        mockMvc.perform(multipart("/admin/import/csv/users").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verifyNoInteractions(adminService);
    }

    private MockMultipartFile createProblemsCsvFile() {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";
        return new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile createUsersCsvFile() {
        String csv = "user1,password,ユーザー1,ROLE_USER";
        return new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
    }
}
