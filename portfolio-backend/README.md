# portfolio-backend

[![Backend](https://github.com/kerekdominik/portfolio-manager/actions/workflows/sonarcloud.yml/badge.svg)](https://github.com/kerekdominik/portfolio-manager/actions/workflows/sonarcloud.yml)

REST API of **portfolio-manager**, an application for tracking an investment portfolio of **stocks** and **cryptocurrencies**. Users register or sign in with Google, add the assets they hold, organise them into groups, and see profit and loss (PnL), portfolio composition and value on a dashboard.

**Stack:** Java 17, Spring Boot, Spring Security (JWT + Google OAuth2), Spring Data JPA, PostgreSQL, MapStruct, Lombok, Caffeine, springdoc-openapi (Swagger UI). The Angular client is in [`../portfolio-frontend`](../portfolio-frontend).

---

## Table of contents
- [Features](#features)
- [Architecture](#architecture)
- [Getting started (backend)](#getting-started-backend)
- [Configuration](#configuration)
- [API](#api)
- [Authentication](#authentication)
- [Data model](#data-model)
- [External data sources](#external-data-sources)
- [Testing and quality](#testing-and-quality)
- [Code conventions](#code-conventions)
- [Project structure](#project-structure)
- [Known limitations](#known-limitations)

---

## Features
- Register and log in with e-mail and password, or sign in with Google.
- Add, list, edit and delete **stocks** and **cryptocurrencies** in your portfolio (quantity, purchase date, purchase price).
- Browse the lists of tradable stocks and cryptocurrencies, and look up current and historical prices.
- Organise holdings into **groups**.
- Dashboard figures: total PnL, PnL per asset and per group, portfolio composition and portfolio values.
- Interactive API documentation (Swagger UI).

## Architecture

```mermaid
flowchart LR
    U["Browser<br/>Angular SPA :4200"] -- "REST + JWT" --> API
    subgraph BE["portfolio-backend (Spring Boot :8080)"]
        API["Controllers<br/>/api/..."] --> SVC["Services<br/>(PnL, composition, value, auth)"]
        SVC --> REPO["Spring Data repositories"]
        SVC --> CACHE["Caffeine cache<br/>3 min, max 100"]
    end
    REPO --> DB[("PostgreSQL<br/>portfolio-manager")]
    SVC -- "stock listing and quotes" --> AV["Alpha Vantage"]
    CACHE -- "crypto prices" --> CG["CoinGecko"]
    U -. "Google sign-in" .-> G["Google OAuth2"]
    G -. "success handler" .-> API
```

The backend is layered: **controller → service → repository → entity**, with DTOs at the API boundary and MapStruct for mapping. Lombok removes boilerplate. The API is stateless: every request carries a JWT.

## Getting started (backend)

### Prerequisites
- **JDK 17** (the Gradle toolchain is set to 17)
- **PostgreSQL** with an empty database named `portfolio-manager`
- A **Google OAuth2 client** (only needed for Google sign-in, but the app expects the properties to resolve at startup)
- Gradle is not required, the wrapper is included

### Run
From this folder (`portfolio-backend`):
```bash
# 1. set the environment variables (see Configuration)
export DB_USERNAME=postgres
export DB_PASSWORD=...
export JWT_SECRET_KEY=...            # at least 64 bytes, see below
export GOOGLE_CLIENT_ID=...
export GOOGLE_CLIENT_SECRET=...

# 2. start the API on http://localhost:8080
./gradlew bootRun
```
On Windows use `gradlew.bat bootRun`.

Then open:
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

### Build and test
```bash
./gradlew build            # compile, test, package
./gradlew test             # tests only
./gradlew test --tests "com.portfolio.service.impl.JwtServiceTest"
./gradlew jacocoTestReport # coverage report
```

> **Heads up:** `spring.jpa.hibernate.ddl-auto` is `create-drop`, so the schema is created at startup and **all data is deleted when the application stops**. See [Known limitations](#known-limitations).

## Configuration

Settings live in [`application.properties`](src/main/resources/application.properties). Secrets are read from environment variables, so nothing sensitive is stored in the repository.

| Environment variable | Used for | Notes |
|---|---|---|
| `DB_USERNAME` | PostgreSQL user | |
| `DB_PASSWORD` | PostgreSQL password | |
| `JWT_SECRET_KEY` | Signing JWTs | At least 64 bytes for HS512. Also set as a secret in CI. |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client id | |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | |

Fixed settings:

| Setting | Value |
|---|---|
| Database URL | `jdbc:postgresql://localhost:5432/portfolio-manager` |
| Server port | `8080` (Spring Boot default) |
| CORS allowed origin | `http://localhost:4200` (methods GET, POST, PUT, DELETE, OPTIONS) |
| JWT lifetime | 24 hours |
| Cache | Caffeine, entries expire 3 minutes after write, max 100 entries |
| Log level | `DEBUG` for `org.springframework.security` and `org.springframework.web` |

Generate a suitable JWT secret with:
```bash
openssl rand -base64 64
```

## API

All endpoints are under `/api`. Everything except `/api/auth/**` requires the header `Authorization: Bearer <token>`. The authoritative, always up-to-date reference is the Swagger UI.

| Area | Method and path | Purpose |
|---|---|---|
| **Auth** | `POST /api/auth/register` | Create an account, returns a JWT |
| | `POST /api/auth/login` | Log in with e-mail and password, returns a JWT |
| | `GET /api/auth/oauth2/authorize` | Start Google sign-in (redirect) |
| **User** | `GET /api/user/me` | Current user |
| **Stocks** | `GET /api/stock/list` | All available stocks (Alpha Vantage listing) |
| | `POST /api/stock` | Add a stock to your portfolio |
| | `GET /api/stock` | Your stocks |
| | `PUT /api/stock/{id}` | Update a holding |
| | `DELETE /api/stock/{id}` | Remove a holding |
| **Crypto** | `GET /api/crypto/list` | All available coins (CoinGecko) |
| | `GET /api/crypto/current/price?id=` | Current price in USD |
| | `GET /api/crypto/historical/price?id=&date=` | Price on a given date |
| | `POST /api/crypto` | Add a coin to your portfolio |
| | `GET /api/crypto` | Your coins |
| | `PUT /api/crypto/{id}` | Update a holding |
| | `DELETE /api/crypto/{id}` | Remove a holding |
| **Groups** | `POST /api/groups` | Create a group |
| | `GET /api/groups` | Your groups |
| | `PUT /api/groups/{id}` | Rename or update a group |
| | `DELETE /api/groups/{id}` | Delete a group |
| **Dashboard** | `GET /api/dashboard/pnl-summary` | Total PnL summary |
| | `GET /api/dashboard/portfolio-composition` | Share of each asset type |
| | `GET /api/dashboard/group-pnl` | PnL per group |
| | `GET /api/dashboard/pnl-assets` | PnL per asset |
| | `GET /api/dashboard/portfolio-values` | Portfolio values |

Example:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"me@example.com","password":"..."}' | jq -r .token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/dashboard/pnl-summary
```
Check the request and response fields in Swagger UI (`LoginRequestDto`, `AuthenticationResponseDto`), as this example assumes the token field is called `token`.

## Authentication

```mermaid
sequenceDiagram
    participant B as Browser (Angular)
    participant A as Backend
    participant G as Google
    Note over B,A: E-mail and password
    B->>A: POST /api/auth/login
    A-->>B: JWT (24 h)
    B->>A: any /api/... call with Bearer JWT
    Note over B,G: Google sign-in
    B->>A: GET /api/auth/oauth2/authorize
    A->>G: redirect to Google
    G-->>A: callback (/login/oauth2/...)
    A-->>B: redirect to http://localhost:4200/oauth2/redirect?token=JWT
```

- Sessions are stateless (`SessionCreationPolicy.STATELESS`), CSRF protection is disabled because there are no cookies, and `JwtAuthFilter` validates the token on each request.
- Public paths: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/login/oauth2/**`, `/oauth2/**`.
- Roles: `USER` and `ADMIN` (`Role` enum).
- Relevant classes: `SecurityConfig`, `JwtAuthFilter`, `JwtService`, `OAuth2SuccessHandler`, `OAuth2Service`, `AuthService`.

## Data model

```mermaid
erDiagram
    USER ||--o| PORTFOLIO : owns
    USER ||--o{ GROUP : creates
    PORTFOLIO ||--o{ PORTFOLIO_ASSET : contains
    GROUP ||--o{ PORTFOLIO_ASSET : groups
    COMMON_ASSET ||--o{ PORTFOLIO_ASSET : "held as"
    COMMON_ASSET ||--|| STOCK : "is a"
    COMMON_ASSET ||--|| CRYPTOCURRENCY : "is a"
    PORTFOLIO_ASSET {
        double quantity
        date purchaseDate
        double priceWhenBought
        double currentPrice
    }
```

- A user currently has **one portfolio**.
- `CommonAsset` is the abstract base (symbol, name, category). `Stock` adds `exchange`, `Crypto` adds `externalId` (the CoinGecko id). Asset categories: `STOCK`, `CRYPTOCURRENCY`.
- `StockListItem` and `CryptoListItem` hold the cached lists of available instruments.

## External data sources

| Source | Used for | Notes |
|---|---|---|
| [CoinGecko](https://www.coingecko.com/en/api) | Coin list, current and historical prices (USD) | Public API with rate limits. Price calls are cached for 3 minutes (`cryptoPriceCache`, `historicalCryptoPriceCache`). |
| [Alpha Vantage](https://www.alphavantage.co/documentation/) | Stock listing and quotes (`LISTING_STATUS`, `GLOBAL_QUOTE`) | The free tier has strict daily request limits. |

## Testing and quality
- JUnit 5, Mockito and Spring Security Test. Tests use an in-memory **H2** database configured in `src/test/resources/application-test.properties`.
- Coverage is measured with JaCoCo. Reports are written to `build/reports/jacoco/test/html/index.html` and as XML for Sonar.
- CI ([`.github/workflows`](../.github/workflows)) runs `./gradlew build jacocoTestReport sonarqube` on every push to `main` and on pull requests, publishing results to [SonarCloud](https://sonarcloud.io) (project `kerekdominik_portfolio-manager`). It needs the repository secrets `SONAR_TOKEN` and `JWT_SECRET_KEY`.

### Running tests
```bash
./gradlew test                                                   # all tests
./gradlew test --tests "com.portfolio.entity.UserTest"           # one class
./gradlew test --tests "com.portfolio.entity.UserTest.testBuilder"  # one method
```
On Windows use `gradlew.bat`. Coverage reports are written to `build/reports/jacoco/test/html/index.html` (HTML) and `build/reports/jacoco/test/jacocoTestReport.xml` (XML).

### Adding new tests
1. Create the test class under `src/test/java`, in the same package as the class under test.
2. Use JUnit 5 (Jupiter) and follow the existing patterns:
   - `@BeforeEach` for setup, test methods named `test...` plus the behaviour, Arrange-Act-Assert structure.
   - **Entity tests:** constructors, getters/setters, `equals`/`hashCode`, `toString`.
   - **Service tests:** mock dependencies with Mockito.
   - **Controller tests:** `@ExtendWith(SpringExtension.class)` and mocked services.

Example entity test:
```java
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

## Code conventions
- Lombok removes boilerplate. Entities carry JPA annotations, DTOs cross the layer boundaries, MapStruct converts between them.
- Services that need to be mocked or swapped are implemented behind interfaces.
- Where to look: caching in `CacheConfig`, OpenAPI in `OpenApiConfig`, security in `SecurityConfig`, `JwtAuthFilter` and `OAuth2SuccessHandler`.
- Debugging: `DEBUG` logging for Spring Security and Web is on by default, tune it in `application.properties`.

## Project structure

```
src/main/java/com/portfolio
├── PortfolioBackendApplication.java
├── configuration/      # Cache, OpenAPI, application beans
│   └── security/       # SecurityConfig, JwtAuthFilter, OAuth2SuccessHandler, AuthEntryPoint
├── controller/         # REST controllers (auth, user, stock, crypto, groups, dashboard)
├── dto/                # Request/response objects, external API responses
├── entity/             # JPA entities, asset hierarchy, enums
├── mapper/             # MapStruct mappers
├── repository/         # Spring Data JPA repositories
└── service/
    ├── (interfaces)    # ExternalCryptoService, ExternalStockService
    └── impl/           # Auth, JWT, OAuth2, Crypto, Stock, PnL, composition, value, external clients
```

## Known limitations
These are open points worth fixing, not intended behaviour:

- **Data is wiped on restart.** `ddl-auto=create-drop` drops the schema when the app stops. Switch to `validate` or `update` plus a migration tool (Flyway or Liquibase) before keeping real data.
- **Alpha Vantage API key is hard-coded** in `ExternalStockServiceImpl`. Move it to an environment variable and rotate it, since it is in the git history.
- **Hard-coded localhost URLs.** The OAuth2 success redirect (`http://localhost:4200/...`), the CORS origin and the Swagger server URL are fixed in code, so a deployed instance needs them made configurable.
- **JWT in a URL.** After Google sign-in the token is passed as a query parameter, which can end up in browser history and logs.
- **Verbose logging.** `DEBUG` logging for Spring Security and Web, and `show-sql=true`, should be turned down outside development.
- **No Docker setup or database migrations yet.** PostgreSQL has to be provided separately.
