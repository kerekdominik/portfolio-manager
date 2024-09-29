package com.portfolio.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login( oauth2 -> oauth2
                        .defaultSuccessUrl("/api/")
                )
                .formLogin( form -> form
                        .defaultSuccessUrl("/api/")
                )
                .build();
    }
}
