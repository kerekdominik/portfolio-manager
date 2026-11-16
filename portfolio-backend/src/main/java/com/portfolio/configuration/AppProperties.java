package com.portfolio.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Deployment specific settings, so that nothing environment dependent is hard-coded.
 * Environment variables: APP_FRONTEND_URL and APP_CORS_ALLOWED_ORIGINS (comma separated).
 *
 * @param frontendUrl public base URL of the web application, used to redirect the browser after an OAuth2 login
 * @param cors        cross-origin settings, only needed when the frontend is served from another origin than the API
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(String frontendUrl, Cors cors) {

    public static final String DEFAULT_FRONTEND_URL = "http://localhost:4200";

    public AppProperties {
        frontendUrl = (frontendUrl == null || frontendUrl.isBlank())
                ? DEFAULT_FRONTEND_URL
                : frontendUrl.strip().replaceAll("/+$", "");
        cors = cors == null ? new Cors(null) : cors;
    }

    /**
     * @param allowedOrigins origins allowed to call the API from a browser, defaults to the local Angular dev server
     */
    public record Cors(List<String> allowedOrigins) {

        public Cors {
            allowedOrigins = (allowedOrigins == null || allowedOrigins.isEmpty())
                    ? List.of(DEFAULT_FRONTEND_URL)
                    : List.copyOf(allowedOrigins);
        }
    }
}
