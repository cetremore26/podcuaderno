# ADR-0003: Comunicación entre módulos por eventos de dominio

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

Algunos módulos deben reaccionar a lo que pasa en otros. Por ejemplo, al eliminar una cuenta en `identity` se deben borrar los cuadernos de `notebooks` y los eventos de `metrics`. Si `identity` llamara directamente a esos módulos, quedarían acoplados.

## Decisión

- Los módulos de negocio **no se llaman entre sí**. Se comunican publicando **eventos de dominio** (`UserDeleted`, `UserRegistered`, `NotebookCreated`) que otros módulos escuchan con `@ApplicationModuleListener` de Spring Modulith.
- Se usa el **registro de publicación de eventos** de Spring Modulith (persistido en la base de datos) para que un evento no se pierda si el listener falla; se reintenta al reiniciar.
- Los eventos llevan todos los datos que necesita quien los escucha, para que no tenga que consultar al módulo de origen.
- Cuando el frontend necesita combinar módulos (importar y luego crear un cuaderno), la orquestación la hace el frontend con dos llamadas a la API.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Llamadas directas entre servicios de distintos módulos | Acoplamiento y riesgo de ciclos entre módulos. |
| Broker de mensajes externo (RabbitMQ, Kafka) | Infraestructura extra innecesaria dentro de un solo proceso. Se reconsidera si un módulo se extrae como servicio. |

## Consecuencias

**Positivas:** módulos independientes; agregar un nuevo interesado en un evento no modifica al que lo publica (principio Abierto/Cerrado).

**Negativas (costos aceptados):** el flujo es menos evidente al leer el código; se mitiga con la documentación de módulos que genera Spring Modulith y con pruebas `@ApplicationModuleTest` de cada evento.

**Cómo se verifica:** `ApplicationModules.verify()` y pruebas de módulo que comprueban la publicación y el consumo de cada evento.
