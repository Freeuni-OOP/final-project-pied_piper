package com.lecturboxd;

import com.lecturboxd.config.AdminProperties;
import com.lecturboxd.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * EN: Spring Boot entry point for the LecturBoxd backend application.
 * KA: Spring Boot-ის შესვლის წერტილი LecturBoxd ბექენდ აპლიკაციისთვის.
 */
@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, AdminProperties.class})
public class LecturboxdApplication {

    /**
     * EN: Boots the Spring application context with JWT and admin configuration properties enabled.
     * KA: იწყებს Spring აპლიკაციის კონტექსტს ჩართული JWT და ადმინის კონფიგურაციის თვისებებით.
     */
    public static void main(String[] args) {
        SpringApplication.run(LecturboxdApplication.class, args);
    }
}
