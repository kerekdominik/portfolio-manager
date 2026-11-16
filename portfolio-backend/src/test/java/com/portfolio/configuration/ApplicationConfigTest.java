package com.portfolio.configuration;

import com.portfolio.entity.User;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    private ApplicationConfig config;

    @BeforeEach
    void setUp() {
        config = new ApplicationConfig(userRepository);
    }

    @Test
    void userDetailsService_loadsUserByEmail() {
        User user = User.builder().email("test@example.com").build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertEquals(user, config.userDetailsService().loadUserByUsername("test@example.com"));
    }

    @Test
    void userDetailsService_unknownEmail_throws() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());
        UserDetailsService userDetailsService = config.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("nobody@example.com"));
    }

    @Test
    void authenticationProvider_isDaoProvider() {
        assertInstanceOf(DaoAuthenticationProvider.class, config.authenticationProvider());
    }

    @Test
    void passwordEncoder_hashesAndMatches() {
        PasswordEncoder encoder = config.passwordEncoder();
        String hash = encoder.encode("secret-password");

        assertNotEquals("secret-password", hash);
        assertTrue(encoder.matches("secret-password", hash));
    }

    @Test
    void authenticationManager_comesFromConfiguration() {
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(manager);

        assertEquals(manager, config.authenticationManager(authenticationConfiguration));
    }

    @Test
    void clock_isProvided() {
        assertNotNull(config.clock());
    }

    @Test
    void cacheManager_createsCaches() {
        CacheManager cacheManager = new CacheConfig().cacheManager();

        assertNotNull(cacheManager.getCache("cryptoPriceCache"));
    }
}
