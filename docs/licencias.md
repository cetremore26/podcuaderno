# Inventario de licencias

**Proyecto:** Podcuaderno
**Requisito:** RNF-14 (cada librería y cada modelo de voz tiene una licencia compatible con uso comercial)
**Actualizado:** 27 de noviembre de 2026

Solo se listan las dependencias directas. Las versiones exactas están en `backend/gradle/libs.versions.toml`, `backend/gradle.lockfile` y `frontend/pnpm-lock.yaml`. Una dependencia nueva se agrega aquí en el mismo PR que la incorpora.

## Backend

| Dependencia | Uso | Licencia | Uso comercial |
|---|---|---|---|
| Spring Boot (starters web, seguridad, datos, validación, correo, actuator) | Base de la aplicación | Apache-2.0 | Sí |
| Spring Modulith | Módulos y eventos entre módulos | Apache-2.0 | Sí |
| springdoc-openapi | Documentación OpenAPI | Apache-2.0 | Sí |
| Flyway | Migraciones de la base de datos | Apache-2.0 | Sí |
| PostgreSQL JDBC | Controlador de la base de datos | BSD-2-Clause | Sí |
| Bouncy Castle (`bcprov-jdk18on`) | Hash Argon2id de contraseñas | MIT | Sí |
| commonmark-java y `commonmark-ext-gfm-tables` | Lectura de Markdown al importar (ADR-0013) | BSD-2-Clause | Sí |
| ArchUnit | Pruebas de arquitectura (solo pruebas) | Apache-2.0 | Sí |
| Testcontainers | Pruebas de integración (solo pruebas) | MIT | Sí |

## Frontend

| Dependencia | Uso | Licencia | Uso comercial |
|---|---|---|---|
| React y React DOM | Interfaz | MIT | Sí |
| react-router | Rutas | MIT | Sí |
| TanStack Query | Estado del servidor | MIT | Sí |
| React Hook Form y `@hookform/resolvers` | Formularios | MIT | Sí |
| Zod | Validación de esquemas | MIT | Sí |
| Vite y Vitest | Compilación y pruebas (solo desarrollo) | MIT | Sí |
| Playwright | Pruebas de extremo a extremo (solo desarrollo) | Apache-2.0 | Sí |

## Modelos de voz

Pendiente (HU-14). Cada modelo o voz se registra aquí con su licencia antes de incorporarlo.
