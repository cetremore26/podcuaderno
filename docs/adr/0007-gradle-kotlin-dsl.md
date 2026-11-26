# ADR-0007: Gradle con Kotlin DSL para el backend

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

Faltaba elegir la herramienta de build del backend: el documento 05 y ADR-0004 dicen "Maven o Gradle".

## Decisión

- Gradle con Kotlin DSL y wrapper versionado (Gradle 9.9.2).
- Catálogo de versiones en `backend/gradle/libs.versions.toml` para lo que no gestiona el BOM de Spring Boot.
- Un solo proyecto Gradle, sin un módulo por capa (eso sigue siendo una alternativa de ADR-0004).
- Formato con Spotless y palantir-java-format.
- Cobertura con JaCoCo: mínimo 80 % de líneas en `..domain..` y `..application..`, verificado en la tarea `check`.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Maven | XML más verboso y builds incrementales y caché menos eficientes. Es más común en tutoriales, pero no aporta nada al proyecto. |
| Gradle con Groovy DSL | No tiene tipado ni autocompletado en el IDE. |

## Consecuencias

**Positivas:** builds incrementales y con caché, scripts tipados, y Dependabot entiende el catálogo de versiones.

**Negativas (costos aceptados):** curva de aprendizaje; la primera configuración del Kotlin DSL es más lenta.

**Cómo se verifica:** `./gradlew build` en CI.
