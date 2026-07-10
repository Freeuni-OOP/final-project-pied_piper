package com.lecturboxd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * EN: Configuration properties bean for JWT secret and token expiration (prefix: jwt).
 * KA: კონფიგურაციის თვისებების bean JWT საიდუმლოსა და ტოკენის ვადისთვის (პრეფიქსი: jwt).
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long expirationMs;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
