# ADR-0010: Datos y eventos entre módulos

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

Complementa ADR-0003 y enmienda el punto 2 de ADR-0008.

## Contexto

Con HU-01 y HU-06 aparecen las primeras tablas de negocio y los primeros eventos entre módulos (`UserRegistered` y `NotebookCreated`, que escucha `metrics`). El modelo de datos inicial (02 §7) dibujaba claves foráneas de `notebooks` y `usage_events` hacia `users`, y 05 §5.2 ponía el puerto `DomainEventPublisher` dentro de `notebooks`. Ambas cosas chocan con la independencia de los módulos de ADR-0001 y ADR-0003: un módulo no debería conocer las tablas ni los tipos internos de otro.

## Decisión

1. **No hay claves foráneas entre tablas de módulos distintos.** Cada módulo es dueño de sus tablas. `notebooks.owner_id` y `usage_events.user_id` guardan el UUID del usuario sin referenciar `users`. Dentro de un mismo módulo sí hay claves foráneas (por ejemplo, `topics → notebooks`). La limpieza al eliminar una cuenta llegará con el evento `UserDeleted` (HU-04).
2. **Los eventos solo llevan `UUID` o tipos de `shared`** (por ejemplo, `UserId`), nunca tipos internos del módulo que publica: `NotebookCreated` lleva `UUID notebookId` y no `NotebookId`. Todos implementan la interfaz marcadora `shared.domain.DomainEvent` (`Instant occurredAt()`).
3. **`DomainEventPublisher` vive en `shared`.** Se amplía la estructura de `shared` definida en ADR-0008 §2:
   - `shared.application.port.out.DomainEventPublisher` (el puerto);
   - `shared.adapter.out.event.SpringDomainEventPublisher` (el adaptador, que delega en el `ApplicationEventPublisher` de Spring).
4. **Paquetes de adaptadores de salida reconocidos por ArchUnit:** `adapter.out.persistence`, `adapter.out.event`, `adapter.out.email` y `adapter.out.security`. `onionArchitecture()` exige que la capa de aplicación solo sea accedida por adaptadores declarados, así que un paquete de adaptador nuevo exige ampliar `cleanLayers` (y un fixture que lo pruebe).
5. **Infraestructura de toda la aplicación en la clase principal:** `@EnableAsync` (para los listeners de Modulith) y el bean `Clock` (UTC), que los servicios inyectan para que las pruebas controlen el tiempo.
6. **Un módulo puede escuchar sus propios eventos.** `identity` envía el correo de verificación desde un listener de `UserRegistered` y `EmailVerificationRequested`, dentro de su paquete `adapter.in.event`. Así la respuesta del registro no espera al servidor de correo, el token en claro se crea dentro del listener y nunca se guarda en `event_publication`, y si el envío falla la publicación queda incompleta y se reintenta al reiniciar.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Claves foráneas hacia `users` | Acopla los esquemas de los módulos e impide extraer uno sin migrar datos. |
| `DomainEventPublisher` en cada módulo | Duplica el mismo puerto y el mismo adaptador en cada módulo. |
| Publicar con `ApplicationEventPublisher` desde los servicios | Mete Spring en la capa de aplicación (lo prohíbe ArchUnit). |
| Eventos con tipos internos (`NotebookId`) | Modulith rompe si `metrics` usa un tipo interno de `notebooks`. |
| Enviar el correo dentro de la transacción del registro | La respuesta esperaría al SMTP y el tiempo delataría si el correo existe. |

## Consecuencias

**Positivas:** los módulos no comparten esquema ni tipos internos; los eventos son contratos pequeños y estables.

**Negativas (costos aceptados):**
- La base de datos no garantiza que `owner_id` apunte a un usuario existente; lo garantiza el flujo de eliminación (HU-04).
- `usage_events` puede duplicar una fila si Modulith reintenta una publicación.

**Cómo se verifica:** `ModularityTests` (Spring Modulith) y `CleanArchitectureTests` con sus *fixtures* (`ArchitectureRulesSelfTests`).
