package com.javaexam.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.javaexam.entity.User;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


@ExtendWith(MockitoExtension.class)
@SpringBootTest


public class UserJdbcRepositoryTest {
    private final UserJdbcRepository userJdbcRepository;

    public UserJdbcRepositoryTest(UserJdbcRepository userJdbcRepository) {
        this.userJdbcRepository = userJdbcRepository;
    }

    @Test
    void testSave() {
        User user = new User();
        user.setUsername("user1");
        user.setDisplayName("ユーザー1");
        user.setPassword("encoded-password");
        user.setRole("ROLE_USER");

        userJdbcRepository.save(user);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userJdbcRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("user1", savedUser.getUsername());
        assertEquals("ユーザー1", savedUser.getDisplayName());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
    }
}
