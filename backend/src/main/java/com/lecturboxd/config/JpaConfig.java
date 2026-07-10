package com.lecturboxd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * EN: Enables JPA auditing so created/updated timestamps are filled automatically.
 * KA: ჩართავს JPA აუდიტს, რომ შექმნის/განახლების დროის ნიშნულები ავტომატურად შეივსოს.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
