package com.lecturboxd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:lecturboxd;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.mail.username=test@freeuni.edu.ge",
        "spring.mail.password=test",
        "jwt.secret=test-secret-key-must-be-at-least-32-characters-long",
        "jwt.expiration-ms=3600000"
})
class LecturboxdApplicationTests {

    @Test
    void contextLoads() {
    }
}
