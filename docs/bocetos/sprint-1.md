# Bocetos del Sprint 1

**Proyecto:** Podcuaderno
**Historias:** HU-01, HU-02, HU-05 y HU-06
**Estado:** aprobados por el responsable del producto el 26 de noviembre de 2026 (Definición de Listo, documento 03 §5)

Formato móvil de 360 px. Todas las pantallas se usan también con teclado (Definición de Terminado). Las decisiones de rutas, estilos y sesión están en [ADR-0011](../adr/0011-frontend-rutas-estilos-y-sesion.md).

**Cabecera y pie comunes.** Sin sesión, la cabecera muestra "Podcuaderno" y "Iniciar sesión". Con sesión, muestra "Podcuaderno" y "Cerrar sesión". El pie lleva el enlace "Política de datos".

**B0. Inicio (`/`).** Muestra el título "Podcuaderno", el texto "Convierte tus apuntes en un audiolibro organizado por temas.", los botones [Crear cuenta] e [Iniciar sesión], y el enlace a la política. Con sesión, enlaza a "Mis cuadernos".

**B1. Registro (`/registro`)**
```
┌────────────────────────────────────┐
│ Podcuaderno         Iniciar sesión │
├────────────────────────────────────┤
│ Crea tu cuenta                     │
│ Guarda tus cuadernos y escúchalos  │
│ desde cualquier dispositivo.       │
│                                    │
│ Correo                             │
│ ┌────────────────────────────────┐ │
│ │ nombre@correo.com              │ │
│ └────────────────────────────────┘ │
│ Contraseña                         │
│ ┌────────────────────────────────┐ │
│ │ ••••••                         │ │
│ └────────────────────────────────┘ │
│ ! Usa al menos 8 caracteres.       │
│   Te faltan 2.                     │
│ [ ] Mostrar contraseña             │
│                                    │
│ [ ] Leí y acepto la política de    │
│     datos (versión 1.0).           │
│     Leer la política (se abre en   │
│     otra pestaña)                  │
│                                    │
│ ┌────────────────────────────────┐ │
│ │   Crear cuenta   (deshabilitado)│ │
│ └────────────────────────────────┘ │
│ Para crear tu cuenta, acepta la    │
│ política de datos.                 │
│                                    │
│ ¿Ya tienes cuenta? Inicia sesión   │
├────────────────────────────────────┤
│ Política de datos                  │
└────────────────────────────────────┘
```
Si el servidor falla, aparece arriba: "No pudimos crear tu cuenta. Inténtalo de nuevo en unos minutos."

**B2. Revisa tu correo (`/registro/revisa-tu-correo`).** El texto es genérico y vale tanto si el correo es nuevo como si ya existía.
```
┌────────────────────────────────────┐
│ Revisa tu correo                   │
│                                    │
│ Si ana@correo.com no tenía cuenta, │
│ te enviamos un enlace para         │
│ activarla. Vence en 24 horas.      │
│                                    │
│ Si ya tenías una cuenta con ese    │
│ correo, inicia sesión.             │
│                                    │
│ ¿No lo ves? Busca en "Spam" o      │
│ "Promociones".                     │
│ ┌────────────────────────────────┐ │
│ │     Enviarme otro enlace       │ │
│ └────────────────────────────────┘ │
│ ✓ Listo. Si tu cuenta está         │
│   pendiente, te llegará un enlace  │
│   nuevo en unos minutos.           │
│                                    │
│ Ir a iniciar sesión                │
└────────────────────────────────────┘
```

**B3. Verificación (`/verificar-correo#token=…`)**
```
┌────────────────────────────────────┐   ┌────────────────────────────────────┐
│ Activando tu cuenta…               │   │ Tu cuenta está activa              │
│ (estado con role="status")         │   │ Ya puedes guardar tus cuadernos.   │
│                                    │   │ ┌────────────────────────────────┐ │
│                                    │   │ │        Iniciar sesión          │ │
│                                    │   │ └────────────────────────────────┘ │
└────────────────────────────────────┘   └────────────────────────────────────┘
┌────────────────────────────────────┐
│ Este enlace ya no sirve            │
│ Venció o no es válido. Escribe tu  │
│ correo y te enviamos uno nuevo.    │
│ Correo                             │
│ ┌────────────────────────────────┐ │
│ │                                │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │     Enviarme otro enlace       │ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
```

**B4. Iniciar sesión (`/iniciar-sesion`), con los tres avisos posibles**
```
┌────────────────────────────────────┐
│ Podcuaderno            Crear cuenta│
├────────────────────────────────────┤
│ Inicia sesión                      │
│ ┌────────────────────────────────┐ │
│ │ ⚠ El correo o la contraseña no │ │  ← 401 (genérico)
│ │   son correctos.               │ │
│ └────────────────────────────────┘ │
│ Correo                             │
│ ┌────────────────────────────────┐ │
│ │ ana@correo.com                 │ │
│ └────────────────────────────────┘ │
│ Contraseña                         │
│ ┌────────────────────────────────┐ │
│ │ ••••••••••                     │ │
│ └────────────────────────────────┘ │
│ [ ] Mostrar contraseña             │
│ ┌────────────────────────────────┐ │
│ │            Entrar              │ │
│ └────────────────────────────────┘ │
│ ¿No tienes cuenta? Crea una        │
└────────────────────────────────────┘
 Aviso 429:  ⚠ Hiciste demasiados intentos. Espera 15 minutos
             y vuelve a intentarlo.
 Aviso 422:  ⚠ Primero activa tu cuenta con el enlace que te
             enviamos al correo.   [Enviarme otro enlace]
 Sin red:    ⚠ No hay conexión. Revisa tu internet e inténtalo de nuevo.
```
"¿Olvidaste tu contraseña?" no aparece: es HU-03, del S2.

**B5. Política de datos (`/politica-de-datos`)**
```
┌────────────────────────────────────┐
│ Política de tratamiento de datos   │
│ Versión 1.0 · Vigente desde el     │
│ 14 de diciembre de 2026            │
│                                    │
│ ¿Quién es el responsable?          │
│ ¿Qué datos guardamos?              │
│ ¿Para qué los usamos?              │
│ Lo que nunca hacemos               │
│ ¿Cuánto tiempo los guardamos?      │
│ Tus derechos (Ley 1581 de 2012)    │
│ ¿Cómo ejercerlos?                  │
│ Cambios a esta política            │
│ (cada sección con un párrafo corto)│
│                                    │
│ Volver al inicio                   │
└────────────────────────────────────┘
```

**B6. Mis cuadernos, vacío (`/cuadernos`)**
```
┌────────────────────────────────────┐
│ Podcuaderno          Cerrar sesión │
├────────────────────────────────────┤
│ Mis cuadernos                      │
│ ┌────────────────────────────────┐ │
│ │       + Nuevo cuaderno         │ │
│ └────────────────────────────────┘ │
│                                    │
│   Todavía no tienes cuadernos.     │
│   Crea uno para cada materia.      │
└────────────────────────────────────┘
```

**B7. Mis cuadernos, con elementos** (ordenados por última modificación)
```
┌────────────────────────────────────┐
│ Mis cuadernos                      │
│ ┌────────────────────────────────┐ │
│ │       + Nuevo cuaderno         │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │ Bases de datos                 │ │
│ │ Modificado hace 5 minutos      │ │
│ │ [Cambiar nombre]  [Eliminar]   │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │ Cálculo integral               │ │
│ │ Modificado ayer                │ │
│ │ [Cambiar nombre]  [Eliminar]   │ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
```
- Los botones llevan `aria-label` con el nombre del cuaderno, por ejemplo "Cambiar nombre de Bases de datos".
- Mientras carga: "Cargando tus cuadernos…".
- Si falla: "No pudimos cargar tus cuadernos." con el botón [Intentar de nuevo].

**B8. Crear o cambiar nombre** (diálogo modal; Esc o "Cancelar" lo cierran)
```
┌────────────────────────────────────┐
│ Nuevo cuaderno  (o: Cambiar nombre)│
│                                    │
│ Nombre del cuaderno                │
│ ┌────────────────────────────────┐ │
│ │ Bases de datos                 │ │
│ └────────────────────────────────┘ │
│ ! Escribe un nombre.  /  Usa máximo│
│   150 caracteres.                  │
│                                    │
│ [Cancelar]        [Crear / Guardar]│
└────────────────────────────────────┘
```

**B9. Confirmar la eliminación** (el foco inicial va a "Cancelar")
```
┌────────────────────────────────────┐
│ ¿Eliminar «Bases de datos»?        │
│                                    │
│ Se borrarán todos sus temas y      │
│ conceptos. No se puede deshacer.   │
│                                    │
│ [Cancelar]             [Eliminar]  │
└────────────────────────────────────┘
```
Si el cuaderno ya no existe (404): "Ese cuaderno ya no existe." y se refresca la lista.
