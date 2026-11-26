# Podcuaderno

**Turn your study notes into an audiobook.**
Paste or upload your notes, review the topics Podcuaderno detects, and download an audiobook with a bookmark for every topic and concept, ready for any audiobook app.

> 🚧 **Status:** Sprint 0: project foundations ready (v0.0.1). Proofs of concept for in-browser voice and M4B in progress. Public beta planned for April 2027.

## Why

Many students also work and commute. Podcuaderno lets them review **their own notes, exactly as they wrote them**, on the bus or at the gym, and jump straight to any concept, instead of listening to an AI summary of their material.

## Planned features (v1.0, free)

- Import plain text, Markdown and Word (DOCX), with automatic topic and concept detection.
- Editor to rename, reorder, merge and split topics and concepts.
- Free voice generated **in the browser**: zero server cost, and notes never leave the device.
- Export as **M4B** (a chapter marker per concept) or **MP3** per topic.
- Accounts, or guest mode with no sign-up.

**v2 (premium):** natural neural voice generated on the server, monthly plans and usage limits.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React · TypeScript · Vite · TanStack Query |
| In-browser audio | Open-source TTS model on WebAssembly, Web Workers, IndexedDB |
| Backend | Java 25 · Spring Boot 4 · Spring Modulith · Spring Security (JWT) |
| Database | PostgreSQL · Flyway |
| Quality | JUnit Jupiter 6 · Testcontainers · ArchUnit · Spring Modulith · Vitest · Playwright · SonarCloud |
| Build | Gradle (Kotlin DSL) · pnpm |
| Delivery | Docker · GitHub Actions |

## Architecture

A **modular monolith** in which each business module (`identity`, `notebooks`, `importing`, `metrics`) follows **Clean Architecture with ports and adapters**. Modules communicate through domain events, and automated tests (Spring Modulith + ArchUnit) enforce the architecture rules on every pull request.

See [docs/05_Arquitectura_y_Diseno.md](docs/05_Arquitectura_y_Diseno.md) and the [architecture decision records](docs/adr/).

## Getting started

**Prerequisites:** only [Docker](https://docs.docker.com/get-docker/) with Docker Compose.
For local development you also need Java 25, Node 22 and pnpm.

Start the whole stack (PostgreSQL, Mailpit, backend and frontend) with one command:

```bash
docker compose up --build
```

| Service | URL |
|---|---|
| App | `http://localhost:3000` |
| API | `http://localhost:8080` |
| OpenAPI | `http://localhost:8080/swagger-ui.html` |
| Health | `http://localhost:8080/actuator/health` |
| Mailpit | `http://localhost:8025` |

The app serves the API under `/api/` from the same origin, so no CORS setup is needed locally.

A `.env` file is **optional**: every setting has a default. To override ports or credentials, copy the example and edit it (never commit `.env`):

```bash
cp .env.example .env
```

**Port 5432 already in use?** If a local PostgreSQL is running, publish the container on another port:

```bash
POSTGRES_PORT=5433 docker compose up --build
```

or set `POSTGRES_PORT=5433` in `.env`. The same applies to `BACKEND_PORT`, `FRONTEND_PORT`, `MAILPIT_UI_PORT` and `MAILPIT_SMTP_PORT`.

Stop the stack with `docker compose down` (add `-v` to also delete the database volume).

## Local development

### Backend

```bash
cd backend
./gradlew bootTestRun        # starts the API with a PostgreSQL container (Testcontainers)
```

Or use the PostgreSQL service from Docker Compose:

```bash
docker compose up -d postgres
cd backend
./gradlew bootRun
```

Quality checks:

```bash
./gradlew build              # format check, tests (including architecture rules) and coverage gate
./gradlew spotlessApply      # format the code
```

The Spring Modulith module documentation is generated in `backend/build/spring-modulith-docs`.

### Frontend

```bash
cd frontend
pnpm install
pnpm dev                     # http://localhost:5173, proxies /api to http://localhost:8080
```

Quality checks:

```bash
pnpm lint                    # ESLint, including the import boundary rules
pnpm test                    # Vitest unit and architecture tests
pnpm test:e2e                # Playwright smoke tests (run `pnpm exec playwright install chromium` once)
```

## Contributing / branching

- The repository follows **Gitflow** ([ADR-0006](docs/adr/0006-flujo-de-trabajo-gitflow.md)): `main` holds released versions and `develop` integrates the work in progress.
- Create `feature/*`, `docs/*`, `chore/*` or `ci/*` branches from `develop` and open pull requests against `develop`; `release/x.y.z` and `hotfix/x.y.z` branches bring versions to `main` with an annotated `vX.Y.Z` tag.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/) with a scope, for example `feat(notebooks): ...`.
- CI (backend, frontend, end-to-end and Docker Compose checks) must pass before merging.

## Project documentation (in Spanish)

| | Document |
|---|---|
| 01 | [Project charter](docs/01_Formulacion_y_Constitucion.md) |
| 02 | [Scope and requirements](docs/02_Alcance_y_Especificacion_de_Requisitos.md) |
| 03 | [Backlog, user stories and use cases](docs/03_Backlog_Historias_y_Casos_de_Uso.md) |
| 04 | [Stakeholder and project management](docs/04_Gestion_de_Interesados_y_del_Proyecto.md) |
| 05 | [Architecture and design](docs/05_Arquitectura_y_Diseno.md) |

## Repository structure

```text
podcuaderno/
├── backend/              Spring Boot API (modular monolith)
├── frontend/             React SPA
├── docs/                 Project documentation and ADRs
├── .github/              CI workflows, Dependabot and templates
└── docker-compose.yml    Local stack: PostgreSQL, Mailpit, backend and frontend
```

## License

All rights reserved for now. The license will be defined in an ADR before the public beta.

## Author

**Sebastián** · Software development student (SENA, ADSO) · Medellín, Colombia
