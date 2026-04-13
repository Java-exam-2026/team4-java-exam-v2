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

/**
 * 管理者画面の CSV インポート処理に関するコントローラテスト。
 */
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

    /**
     * 問題 CSV の読み取り成功時に、問題一覧画面へリダイレクトすること、成功のメッセージがでることをを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldRedirectToQuestionsPage_whenSuccess() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("message", "CSV取り込みが完了しました"));

        verify(adminService).importQuestionsFromCsv(any(MultipartFile.class));
    }

    /**
     * 問題 CSV 読み取り時に不正な引数が渡された場合、正しいエラーメッセージが出ることを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenIllegalArgumentException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new IllegalArgumentException("CSVの形式が不正です"))
                .when(adminService)
                .importQuestionsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    /**
     * 問題 CSV の形式が不正な場合、正しいエラーメッセージが出ることを確認する。
     * CsvValidationException のテスト
     * @throws Exception MockMvc 実行時の例外
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenCsvValidationException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new CsvValidationException("csv error"))
                .when(adminService)
                .importQuestionsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSVの形式が不正です"));
    }

    /**
     * 問題 CSV の読込時に入出力エラーが発生した場合、正しいエラーメッセージが出ることを確認する。
     * IOExceptionのテスト
     * @throws Exception MockMvc 実行時の例外
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenIOException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new IOException("read error"))
                .when(adminService)
                .importQuestionsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "ファイル読み込みに失敗しました"));
    }

    /**
     * 問題 CSV 読取時に想定外の例外が発生した場合、汎用エラーメッセージを設定することを確認する。
     * Exceptionのテスト
     * @throws Exception MockMvc 実行時の例外
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void importProblemsCsv_shouldSetErrorMessage_whenUnexpectedException() throws Exception {
        MockMultipartFile file = createProblemsCsvFile();

        doThrow(new RuntimeException("unexpected error"))
                .when(adminService)
                .importQuestionsFromCsv(any(MultipartFile.class));

        mockMvc.perform(multipart("/admin/import/csv/problems").file(file).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attribute("error", "CSV取込中にエラーが発生しました"));
    }

    /**
     * ユーザー CSV の取込成功時に、ホーム画面へリダイレクトすることを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
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

    /**
     * ユーザー CSV 読み取り時に不正な引数が渡された場合、正しいエラーメッセージが出ることを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
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

    /**
     * ユーザー CSV の形式が不正な場合、正しいエラーメッセージが出ることを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
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

    /**
     * ユーザー CSV の読込時に入出力エラーが発生した場合、正しいエラーメッセージが出ることを確認する。
     * @throws Exception MockMvc 実行時の例外
     */
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

    /**
     * ユーザー CSV 取込中に想定外の例外が発生した場合、正しいエラーメッセージが出ることを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
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

    /**
     * ユーザー CSV が空ファイルの場合、サービスを呼び出さずにホームへ戻ることを確認する。
     *
     * @throws Exception MockMvc 実行時の例外
     */
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
