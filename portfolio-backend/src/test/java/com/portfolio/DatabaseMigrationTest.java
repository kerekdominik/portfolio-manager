package com.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Applies the Flyway migrations to a real PostgreSQL and lets Hibernate validate the result against the entities.
 * If an entity changes without a matching migration, the application context fails to start and this test fails.
 * Skipped when Docker is not available.
 */
@DataJpaTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class DatabaseMigrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void migrationsApplyCleanlyAndMatchTheEntities() {
        // reaching this point means Flyway ran and Hibernate's schema validation passed
        List<String> failedMigrations = jdbcTemplate.queryForList(
                "select version from flyway_schema_history where success = false", String.class);
        assertTrue(failedMigrations.isEmpty(), "failed migrations: " + failedMigrations);

        Integer applied = jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history where success = true", Integer.class);
        assertTrue(applied != null && applied >= 1, "expected at least the baseline migration");
    }

    @Test
    void baselineCreatesTheApplicationTables() {
        List<String> tables = jdbcTemplate.queryForList(
                "select table_name from information_schema.tables where table_schema = 'public'", String.class);

        for (String expected : List.of("users", "group", "portfolio", "portfolio_asset", "common_asset",
                "stock", "cryptocurrency", "stock_list", "crypto_list")) {
            assertTrue(tables.contains(expected), "missing table " + expected + ", found " + tables);
        }
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from information_schema.sequences where sequence_name = 'users_seq'", Integer.class));
    }
}
