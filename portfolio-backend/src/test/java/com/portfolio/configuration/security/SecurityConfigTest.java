package com.portfolio.configuration.security;

import com.portfolio.configuration.AppProperties;
import com.portfolio.controller.DashboardController;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.impl.JwtService;
import com.portfolio.service.impl.PnlCalculationService;
import com.portfolio.service.impl.PortfolioCompositionService;
import com.portfolio.service.impl.PortfolioValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the real security filter chain from {@link SecurityConfig} against one protected controller.
 */
@WebMvcTest(value = DashboardController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@Import({SecurityConfig.class, AuthEntryPoint.class, SecurityConfigTest.OAuth2TestConfig.class})
class SecurityConfigTest {

    private static final String PROTECTED_URL = "/api/dashboard/pnl-summary";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;
    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private PnlCalculationService pnlCalculationService;
    @MockitoBean
    private PortfolioCompositionService portfolioCompositionService;
    @MockitoBean
    private PortfolioValueService portfolioValueService;
    @MockitoBean
    private PortfolioRepository portfolioRepository;

    @TestConfiguration
    @EnableConfigurationProperties(AppProperties.class)
    static class OAuth2TestConfig {
        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(ClientRegistration.withRegistrationId("google")
                    .clientId("test-client")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .authorizationUri("https://accounts.example.com/auth")
                    .tokenUri("https://accounts.example.com/token")
                    .build());
        }
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get(PROTECTED_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_whenAuthenticated_returnsData() throws Exception {
        // The JWT handling itself is covered by JwtAuthFilterTest; here we only check the authorization rules
        User user = User.builder().id(1L).email("test@example.com").build();
        when(portfolioRepository.findByUser(any())).thenReturn(Optional.of(new Portfolio()));
        when(pnlCalculationService.calculatePnlForPortfolio(any())).thenReturn(Map.of("total", 42.0));

        mockMvc.perform(get(PROTECTED_URL)
                        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(42.0));
    }

    @Test
    void publicPath_isNotBlockedBySecurity() throws Exception {
        // No controller serves this path in the slice, so 404 shows security let the request through
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isNotFound());
    }

    @Test
    void corsPreflight_fromFrontendOrigin_isAllowed() throws Exception {
        mockMvc.perform(options(PROTECTED_URL)
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void actuatorHealth_isPublic() throws Exception {
        // The actuator endpoint itself is not part of this MVC slice, so 404 shows security let the request through
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isNotFound());
    }

    @Test
    void otherActuatorEndpoints_requireAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsPreflight_fromOtherOrigin_isRejected() throws Exception {
        mockMvc.perform(options(PROTECTED_URL)
                        .header("Origin", "https://evil.example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
