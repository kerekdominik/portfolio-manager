# portfolio-backend

Spring Boot backend for a portfolio management app. It handles authentication, users, portfolios, stocks, crypto assets, dashboard data, and external market-data lookups.

## Tech stack
- Java 17
- Spring Boot
- Spring Security + JWT + OAuth2 login
- Spring Data JPA + PostgreSQL
- Spring Cache + Caffeine
- MapStruct for mapping entities to DTOs
- OpenAPI/Swagger for API docs

## Main entry point
- `com.portfolio.PortfolioBackendApplication` — starts the application and boots all Spring components.

## Package overview

### `com.portfolio.configuration`
App wiring and cross-cutting setup.
- `ApplicationConfig` — general application beans/config.
- `CacheConfig` — cache setup for frequently used data.
- `OpenApiConfig` — Swagger/OpenAPI documentation config.
- `security` — authentication and request protection:
  - `SecurityConfig` — main security rules and filter chain.
  - `JwtAuthFilter` — reads/validates JWTs on requests.
  - `AuthEntryPoint` — handles unauthorized access responses.
  - `OAuth2SuccessHandler` — processes successful OAuth2 logins.

### `com.portfolio.controller`
REST API layer exposed to the frontend/client.
- `AuthController` — login/register/auth flows.
- `UserController` — user-related endpoints.
- `GroupController` — portfolio grouping endpoints.
- `StockController` / `CryptoController` — asset CRUD and queries.
- `DashboardController` — summary/overview data for the dashboard.
- `ExternalStockController` / `ExternalCryptoController` — endpoints for external market data.

### `com.portfolio.service`
Service contracts for business logic and external integrations.
- `ExternalStockService`, `ExternalCryptoService` — abstractions for market-data providers.

### `com.portfolio.service.impl`
Concrete business logic implementation.
- `AuthService`, `JwtService`, `OAuth2Service` — authentication and token handling.
- `StockService`, `CryptoService` — asset management logic.
- `PortfolioValueService` — calculates portfolio value.
- `PortfolioCompositionService` — computes asset allocation/composition.
- `PnlCalculationService` — calculates profit/loss.
- `ExternalStockServiceImpl`, `ExternalCryptoServiceImpl` — fetch and adapt data from external APIs.

### `com.portfolio.entity`
Persistence model mapped to the database.
- Core domain objects like `User`, `Group`, `Portfolio`, `PortfolioAsset`.
- Asset models under `asset` such as `Stock`, `Crypto`, `CommonAsset`.
- External list items under `asset.external` such as `StockListItem` and `CryptoListItem`.
- Enums under `entity.enums` for things like roles and asset categories.

### `com.portfolio.repository`
Spring Data repositories for database access.
- Repositories for users, groups, portfolios, portfolio assets, stocks, cryptos, and external asset lists.

### `com.portfolio.dto`
Request/response models used by the API.
- Auth DTOs for register/login/token responses.
- User, group, stock, and crypto DTOs for API payloads.
- `external.api` DTOs for mapping raw responses from external crypto APIs.

### `com.portfolio.mapper`
Object mappers between entities and DTOs.
- `UserMapper` — converts user entities to API-friendly response objects.

## Runtime config
Important settings live in `src/main/resources/application.properties`:
- PostgreSQL connection (`DB_USERNAME`, `DB_PASSWORD`)
- JWT secret (`JWT_SECRET_KEY`)
- Google OAuth2 client credentials (`GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`)

## Tests
The project includes unit/integration tests for controllers, security, services, entities, and the application bootstrap under `src/test/java/com/portfolio`.

