package com.lecturboxd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

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

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
