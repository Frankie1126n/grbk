package com.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Personal Blog System - Main Application
 * 
 * @author Blog System
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.blog.mapper")
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Personal Blog System Started Successfully!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("Swagger URL: http://localhost:8080/api/swagger-ui/");
        System.out.println("========================================\n");
    }
}
