package com.lecturboxd.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminPropertiesTest {

    @Test
    void defaultsAndConfigurationChecks() {
        AdminProperties props = new AdminProperties();
        assertEquals("", props.getApiKey());
        assertEquals("X-Admin-Api-Key", props.getApiKeyHeader());
        assertFalse(props.isConfigured());

        props.setApiKey("secret");
        props.setApiKeyHeader("X-Custom");
        assertTrue(props.isConfigured());
        assertEquals("secret", props.getApiKey());
        assertEquals("X-Custom", props.getApiKeyHeader());

        props.setApiKey("   ");
        assertFalse(props.isConfigured());

        props.setApiKey(null);
        assertFalse(props.isConfigured());
    }
}
