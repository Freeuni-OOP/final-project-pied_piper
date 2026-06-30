package com.lecturboxd;

import com.lecturboxd.config.AdminProperties;
import com.lecturboxd.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, AdminProperties.class})
public class LecturboxdApplication {

    public static void main(String[] args) {
        SpringApplication.run(LecturboxdApplication.class, args);
    }
}
