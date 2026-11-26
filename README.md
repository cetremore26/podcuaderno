# Podcuaderno

**Turn your study notes into an audiobook.**
Paste or upload your notes, review the topics Podcuaderno detects, and download an audiobook with a bookmark for every topic and concept, ready for any audiobook app.

> 🚧 **Status:** planning complete, Sprint 0 in progress (proofs of concept). Public beta planned for April 2027.

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
| Quality | JUnit 5 · Testcontainers · ArchUnit · Vitest · Playwright · SonarQube |
| Delivery | Docker · GitHub Actions |

## Architecture

A **modular monolith** in which each business module (`identity`, `notebooks`, `importing`, `metrics`) follows **Clean Architecture with ports and adapters**. Modules communicate through domain events, and automated tests (Spring Modulith + ArchUnit) enforce the architecture rules on every pull request.

See [docs/05_Arquitectura_y_Diseno.md](docs/05_Arquitectura_y_Diseno.md) and the [architecture decision records](docs/adr/).

## Project documentation (in Spanish)

| | Document |
|---|---|
| 01 | [Project charter](docs/01_Formulacion_y_Constitucion.md) |
| 02 | [Scope and requirements](docs/02_Alcance_y_Especificacion_de_Requisitos.md) |
| 03 | [Backlog, user stories and use cases](docs/03_Backlog_Historias_y_Casos_de_Uso.md) |
| 04 | [Stakeholder and project management](docs/04_Gestion_de_Interesados_y_del_Proyecto.md) |
| 05 | [Architecture and design](docs/05_Arquitectura_y_Diseno.md) |

## Repository structure (planned)

```text
podcuaderno/
├── backend/     Spring Boot API (modular monolith)
├── frontend/    React SPA
├── docs/        Project documentation and ADRs
└── .github/     CI workflows and templates
```

## License

All rights reserved for now. The license will be defined in an ADR before the public beta.

## Author

**Sebastián** · Software development student (SENA, ADSO) · Medellín, Colombia
