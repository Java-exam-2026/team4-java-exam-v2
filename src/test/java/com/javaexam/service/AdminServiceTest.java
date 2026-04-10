package com.javaexam.service;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.javaexam.repository.ChapterJdbcRepository;
import com.javaexam.repository.QuestionJdbcRepository;


@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    
    @InjectMocks
    private AdminService adminService;

    @Mock
    private ChapterJdbcRepository chapterJdbcRepository;

    @Mock
    private QuestionJdbcRepository questionJdbcRepository;
    
    /**
     * 問題CSVの取り込み成功時に正しい値であることをテストする。
     */
    @Test
    void testImportProblemsFromCsv_whenSuccess() {
        MockMultipartFile file = createProblemsCsvFile();
        try {
            adminService.importProblemsFromCsv(file);
    }
        


    }

    @Test
    void testImportUsersFromCsv() {

    }

    @Test
    void testIsDuplicateQuestion() {

    }

    @Test
    void testIsDuplicateUser() {

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
