package com.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PortfolioBackendApplicationTests {

	@Test
	void applicationStartsSuccessfully() {
		PortfolioBackendApplication.main(new String[] {});
		assertThat(PortfolioBackendApplicationTests.class).isNotNull();
	}
}