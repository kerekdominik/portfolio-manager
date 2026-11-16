package com.portfolio.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesConfig.class);

    @EnableConfigurationProperties(AppProperties.class)
    static class PropertiesConfig {
    }

    @Test
    void defaultsPointAtTheLocalAngularDevServer() {
        AppProperties properties = new AppProperties(null, null);

        assertEquals("http://localhost:4200", properties.frontendUrl());
        assertEquals(List.of("http://localhost:4200"), properties.cors().allowedOrigins());
    }

    @Test
    void blankValuesFallBackToDefaults() {
        AppProperties properties = new AppProperties("  ", new AppProperties.Cors(List.of()));

        assertEquals("http://localhost:4200", properties.frontendUrl());
        assertEquals(List.of("http://localhost:4200"), properties.cors().allowedOrigins());
    }

    @Test
    void frontendUrlIsTrimmedAndLosesTrailingSlashes() {
        assertEquals("https://portfolio.example.com", new AppProperties(" https://portfolio.example.com// ", null).frontendUrl());
    }

    @Test
    void bindsValuesFromConfiguration() {
        contextRunner
                .withPropertyValues(
                        "app.frontend-url=https://portfolio.example.com",
                        "app.cors.allowed-origins=https://a.example.com,https://b.example.com")
                .run(context -> {
                    AppProperties properties = context.getBean(AppProperties.class);
                    assertEquals("https://portfolio.example.com", properties.frontendUrl());
                    assertEquals(List.of("https://a.example.com", "https://b.example.com"), properties.cors().allowedOrigins());
                });
    }

    @Test
    void usesDefaultsWhenNothingIsConfigured() {
        contextRunner.run(context -> {
            AppProperties properties = context.getBean(AppProperties.class);
            assertEquals("http://localhost:4200", properties.frontendUrl());
        });
    }
}
