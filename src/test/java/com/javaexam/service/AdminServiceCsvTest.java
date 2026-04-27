package com.javaexam.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaexam.entity.Chapter;
import com.javaexam.entity.Question;
import com.javaexam.entity.User;
import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;
import com.javaexam.repository.UserAnswerJdbcRepository;
import com.javaexam.repository.UserJdbcRepository;
import com.javaexam.repository.UserProgressJdbcRepository;

@ExtendWith(MockitoExtension.class)
public class AdminServiceCsvTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private ChapterJdbcRepository chapterJdbcRepository;
    @Mock
    private QuestionJdbcRepository questionJdbcRepository;
    @Mock
    private UserJdbcRepository userJdbcRepository;
    @Mock
    private UserProgressJdbcRepository userProgressJdbcRepository;
    @Mock
    private UserAnswerJdbcRepository userAnswerJdbcRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PasswordEncoder passwordEncoder;


    /**
     * 問題CSVの取り込み成功時に正しい値であることをテストする。
     */
    @Test
    void testImportQuestionsFromCsv_whenSuccess() throws Exception {
        MockMultipartFile file = createQuestionsCsvFile();
        Map<String, String> mockOptions = Map.of(
                "A", "aaa",
                "B", "bbb",
                "C", "ccc",
                "D", "ddd");

        Chapter chapter = new Chapter();
        chapter.setId("test-chapter-id");
        chapter.setChapterCode("chap3");
        chapter.setTitle("第3章");

        when(chapterJdbcRepository.findByChapterCode("chap3"))
                .thenReturn(Optional.of(chapter));
        when(questionJdbcRepository.existsByChapterIdAndQuestionText("test-chapter-id", "問題文"))
                .thenReturn(false);
        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(mockOptions);

        adminService.importQuestionsFromCsv(file);

        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionJdbcRepository).save(captor.capture());

        Question savedQuestion = captor.getValue();

        assertEquals("test-chapter-id", savedQuestion.getChapter().getId());
        assertEquals("問題文", savedQuestion.getQuestionText());
        assertEquals("A", savedQuestion.getCorrectAnswer());
        assertEquals("aaa", savedQuestion.getOptions().get("A"));
        assertEquals("bbb", savedQuestion.getOptions().get("B"));
    }

    /**
     * 問題のCSV取り込み時に列が少ないとき、IllegalArgumentExceptionが発生することをテストする。
     * 
     * @throws Exception
     */
    @Test
    void testImportQuestionsFromCsv_whenIllegalArgumentsException() throws Exception {
        MockMultipartFile file = createQuestionsCsvFile_whenLineIsShort();
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.importQuestionsFromCsv(file);
        });
    }

    /**
     * 問題のCSV取り込み時にチャプターが見つからないとき、IllegalArgumentExceptionが発生することをテストする。
     * 
     * @throws Exception
     */
    @Test
    void testImportQuestionsFromCsv_whenChapterNotFound() throws Exception {
        MockMultipartFile file = createQuestionsCsvFile_whenChapterNotFound();
        when(chapterJdbcRepository.findByChapterCode("NOT_EXIST_CHAPTER"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.importQuestionsFromCsv(file);
        });
    }

    /**
     * ユーザーCSVの取り込み成功時に正しい値であることをテストする。
     */
    @Test
    void testImportUsersFromCsv() throws Exception {
        MockMultipartFile file = createUsersCsvFile();
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");
        adminService.importUsersFromCsv(file);

        User user = new User();
        user.setUsername("user1");
        user.setPassword("password");
        user.setDisplayName("ユーザー1");
        user.setRole("ROLE_USER");


        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userJdbcRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("user1", savedUser.getUsername());
        assertEquals("hashed-password", savedUser.getPassword());
        assertEquals("ユーザー1", savedUser.getDisplayName());
        assertEquals("ROLE_USER", savedUser.getRole());
    }

    /**
     * ユーザーのCSV取り込み時に列が少ないとき、IllegalArgumentExceptionが発生することをテストする。
     * 
     * @throws Exception
     */
    @Test
    void testImportUsersFromCsv_whenLineIsShort() throws Exception {
        MockMultipartFile file = createUsersCsvFile_whenLineIsShort();
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.importUsersFromCsv(file);
        });
    }

    /**
     * ユーザーのCSV取り込み時にロールが不正なとき、IllegalArgumentExceptionが発生することをテストする。
     * 
     * @throws Exception
     */

    @Test
    void testImportUsersFromCsv_whenRoleIsIllegal() throws Exception {
        MockMultipartFile file = createUsersCsvFile_whenRoleIsIllegal();
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.importUsersFromCsv(file);
        });
    }

    /**
     * 問題の重複判定が正しく判定されているか確かめるテストメソッド。
     * 
     * @throws Exception
     */
    @Test
    void testIsDuplicateQuestion() throws Exception {
        when(questionJdbcRepository.existsByChapterIdAndQuestionText("chapter-1", "問題文"))
                .thenReturn(true);

        boolean result = adminService.isDuplicateQuestion("chapter-1", "問題文");
        assertTrue(result);
    }

    /**
     * ユーザーの重複判定が正しく判定されているか確かめるテストメソッド。
     * 
     * @throws Exception
     */

    @Test
    void testIsDuplicateUser() {
        when(userJdbcRepository.existsByUsername("user1"))
                .thenReturn(true);

        boolean result = adminService.isDuplicateUser("user1");

         assertTrue(result);
    }

    private MockMultipartFile createQuestionsCsvFile() {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";
        return new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile createQuestionsCsvFile_whenLineIsShort() {
        String csv = "chap3,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\"";
        return new MockMultipartFile(
                "file",
                "problems.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile createQuestionsCsvFile_whenChapterNotFound() {
        String csv = "NOT_EXIST_CHAPTER,問題文,SINGLE_CHOICE,\"{\"\"A\"\":\"\"aaa\"\",\"\"B\"\":\"\"bbb\"\",\"\"C\"\":\"\"ccc\"\",\"\"D\"\":\"\"ddd\"\"}\",A";
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

    private MockMultipartFile createUsersCsvFile_whenLineIsShort() {
        String csv = "user1,password,ROLE_USER";
        return new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile createUsersCsvFile_whenRoleIsIllegal() {
        String csv = "user1,password,ユーザー1,ROLE_NOT_EXIST";
        return new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
    }
}
