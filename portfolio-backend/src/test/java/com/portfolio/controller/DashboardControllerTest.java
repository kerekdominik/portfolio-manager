package com.portfolio.controller;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.impl.PnlCalculationService;
import com.portfolio.service.impl.PortfolioCompositionService;
import com.portfolio.service.impl.PortfolioValueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DashboardController.class)
@ExtendWith(SpringExtension.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock dependencies
    @MockBean
    private PnlCalculationService pnlCalculationService;

    @MockBean
    private PortfolioCompositionService portfolioCompositionService;

    @MockBean
    private PortfolioValueService portfolioValueService;

    @MockBean
    private PortfolioRepository portfolioRepository;

    private User testUser;
    private Portfolio testPortfolio;

    @BeforeEach
    void setUp() {
        // Initialize test user and portfolio
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testPortfolio = new Portfolio();
        testPortfolio.setId(1L);
        testPortfolio.setUser(testUser);
    }

    @Test
    void testGetPnlSummary() throws Exception {
        // Arrange
        when(portfolioRepository.findByUser(any(User.class))).thenReturn(Optional.of(testPortfolio));
        Map<String, Double> pnlSummary = Map.of("totalPnl", 1000.0);
        when(pnlCalculationService.calculatePnlForPortfolio(testPortfolio)).thenReturn(pnlSummary);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/pnl-summary")
                        .with(authentication(getAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPnl").value(1000.0));
    }

    @Test
    void testGetPortfolioComposition() throws Exception {
        // Arrange
        when(portfolioRepository.findByUser(any(User.class))).thenReturn(Optional.of(testPortfolio));
        Map<String, Double> composition = Map.of("stocks", 60.0, "bonds", 40.0);
        when(portfolioCompositionService.calculateComposition(testPortfolio)).thenReturn(composition);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/portfolio-composition")
                        .with(authentication(getAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").value(60.0))
                .andExpect(jsonPath("$.bonds").value(40.0));
    }

    @Test
    void testGetGroupPnl() throws Exception {
        // Arrange
        when(portfolioRepository.findByUser(any(User.class))).thenReturn(Optional.of(testPortfolio));
        Map<String, Double> groupPnl = Map.of("group1", 500.0, "group2", 300.0);
        when(pnlCalculationService.calculatePnlForGroups(testPortfolio)).thenReturn(groupPnl);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/group-pnl")
                        .with(authentication(getAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group1").value(500.0))
                .andExpect(jsonPath("$.group2").value(300.0));
    }

    @Test
    void testGetPnlByAsset() throws Exception {
        // Arrange
        when(portfolioRepository.findByUser(any(User.class))).thenReturn(Optional.of(testPortfolio));
        Map<String, Double> pnlAssets = Map.of("asset1", 200.0, "asset2", -100.0);
        when(pnlCalculationService.calculatePnlByAsset(testPortfolio)).thenReturn(pnlAssets);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/pnl-assets")
                        .with(authentication(getAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asset1").value(200.0))
                .andExpect(jsonPath("$.asset2").value(-100.0));
    }

    @Test
    void testGetPortfolioValues() throws Exception {
        // Arrange
        when(portfolioRepository.findByUser(any(User.class))).thenReturn(Optional.of(testPortfolio));
        Map<String, Double> portfolioValues = Map.of("totalValue", 50000.0);
        when(portfolioValueService.calculatePortfolioValues(testPortfolio)).thenReturn(portfolioValues);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/portfolio-values")
                        .with(authentication(getAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue").value(50000.0));
    }

    // Helper method to create an Authentication object for testing
    private Authentication getAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(testUser, null, authorities);
        authenticationToken.setAuthenticated(true);
        return authenticationToken;
    }
}
