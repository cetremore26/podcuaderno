# ADR-0001: Monolito modular en lugar de microservicios

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

El proyecto lo construye una sola persona con 6 a 8 horas por semana y un presupuesto de operación de máximo 10 USD al mes. Aun así, se quiere una arquitectura que permita crecer (voz premium, suscripciones) sin reescribir el sistema.

## Decisión

El backend es **un solo despliegue de Spring Boot dividido en módulos de negocio** (`identity`, `notebooks`, `importing`, `metrics` y, en la v2, `premiumvoice` y `subscriptions`), con límites verificados por Spring Modulith.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Microservicios | Multiplica la infraestructura (varios despliegues, red, monitoreo distribuido, consistencia eventual) sin beneficio para un equipo de una persona y un tráfico bajo. También eleva el costo de hosting. |
| Monolito por capas sin módulos | Simple al inicio, pero con el tiempo todo termina dependiendo de todo y es difícil extraer partes. |

## Consecuencias

**Positivas:** un solo despliegue, transacciones locales simples, depuración fácil y bajo costo. Los módulos se pueden extraer como servicios si algún día hace falta (por ejemplo, el worker de voz premium).

**Negativas (costos aceptados):** todos los módulos escalan juntos; un error grave puede afectar a toda la aplicación.

**Cómo se verifica:** `ApplicationModules.verify()` en el pipeline (ADR-0004).
