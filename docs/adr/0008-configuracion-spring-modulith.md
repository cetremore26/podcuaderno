# ADR-0008: Configuración de Spring Modulith

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

Complementa ADR-0003.

## Contexto

Los eventos de dominio viven en `<módulo>.domain.event`, que Spring Modulith considera un paquete interno del módulo, y `shared` lo usan los dominios de todos los módulos. Además, el `onionArchitecture()` de ArchUnit también incluye a `shared`. Hace falta decidir cómo se declaran los módulos para que Modulith y ArchUnit verifiquen los límites sin falsos positivos.

## Decisión

1. Cada módulo de negocio declara en su `package-info.java`: `@ApplicationModule(displayName = "...", allowedDependencies = "shared")`. Es una lista blanca explícita: `verify()` falla si hay una dependencia que no está declarada.
2. `shared` se declara `@ApplicationModule(type = ApplicationModule.Type.OPEN)`. Sus tipos (`UserId`, las excepciones base y, más adelante, `AuthenticatedUser`) los consumen todos los módulos.
   - Estructura interna de `shared`: `shared.domain` (tipos y `shared.domain.exception`) y `shared.adapter.in.web` (manejo global de errores y Spring Security).
   - No hay un paquete `config`: la configuración de Spring vive en el adaptador al que pertenece.
   - Una regla de ArchUnit (`sharedKernelIsIndependent`) prohíbe que `shared` dependa de un módulo de negocio.
3. Los eventos de dominio se publican en `<módulo>.domain.event`, cuyo `package-info.java` lleva `@NamedInterface("events")`. Los módulos que escuchan amplían su lista: `allowedDependencies = {"shared", "identity :: events"}`.
   - Esto se agrega en la historia que crea cada evento (HU-01, HU-04 y HU-06), no antes, porque Modulith no puede resolver interfaces nombradas que todavía no existen.
   - El `package-info` es metadato del paquete, no código de dominio. Por eso las reglas de ArchUnit excluyen las clases `package-info`.
4. Adaptación de ArchUnit:
   - `onionArchitecture()` se aplica a todos los módulos, incluido `shared` (`shared.domain` es la capa de dominio y `shared.adapter.in.web` el adaptador web).
   - Se usa `withOptionalLayers(true)` y cada regla lleva `allowEmptyShould(true)`, para que los paquetes vacíos no hagan fallar las pruebas.
   - Un conjunto de fixtures de prueba (`fixtures.architecture.*`, fuera de `com.podcuaderno` para que Spring no los escanee) demuestra dos cosas: que la estructura completa de 05 §5.2 cumple todas las reglas, y que las reglas detectan las violaciones.
5. El Event Publication Registry usa JPA. La tabla `event_publication` (esquema v2 de Modulith 2.2) se crea con Flyway, con `republish-outstanding-events-on-restart=true` y `completion-mode=delete` (por privacidad y para que la tabla no crezca).
6. La documentación de los módulos (`Documenter`) se genera en cada build y se publica como artefacto del CI.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Eventos en el paquete raíz del módulo | Rompe la estructura de 05 §5.2. |
| `shared` cerrado, con interfaces nombradas | Más configuración y sin beneficio real: la regla de dependencia ya la vigila ArchUnit. |
| Anotar los tipos de evento con `@NamedInterface` | Mete el framework en las clases de dominio. |

## Consecuencias

**Positivas:** los límites entre módulos quedan explícitos y verificados.

**Negativas (costos aceptados):** hay que mantener `allowedDependencies` al agregar eventos.

**Cómo se verifica:** `ModularityTests` y `CleanArchitectureTests`.
