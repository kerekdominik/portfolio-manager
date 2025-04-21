# Portfolio Manager - Development Guidelines

This document provides specific information for developers working on the Portfolio Manager project.

## Build/Configuration Instructions

### Prerequisites
- Java 17 or higher
- Gradle (wrapper included)
- PostgreSQL database

### Environment Variables
The following environment variables need to be set:
- `DB_USERNAME` - PostgreSQL database username
- `DB_PASSWORD` - PostgreSQL database password
- `JWT_SECRET_KEY` - Secret key for JWT token generation (should be at least 64 bytes for HS512)
- `GOOGLE_CLIENT_ID` - Google OAuth2 client ID
- `GOOGLE_CLIENT_SECRET` - Google OAuth2 client secret

### Database Setup
1. Create a PostgreSQL database named `portfolio-manager`
2. The application is configured to create and drop tables automatically (`spring.jpa.hibernate.ddl-auto=create-drop`)

### Building the Project
```bash
# Windows
gradlew.bat build

# Linux/macOS
./gradlew build
```

### Running the Application
```bash
# Windows
gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```

## Testing Information

### Test Configuration
- Tests use H2 in-memory database instead of PostgreSQL
- Test configuration is in `src/test/resources/application-test.properties`
- JaCoCo is used for test coverage reporting

### Running Tests
```bash
# Run all tests
gradlew.bat test

# Run a specific test class
gradlew.bat test --tests "com.portfolio.entity.UserTest"

# Run a specific test method
gradlew.bat test --tests "com.portfolio.entity.UserTest.testBuilder"
```

### Test Coverage Report
After running tests, JaCoCo generates coverage reports in:
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

### Adding New Tests
1. Create test classes in the `src/test/java` directory following the same package structure as the main code
2. Use JUnit 5 (Jupiter) for writing tests
3. Follow the existing patterns:
   - Use `@BeforeEach` for setup
   - Name test methods with `test` prefix followed by the functionality being tested
   - Use the Arrange-Act-Assert pattern
   - For entity tests, test constructors, getters/setters, equals/hashCode, and toString methods
   - For service tests, mock dependencies using Mockito
   - For controller tests, use `@ExtendWith(SpringExtension.class)` and mock services

### Example Test
Here's a simple entity test example:

```java
package com.portfolio.entity.asset.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoListItemTest {

    private CryptoListItem cryptoListItem;

    @BeforeEach
    void setUp() {
        cryptoListItem = new CryptoListItem();
    }

    @Test
    void testSettersAndGetters() {
        cryptoListItem.setId("ethereum");
        cryptoListItem.setSymbol("eth");
        cryptoListItem.setName("Ethereum");
        
        assertEquals("ethereum", cryptoListItem.getId());
        assertEquals("eth", cryptoListItem.getSymbol());
        assertEquals("Ethereum", cryptoListItem.getName());
    }
}
```

## Additional Development Information

### Code Style
- The project uses Lombok to reduce boilerplate code
- Entity classes use JPA annotations
- DTOs are used for data transfer between layers
- Services are implemented with interfaces for better testability
- Spring Security is used for authentication and authorization
- JWT is used for token-based authentication
- Google OAuth2 is configured for social login

### Project Structure
- `com.portfolio.configuration` - Application configuration classes
- `com.portfolio.controller` - REST API controllers
- `com.portfolio.dto` - Data Transfer Objects
- `com.portfolio.entity` - JPA entities
- `com.portfolio.mapper` - MapStruct mappers for DTO-Entity conversion
- `com.portfolio.repository` - Spring Data JPA repositories
- `com.portfolio.service` - Service interfaces and implementations

### Caching
- The application uses Caffeine for caching
- Cache configuration is in `CacheConfig.java`

### API Documentation
- OpenAPI (Swagger) is configured for API documentation
- Configuration is in `OpenApiConfig.java`
- Access the API documentation at `/swagger-ui.html` when the application is running

### Security
- JWT authentication is implemented in `JwtAuthFilter.java`
- Security configuration is in `SecurityConfig.java`
- OAuth2 success handler is in `OAuth2SuccessHandler.java`

### Debugging
- Debug logging is enabled for Spring Security and Spring Web
- Check application.properties for logging configuration