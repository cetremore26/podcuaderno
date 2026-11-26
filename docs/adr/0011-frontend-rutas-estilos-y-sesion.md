# ADR-0011: Rutas, estilos y sesión en el frontend

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

El Sprint 1 trae las primeras pantallas (registro, verificación, inicio de sesión, política de datos y cuadernos). Hace falta decidir el enrutador, cómo se dan estilos sin sobreingeniería (05 §12) y dónde vive el token de acceso, cumpliendo WCAG 2.1 AA (RNF-07) y el funcionamiento a 360 px.

## Decisión

1. **react-router 8 en modo declarativo** (`BrowserRouter`, `Routes`, `Route`, `Navigate` y `Outlet`). Son pocas rutas y una guarda de sesión, sin cargadores de datos: de eso se encarga TanStack Query.
2. **CSS propio, sin librería de componentes**: una hoja `src/app/styles.css` *mobile-first* con variables CSS. Los diálogos usan el elemento nativo `<dialog>` con `showModal()`, que trae el foco atrapado y el cierre con Esc.
3. **El token de acceso vive solo en memoria** (`core/http/session.ts`), nunca en `localStorage` ni `sessionStorage`. Al recargar la página se recupera con `POST /api/v1/auth/refresh` gracias a la cookie `HttpOnly` (ADR-0009).
   - Las renovaciones se agrupan en **una sola petición en curso**: si varias peticiones reciben 401 a la vez, todas esperan la misma renovación.
   - Entre pestañas, la renovación se serializa con la **Web Locks API**, para que la detección de reutilización del refresh token no cierre la sesión por una carrera.
   - El frontend nunca envía `Authorization` a `/api/v1/auth/**`.
4. **Rutas de la interfaz en español** (`/registro`, `/iniciar-sesion`, `/verificar-correo`, `/politica-de-datos`, `/cuadernos`), porque son texto visible para el usuario (ADR-0005). Los endpoints de la API siguen en inglés.
5. **Botón deshabilitado con texto de ayuda.** HU-01 pide que el botón de registro esté deshabilitado si no se acepta la política. Un botón deshabilitado no recibe el foco, así que se acompaña de un texto de ayuda visible asociado con `aria-describedby` ("Para crear tu cuenta, acepta la política de datos.").

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| TanStack Router | Pide más configuración (o un plugin que genere las rutas) para siete rutas. |
| Modo *framework* o de datos de react-router | Cargadores y acciones duplicarían lo que ya hace TanStack Query. |
| Librería de componentes o Tailwind | Más dependencias y peso para pocas pantallas; los elementos nativos ya son accesibles. |
| Token en `localStorage` | Cualquier XSS lo podría robar. |
| Rutas en inglés | Son visibles en la barra de direcciones y en los enlaces del correo. |

## Consecuencias

**Positivas:** pocas dependencias, accesibilidad por defecto y un token que no sobrevive a un XSS persistente.

**Negativas (costos aceptados):** cada recarga hace una petición de renovación; los estilos se mantienen a mano.

**Cómo se verifica:** las pruebas de componentes con Vitest y Testing Library (incluida la guarda de sesión y la renovación única) y el E2E con Playwright, que recarga la página y conserva la sesión.
