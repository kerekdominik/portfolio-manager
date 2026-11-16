# portfolio-manager

Full-stack portfolio tracker: `portfolio-backend` (Java 17, Spring Boot, Gradle) and `portfolio-frontend` (Angular 18).

## Project rules

- **Keep the GitHub project board up to date.** Board: https://github.com/users/kerekdominik/projects/4 ("Portfolio management", owner `kerekdominik`, project number 4). Whenever something is asked or being worked on, use the board and adjust its items: create an item if none exists, move it as the work progresses, close it when done. Use the `gh project` commands (for example `gh project item-list 4 --owner kerekdominik`).
- Link every commit and PR to a board issue (`Refs #N`). Create the issue first if none exists.
- Work on a feature branch and open a PR into `main`. Never commit directly to `main`.
- Run `./gradlew test` before committing backend changes and `npm test` before committing frontend changes.
- When behaviour, API endpoints or config change, update `portfolio-backend/README.md` in the same commit.

## Security and configuration

- Never hard-code secrets or API keys. Read them from environment variables. Do not touch the existing hard-coded Alpha Vantage key without asking, because it needs to be rotated.
- Do not add new hard-coded localhost URLs. Make them configurable properties.
- Do not change `spring.jpa.hibernate.ddl-auto` or add DB migrations without asking first, because it affects data persistence.

## Code conventions

- Backend layering is controller, service, repository, entity. Use DTOs at the API boundary and MapStruct for mapping. Never return entities from controllers.
- Add unit tests for every new service or entity, following the patterns in the backend README.

## Commands

- Backend (from `portfolio-backend`): `./gradlew bootRun`, `./gradlew test`, `./gradlew build`
- Frontend (from `portfolio-frontend`): `npm start`, `npm test`

## References

- Backend details (setup, API, tests, conventions): `portfolio-backend/README.md`
- Obsidian project note: `LifeOS/Own projects/portfolio-manager/Portfolio Manager.md` in the vault
