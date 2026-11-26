# ADR-0006: Flujo de trabajo Gitflow

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

El documento 04 (§3.1) y el documento 05 (§9) fijaban Trunk-Based Development, y la Definición de Terminado decía que una historia está terminada cuando llega a `main` por pull request. El Product Owner decide usar una rama de integración, por tres razones:

- mantener `main` siempre desplegable;
- agrupar historias en versiones que coinciden con los hitos v0.1.0 a v1.0.0;
- mostrar en el portafolio un flujo de ramas explícito.

Además, HU-27 despliega a producción al crear la etiqueta `vX.Y.Z`.

## Decisión

- `main` es producción: solo recibe merges de `release/x.y.z` y `hotfix/x.y.z`, y cada merge lleva la etiqueta anotada `vX.Y.Z`.
- `develop` es la rama de integración.
- Las ramas de trabajo salen de `develop`: `feature/<slug>`, `docs/<slug>`, `chore/<slug>` y `ci/<slug>`, con el slug en inglés en kebab-case y una vida guía de menos de 3 días. Se integran a `develop` con pull request y `git merge --no-ff`.
- `release/x.y.z` sale de `develop`. Solo fija la versión y corrige errores. Se integra a `main` (con etiqueta) y luego a `develop`.
- `hotfix/x.y.z` sale de `main` y se integra a `main` (con etiqueta) y a `develop`.
- **La versión del build se cambia solo en las ramas `release/*` y `hotfix/*`.**
- `main` y `develop` están protegidas: solo se integra por pull request con los checks en verde, incluidas las pruebas de arquitectura (ADR-0004).
- Mensajes de commit: Conventional Commits en inglés con ámbito (ADR-0005).
  - Ámbitos permitidos: `identity`, `notebooks`, `importing`, `metrics`, `shared`, `architecture`, `backend`, `frontend`, `docker`, `ci`, `deps`, `adr`, `github`, `release`.
  - Tipos: `feat`, `fix`, `docs`, `test`, `ci`, `build`, `chore`, `refactor`, `perf`.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Trunk-Based Development | Lo descarta el Product Owner: no hay una rama que separe lo integrado de lo publicado. |
| GitHub Flow | Igual que el anterior: `main` sería a la vez integración y producción. |

## Consecuencias

**Positivas:** `main` estable, versiones claras e historial legible gracias a los merges `--no-ff`.

**Negativas (costos aceptados):** más merges; `develop` y `main` pueden alejarse si se demoran las versiones; un poco de ceremonia para una sola persona.

**Cómo se verifica:** protección de las ramas y CI en `push` y `pull_request` hacia `main` y `develop`.

Reemplaza lo indicado sobre Trunk-Based en 04 §3.1 y 05 §9.2. Complementa ADR-0004: la protección aplica a `main` y `develop`.
