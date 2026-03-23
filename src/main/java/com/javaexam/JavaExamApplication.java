package com.javaexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class JavaExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaExamApplication.class, args);
    }
}
