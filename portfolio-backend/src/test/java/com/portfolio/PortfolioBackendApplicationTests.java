package com.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PortfolioBackendApplicationTests {

	@Test
	void testMain() {
		// Mock SpringApplication.run to avoid starting the full context
		try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
			PortfolioBackendApplication.main(new String[]{});
			mockedSpringApplication.verify(() -> SpringApplication.run(PortfolioBackendApplication.class, new String[]{}));
		}
	}

	@Test
	void testClassInstantiation() {
		PortfolioBackendApplication application = new PortfolioBackendApplication();
		assertNotNull(application);
	}
}
