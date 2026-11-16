# portfolio-manager
[![Backend](https://github.com/kerekdominik/portfolio-manager/actions/workflows/sonarcloud.yml/badge.svg)](https://github.com/kerekdominik/portfolio-manager/actions/workflows/sonarcloud.yml)
[![Frontend](https://github.com/kerekdominik/portfolio-manager/actions/workflows/sonarcloud-frontend.yml/badge.svg)](https://github.com/kerekdominik/portfolio-manager/actions/workflows/sonarcloud-frontend.yml)

Track your investments: stocks and cryptocurrencies, grouped, with profit and loss, composition and value on a dashboard.

| Part | What | Docs |
|---|---|---|
| `portfolio-backend` | Spring Boot REST API (Java 17, PostgreSQL, JWT) | [backend README](portfolio-backend/README.md) |
| `portfolio-frontend` | Angular 18 web app | `ng serve` (needs the backend on port 8080) |

## Run with Docker
```bash
cp .env.example .env        # set DB_PASSWORD and JWT_SECRET_KEY (openssl rand -base64 64)
docker compose up -d --build
```
Open <http://localhost:8082>. The data is kept in a Docker volume across restarts. Prebuilt images are published to ghcr.io by CI (`docker compose pull` instead of `--build`). Details, ports and backups: [Running in containers](portfolio-backend/README.md#running-in-containers).
