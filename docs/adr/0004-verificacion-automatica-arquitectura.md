# ADR-0004: Verificación automática de la arquitectura

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

Las reglas de arquitectura que solo están en un documento se rompen con el tiempo, sobre todo cuando hay prisa. Con un solo desarrollador no hay otra persona que las haga cumplir en cada revisión.

## Decisión

Las reglas de arquitectura se escriben como pruebas automáticas y se ejecutan en cada pull request:

- **Spring Modulith** (`ApplicationModules.verify()`): sin ciclos entre módulos y sin acceso a clases internas de otro módulo.
- **ArchUnit**: dominio libre de frameworks, capas de Clean Architecture (`onionArchitecture`), sin inyección por atributo, convenciones de nombres.
- **ESLint** en el frontend: `core/` no importa React; las funcionalidades solo se importan por su `index.ts`.

Si una prueba de arquitectura falla, el pull request no se puede integrar.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Solo revisión manual | No escala y depende de la disciplina del momento. |
| Separar cada capa en un módulo de Maven o Gradle | Hace cumplir las dependencias al compilar, pero multiplica los archivos de build. Se puede adoptar más adelante si las pruebas no son suficientes. |

## Consecuencias

**Positivas:** la arquitectura documentada es la arquitectura real; los errores de diseño se detectan en minutos.

**Negativas (costos aceptados):** hay que mantener las reglas cuando cambia la estructura de paquetes.

**Cómo se verifica:** las propias pruebas son obligatorias en la protección de la rama `main`.
