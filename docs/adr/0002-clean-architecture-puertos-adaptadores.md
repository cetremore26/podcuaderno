# ADR-0002: Clean Architecture con puertos y adaptadores en cada módulo

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

El proyecto busca, además de funcionar, demostrar buenas prácticas de diseño. Tiene dependencias externas que probablemente cambien (proveedor de voz, pasarela de pagos, hosting) y reglas de negocio reales (estructura de cuadernos, propiedad de los recursos, límites de uso).

## Decisión

Cada módulo se organiza en **dominio**, **aplicación** (casos de uso y puertos) y **adaptadores** (entrada y salida), siguiendo la regla de dependencia de Clean Architecture: Adaptadores → Aplicación → Dominio.

- El **dominio** es Java puro: sin anotaciones de Spring, JPA ni Jackson.
- Los **casos de uso** se exponen como interfaces (puertos de entrada) y dependen de interfaces (puertos de salida).
- Las **entidades JPA** están separadas de las de dominio y se convierten con mappers escritos a mano.
- `@Transactional` se permite solo en los servicios de aplicación.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Capas clásicas (controller, service, repository) con entidades JPA como modelo | Menos código, pero el negocio queda acoplado a JPA y a Spring, y las pruebas necesitan base de datos. |
| Clean Architecture con entidades JPA anotadas en el dominio | Ahorra los mappers, pero rompe la regla de dependencia y no se podría verificar automáticamente. |

## Consecuencias

**Positivas:** el negocio se prueba en milisegundos y sin infraestructura; cambiar una tecnología solo toca su adaptador; la estructura es igual en todos los módulos y es fácil de navegar.

**Negativas (costos aceptados):** más archivos y más código (mappers, DTO, comandos); arranque más lento de cada historia. Para controlarlo se aplican las reglas antisobreingeniería del documento 05, sección 12.

**Cómo se verifica:** reglas de ArchUnit en el pipeline (ADR-0004).
