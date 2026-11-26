# ADR-0012: Proveedor de correo transaccional (Resend)

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

HU-01 envía el correo de verificación y HU-03 enviará el de recuperación de contraseña. El documento 04 (§8) dejaba pendiente el proveedor de correo transaccional, con la condición de usar un plan gratuito (RNF-03). En local y en el CI se usa Mailpit, que no entrega correos reales. Para probar el flujo completo con un correo real, y más adelante para la beta, hace falta un proveedor.

El backend ya envía por SMTP genérico (`JavaMailSender` detrás del puerto `EmailSender`), así que cambiar de proveedor es configuración, no código.

## Decisión

- **Resend** por **SMTP** (`smtp.resend.com`, puerto 587 con STARTTLS, usuario `resend` y la API key como contraseña).
- La configuración entra solo por variables de entorno (`SMTP_*` y `MAIL_FROM` en el `.env` de docker compose, que nunca se versiona). Sin ellas, todo sigue usando Mailpit. El CI y las pruebas automáticas **siempre** usan Mailpit o el `EmailSender` de prueba: nunca envían correos reales.
- La API key se crea con permiso **solo de envío**.
- **Mientras no haya dominio verificado,** el remitente es `onboarding@resend.dev` y Resend solo entrega al correo de la cuenta de Resend. Sirve para que el responsable pruebe el flujo con su propio correo.
- **Antes de la beta** (HU-27) se verifica el dominio propio en Resend (registros SPF y DKIM) y el remitente pasa a `no-responder@<dominio>`.
- Se usa SMTP y no la API HTTP de Resend para no acoplar el código a un proveedor: cualquier otro proveedor con SMTP se cambia por configuración.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Gmail SMTP con contraseña de aplicación | Expone la cuenta personal del responsable, tiene límites diarios bajos y sus condiciones no están pensadas para correo transaccional de una aplicación. |
| Brevo, Mailjet o SendGrid (plan gratuito) | Igual de viables por SMTP. Resend se elige por su configuración sencilla de dominio y su plan gratuito suficiente para la beta. Si cambia, basta con cambiar las variables. |
| API HTTP de Resend con su SDK | Acopla el adaptador a un proveedor y agrega una dependencia sin ganar nada para este volumen. |

## Consecuencias

**Positivas:** correos reales sin costo para la beta, sin cambios de código y sin secretos en el repositorio.

**Negativas (costos aceptados):**
- Sin dominio verificado solo llegan correos al responsable; la beta exige comprar y verificar el dominio (riesgo R-13).
- Resend es un **encargado del tratamiento** ubicado en Estados Unidos que recibe el correo electrónico de cada usuario. La política de datos (HU-05) debe mencionarlo, con la transferencia internacional, según la Ley 1581 de 2012.
- Hay que vigilar los límites del plan gratuito (consultarlos en el sitio de Resend; se revisan en el informe de la beta).

**Cómo se verifica:** el CI sigue en verde con Mailpit; prueba manual con docker compose y el `.env` de Resend: registrarse con el correo del responsable y abrir el enlace de verificación recibido.
