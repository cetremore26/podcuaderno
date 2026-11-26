# ADR-0009: Autenticación y sesiones

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

RNF-01 pide contraseñas con hash fuerte, un token de acceso JWT de 15 minutos, un refresh token en cookie `HttpOnly`, `Secure` y `SameSite`, CORS limitado al frontend y un límite de intentos de inicio de sesión. HU-02 exige además que la renovación sea transparente y que cerrar sesión invalide el refresh token en el servidor. Los riesgos R-06 (acceso a datos ajenos) y R-09 (abuso) del documento 04 obligan a que las respuestas no revelen si un correo está registrado.

Hay un solo servicio que emite y valida los tokens, un solo despliegue y un presupuesto que no admite infraestructura adicional (Redis, un proveedor de identidad externo).

## Decisión

1. **Contraseñas con Argon2id** (`Argon2PasswordEncoder(16, 32, 1, 19_456, 2)`: los parámetros mínimos de OWASP, m = 19 MiB, t = 2, p = 1), envuelto en un `DelegatingPasswordEncoder` con el prefijo `{argon2id}` para poder migrar de algoritmo sin invalidar los hashes existentes. Requiere Bouncy Castle (`bcprov-jdk18on`) en tiempo de ejecución.
2. **Token de acceso JWT HS256**, firmado con `NimbusJwtEncoder` y validado con `NimbusJwtDecoder` mediante el starter `spring-boot-starter-security-oauth2-resource-server`. Claims: `sub` (UUID del usuario), `role`, `iat` y `exp` (15 minutos). Sin `iss`.
   - La clave sale de la variable `JWT_SECRET` (base64, al menos 32 bytes). Es opcional: si falta, se genera una clave aleatoria al arrancar y se registra un WARN.
3. **Refresh token opaco**: 32 bytes de `SecureRandom` en base64url. La base de datos solo guarda su **hash SHA-256 en hexadecimal** (tabla `refresh_tokens`). Dura 14 días.
   - Se **rota** en cada `POST /api/v1/auth/refresh`: el token presentado queda marcado como rotado y se emite uno nuevo de la misma familia (`family_id`).
   - Si se presenta un token ya rotado, se asume robo y se **revoca toda la familia**. La lectura usa un bloqueo `PESSIMISTIC_WRITE` para que dos renovaciones simultáneas no pasen ambas.
   - Para que la detección de reutilización no cierre sesiones por la carrera entre pestañas, el frontend serializa la renovación con la Web Locks API (ADR-0011).
   - `POST /api/v1/auth/logout` revoca la familia y borra la cookie.
4. **Cookie del refresh**: `pc_refresh`; `HttpOnly`, `Secure` (configurable con `REFRESH_COOKIE_SECURE`, por defecto `true`), `SameSite=Strict`, `Path=/api/v1/auth` y `Max-Age` igual a la vida del token.
5. **CSRF desactivado en Spring Security.** Todos los endpoints, salvo `/refresh` y `/logout`, se autentican con `Authorization: Bearer`, que un sitio ajeno no puede enviar. Los dos endpoints que leen la cookie quedan protegidos por `SameSite=Strict`, por la lista blanca de CORS y por la ruta restringida de la cookie. La respuesta de `/refresh` (el token en el cuerpo) no se puede leer desde otro origen.
6. **CORS** con lista blanca (`podcuaderno.security.allowed-origins`, por defecto `http://localhost:5173,http://localhost:3000`), métodos GET, POST, PATCH y DELETE, cabeceras `Authorization` y `Content-Type`, y credenciales permitidas. En Docker y en producción todo es del mismo origen (nginx hace de proxy de `/api`), así que CORS es defensa en profundidad.
7. **Límite de intentos propio y en memoria**: 5 fallos en 15 minutos por correo normalizado (exista o no la cuenta) responden 429 con `Retry-After`. La regla vive en el dominio (`LoginThrottle`) y los instantes de fallo en un puerto (`LoginAttemptTracker`) implementado en memoria. Un acierto reinicia el contador. El mapa se poda al superar 10 000 claves.
8. **Respuestas genéricas**:
   - El registro responde 202 sin cuerpo, exista o no el correo, y calcula el hash en ambos casos.
   - El inicio de sesión responde un 401 genérico para correo desconocido y contraseña incorrecta; para un correo desconocido compara igual contra un hash ficticio, para que el tiempo de respuesta no lo delate.
   - El aviso de cuenta sin verificar (422 `email-not-verified`) solo aparece con la contraseña correcta.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| bcrypt | Trunca o rechaza contraseñas de más de 72 bytes; con tildes y ñ ese límite obliga a validar bytes en lugar de caracteres. |
| Firma RSA o EC | Solo aporta cuando otro servicio valida los tokens con la clave pública. Aquí un solo servicio emite y valida. |
| jjwt | Otra dependencia, y no se integra con `oauth2ResourceServer()` de Spring Security. |
| Bucket4j | Su modelo de *token bucket* encaja peor con "5 fallos en 15 minutos, un acierto reinicia" y agrega una dependencia. |
| Tabla de intentos en la base de datos | Escribe en cada fallo y no aporta nada con un solo despliegue. |
| Token de acceso en `localStorage` | Cualquier XSS lo podría robar. |
| `SameSite=Lax` | Deja pasar navegaciones de nivel superior desde otros sitios; `Strict` no rompe nada porque la SPA llama a la API desde el mismo sitio. |
| Token CSRF de doble envío | Más complejo y sin beneficio adicional con `SameSite=Strict`, el mismo origen y la ruta restringida. |

## Consecuencias

**Positivas:** con la base de datos filtrada no se puede suplantar a nadie (solo hay hashes). Un refresh token robado se detecta al primer uso del legítimo. La API sigue sin estado salvo por los refresh tokens.

**Negativas (costos aceptados):**
- Si no se configura `JWT_SECRET`, reiniciar invalida los tokens de acceso vigentes (máximo 15 minutos); los refresh tokens siguen sirviendo porque viven en la base de datos.
- Un tercero puede bloquear 15 minutos el inicio de sesión de una víctima si conoce su correo. El límite por IP llega con HU-26.
- Reiniciar borra los contadores del límite de intentos.
- Safari no trata `http://localhost` como contexto seguro: para desarrollar con Safari hay que usar `REFRESH_COOKIE_SECURE=false`.
- Un CSRF desde un subdominio hermano solo podría forzar un cierre de sesión.
- Cada hash de Argon2id usa unos 19 MiB de memoria; el límite de intentos acota el abuso.
- Los refresh tokens vencidos todavía no se limpian (tarea para HU-28).

**Cómo se verifica:** las pruebas unitarias y de integración del módulo `identity` (rotación, reutilización, cierre de sesión, atributos de la cookie, 429 con `Retry-After`) y el E2E del flujo de registro a cuaderno.
