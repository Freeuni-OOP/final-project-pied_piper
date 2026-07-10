package com.lecturboxd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * EN: Configuration properties bean for the admin API key and header name (prefix: lecturboxd.admin).
 * KA: კონფიგურაციის თვისებების bean ადმინის API გასაღებისა და ჰედერის სახელისთვის (პრეფიქსი: lecturboxd.admin).
 */
@ConfigurationProperties(prefix = "lecturboxd.admin")
public class AdminProperties {

    private String apiKey = "";
    private String apiKeyHeader = "X-Admin-Api-Key";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKeyHeader() {
        return apiKeyHeader;
    }

    public void setApiKeyHeader(String apiKeyHeader) {
        this.apiKeyHeader = apiKeyHeader;
    }

    /**
     * EN: Returns true when a non-blank admin API key has been configured.
     * KA: აბრუნებს true-ს, როცა არაცარიელი ადმინის API გასაღები კონფიგურირებულია.
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
