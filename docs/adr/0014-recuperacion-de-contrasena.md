# ADR-0014: Recuperación de contraseña

- **Estado:** Aceptado
- **Fecha:** 2026-11-27
- **Decisor:** Sebastián

Complementa ADR-0009 (sesiones) y ADR-0010 §6 (un módulo escucha sus propios eventos).

## Contexto

HU-03 (RF-02) pide un enlace de recuperación válido por 30 minutos y de un solo uso, y que al abrir un enlace usado o vencido se vea un mensaje claro con la opción de pedir otro. La historia no dice qué pasa con una cuenta pendiente de verificar ni con las sesiones abiertas.

El Sprint 1 ya tiene enlaces de verificación de correo (`EmailVerificationToken`, tabla `email_verification_tokens`) con el mismo ciclo de vida: se guarda solo el hash SHA-256, vence y sirve una vez. El *quality gate* de SonarCloud exige una duplicación ≤ 3 %, así que dos clases casi iguales no pasan. Las respuestas no deben revelar si un correo está registrado (ADR-0009 §8).

## Decisión

1. **Tabla propia `password_reset_tokens`** (migración V6), con `user_id`, `token_hash` único, `created_at`, `expires_at` y `used_at`, y borrado en cascada desde `users`.
2. **Ciclo de vida compartido:** `SingleUseToken` (dominio) modela la expiración y el uso único, y lo usan por composición `EmailVerificationToken` (sin cambiar su API pública) y `PasswordResetToken`. En JPA, `@MappedSuperclass SingleUseTokenJpaEntity`.
3. **Vigencia y espera:** el enlace dura **30 minutos** y sirve **una vez**; entre dos envíos al mismo usuario hay una espera de **2 minutos**.
4. **Petición con respuesta genérica:** `POST /api/v1/auth/password-resets {email}` responde **202 siempre**, exista o no la cuenta. Si existe (pendiente o activa) se publica `PasswordResetRequested`; su *listener* en `identity` crea el token y envía el correo de forma **asíncrona**, para que el token en claro nunca llegue a `event_publication` y el tiempo de respuesta no delate el correo.
5. **Enlace en el fragmento:** `FRONTEND_URL/restablecer-contrasena#token=…`. El fragmento no viaja en la petición HTTP ni queda en los registros del servidor ni en el `Referer`; el frontend lo lee y lo borra de la barra de direcciones.
6. **Comprobación al abrirlo:** `POST /api/v1/auth/password-resets/checks {token}` responde 204 si el enlace se puede usar o 422 `invalid-password-reset-token`. Así el aviso de «enlace vencido» aparece al abrir el enlace, como pide HU-03.
7. **Cambio:** `POST /api/v1/auth/password-resets/confirmations {token, password}` responde 204. El token se lee con `PESSIMISTIC_WRITE` para que dos confirmaciones simultáneas no pasen ambas. Efectos:
   1. guarda el nuevo hash Argon2id;
   2. marca el token como usado;
   3. invalida los demás enlaces pendientes del usuario;
   4. **revoca todos sus refresh tokens** (se cierran todas las sesiones);
   5. borra los intentos fallidos de inicio de sesión;
   6. si la cuenta estaba pendiente, la **activa**: abrir el enlace prueba que el correo es suyo.
8. Una contraseña débil (422 `invalid-password`) **no** consume el enlace.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Reutilizar `email_verification_tokens` con una columna de tipo | Obliga a modificar consultas que ya funcionan y mezcla retenciones distintas. |
| JWT firmado como enlace | No se puede marcar como usado sin guardar estado, así que no sería de un solo uso. |
| Sin endpoint de comprobación | El usuario solo sabría que el enlace venció después de escribir la contraseña: no cumple «al abrirlo». |
| Correo de aviso al cambiar la contraseña | Útil, pero se difiere a HU-26 (seguridad reforzada). |

## Consecuencias

**Positivas:** se cumple RF-02 sin duplicar código; un enlace robado deja de servir al usarse una vez; restablecer la contraseña cierra las sesiones de un posible atacante.

**Negativas (costos aceptados):**
- Los access tokens ya emitidos (JWT sin estado, 15 minutos) siguen válidos hasta vencer.
- No se avisa por correo del cambio (HU-26).
- Los tokens vencidos no se depuran todavía (HU-28).

**Cómo se verifica:** `SingleUseTokenTests`, `PasswordResetTokenTests`, `EmailVerificationTokenTests` sin cambios, `ResetPasswordServiceTests` y `PasswordResetFlowIntegrationTests` (la contraseña vieja da 401, la nueva 200 y el refresh anterior 401).
