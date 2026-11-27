# Bocetos del Sprint 2

**Proyecto:** Podcuaderno
**Historias:** HU-07, HU-09 y HU-03
**Estado:** aprobados por el responsable del producto el 27 de noviembre de 2026 (Definición de Listo, documento 03 §5)

Formato móvil de 360 px. Las decisiones de importación están en [ADR-0013](../adr/0013-importacion-de-apuntes.md) y las de recuperación de contraseña en [ADR-0014](../adr/0014-recuperacion-de-contrasena.md).

Cabecera y pie como en el Sprint 1. Todo se maneja con teclado, y los avisos usan `role="alert"` o `role="status"` según el componente `Alert`.

**B10. Mis cuadernos (cambia B6 y B7)**
```
┌────────────────────────────────────┐
│ Podcuaderno          Cerrar sesión │
├────────────────────────────────────┤
│ Mis cuadernos                      │
│ ┌────────────────────────────────┐ │
│ │       Importar apuntes         │ │ ← enlace con aspecto de botón
│ └────────────────────────────────┘ │   principal
│ ┌────────────────────────────────┐ │
│ │       + Nuevo cuaderno         │ │ ← secundario (cuaderno vacío)
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │ Bases de datos                 │ │ ← el título es un enlace al
│ │ Modificado hace 5 minutos      │ │   cuaderno (B16)
│ │ [Cambiar nombre]  [Eliminar]   │ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
 Vacío: "Todavía no tienes cuadernos."
        "Importa tus apuntes o crea un cuaderno vacío."
```

**B11. Importar, pegando texto (`/cuadernos/importar`)**
```
┌────────────────────────────────────┐
│ ← Mis cuadernos                    │
│ Importar apuntes                   │
│ Pega tu texto o sube un archivo    │
│ .txt o .md. Si usas # para los     │
│ temas y ## para los conceptos,     │
│ respetamos esa estructura.         │
│                                    │
│ ¿Cómo quieres traer tus apuntes?   │
│ (•) Pegar texto  ( ) Subir archivo │
│                                    │
│ Tus apuntes                        │
│ ┌────────────────────────────────┐ │
│ │ # Modelo relacional            │ │
│ │ Un modelo que organiza los     │ │
│ │ datos en tablas.               │ │
│ └────────────────────────────────┘ │
│ 1.834 de 200.000 caracteres        │
│ ┌────────────────────────────────┐ │
│ │        Ver vista previa        │ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
 Errores (bajo el campo):
  ! Pega tus apuntes para continuar.
  ! Tus apuntes tienen 250.000 caracteres y el límite es
    200.000. Divídelos en dos cuadernos.
 Mientras procesa: [Leyendo tus apuntes…] (deshabilitado)
```

**B12. Importar, subiendo un archivo, con los errores de tamaño y de tipo**
```
┌────────────────────────────────────┐
│ ¿Cómo quieres traer tus apuntes?   │
│ ( ) Pegar texto  (•) Subir archivo │
│                                    │
│ Archivo (.txt o .md, hasta 5 MB)   │
│ ┌────────────────────────────────┐ │
│ │ [Elegir archivo] clase-3.pdf   │ │
│ └────────────────────────────────┘ │
│ ! Por ahora solo puedes subir      │
│   archivos .txt o .md.             │
│ ┌────────────────────────────────┐ │
│ │        Ver vista previa        │ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
 Otros errores en el cliente:
  ! El archivo pesa 7,2 MB. El límite es 5 MB.
  ! El archivo está vacío.
  ! Elige un archivo para continuar.
 Errores del servidor (Alert arriba del formulario):
  413: El archivo pesa más de 5 MB.
  unreadable-file: No pudimos leer el archivo. Guárdalo como
     texto (UTF-8) y vuelve a intentarlo.
  empty-document: No encontramos texto en tus apuntes.
  text-too-long: Tus apuntes superan el límite de 200.000
     caracteres. Divídelos en dos cuadernos.
  too-many-concepts: Tus apuntes se dividen en más de 5.000
     conceptos. Agrega títulos con # y ## o divídelos en
     varios cuadernos.
  429: Hiciste muchas importaciones seguidas. Espera un
     minuto y vuelve a intentarlo.
  Sin red: No hay conexión. Revisa tu internet e inténtalo
     de nuevo.
```

**B13. Vista previa con títulos detectados (la misma ruta, paso 2)**
```
┌────────────────────────────────────┐
│ Revisa tus apuntes                 │
│ ┌────────────────────────────────┐ │
│ │ ✓ Usamos tus títulos: # para   │ │
│ │   los temas y ## para los      │ │
│ │   conceptos.                   │ │
│ └────────────────────────────────┘ │
│ 3 temas · 5 conceptos ·            │
│ 214 caracteres                     │
│ No incluimos 1 bloque de código    │
│ porque no se puede escuchar.       │
│                                    │
│ Nombre del cuaderno                │
│ ┌────────────────────────────────┐ │
│ │ Apuntes bases de datos         │ │
│ └────────────────────────────────┘ │
│                                    │
│ Introducción                (h2)   │
│  ▸ Introducción                    │
│ Modelo relacional           (h2)   │
│  ▸ Modelo relacional               │
│  ▸ Tablas                          │
│  ▸ Llaves                          │
│ Normalización               (h2)   │
│  ▸ Primera forma normal            │
│    (▸ = <details>: al abrirlo      │
│     muestra el texto; "(sin texto)"│
│     si está vacío)                 │
│ Podrás editar temas y conceptos    │
│ más adelante.                      │
│ ┌────────────────────────────────┐ │
│ │        Guardar cuaderno        │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │   Volver a mis apuntes         │ │ ← vuelve al paso 1 sin
│ └────────────────────────────────┘ │   perder lo pegado
└────────────────────────────────────┘
```

**B14. Vista previa sin títulos (HU-09)**
```
┌────────────────────────────────────┐
│ Revisa tus apuntes                 │
│ ┌────────────────────────────────┐ │
│ │ ℹ Tus apuntes no tienen        │ │
│ │   títulos. Creamos un tema con │ │
│ │   un concepto por párrafo, y   │ │
│ │   cada uno lleva sus primeras  │ │
│ │   6 palabras como título.      │ │
│ └────────────────────────────────┘ │
│ 1 tema · 4 conceptos ·             │
│ 1.204 caracteres                   │
│ Nombre del cuaderno                │
│ ┌────────────────────────────────┐ │
│ │ Mis apuntes                    │ │
│ └────────────────────────────────┘ │
│ Mis apuntes                 (h2)   │
│  ▸ La normalización es un proceso  │
│    que                             │
│  ▸ Una tabla está en primera       │
│  ▸ …                               │
│ [Guardar cuaderno]                 │
│ [Volver a mis apuntes]             │
└────────────────────────────────────┘
```

**B15. Guardar: validación y errores (dentro de B13 y B14)**
```
 Nombre vacío:      ! Escribe un nombre.
 Más de 150:        ! Usa máximo 150 caracteres.
 Mientras guarda:   [Guardando…] (deshabilitado)
 notebook-too-long o too-many-concepts: el mismo mensaje de B12
 Sin red / 500:     ⚠ No pudimos guardar el cuaderno. Inténtalo de
                      nuevo en unos minutos.
 Éxito: navega a B16.
```

**B16. Cuaderno importado, de solo lectura (`/cuadernos/:id`)**
```
┌────────────────────────────────────┐
│ ← Mis cuadernos                    │
│ Bases de datos               (h1)  │
│ 3 temas · 5 conceptos ·            │
│ 214 caracteres                     │
│ Pronto podrás editar y escuchar    │
│ este cuaderno.                     │
│                                    │
│ Modelo relacional           (h2)   │
│  ▾ Tablas                          │
│    Una tabla tiene filas y         │
│    columnas.                       │
│  ▸ Llaves                          │
│ Normalización               (h2)   │
│  ▸ Primera forma normal            │
└────────────────────────────────────┘
 Cargando: "Cargando tu cuaderno…"
 404: "Ese cuaderno ya no existe." [Volver a mis cuadernos]
 Vacío (creado con «+ Nuevo cuaderno»): "Este cuaderno todavía no
   tiene temas. Pronto podrás agregarlos."
 Error: "No pudimos cargar tu cuaderno." [Intentar de nuevo]
```

**B17. Iniciar sesión (cambia B4)**
```
┌────────────────────────────────────┐
│ Inicia sesión                      │
│ ┌────────────────────────────────┐ │
│ │ ✓ Listo. Tu contraseña cambió. │ │ ← solo al volver de B20
│ │   Inicia sesión con la nueva.  │ │
│ └────────────────────────────────┘ │
│ Correo / Contraseña / [ ] Mostrar  │
│ ┌────────────────────────────────┐ │
│ │            Entrar              │ │
│ └────────────────────────────────┘ │
│ ¿Olvidaste tu contraseña?          │ ← enlace nuevo
│ ¿No tienes cuenta? Crea una        │
└────────────────────────────────────┘
```

**B18. ¿Olvidaste tu contraseña? (`/recuperar-contrasena`)**
```
┌────────────────────────────────────┐
│ ¿Olvidaste tu contraseña?          │
│ Escribe el correo de tu cuenta y   │
│ te enviaremos un enlace para crear │
│ una nueva.                         │
│ Correo                             │
│ ┌────────────────────────────────┐ │
│ │ ana@correo.com                 │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │     Enviarme el enlace         │ │
│ └────────────────────────────────┘ │
│ ¿La recordaste? Inicia sesión      │
└────────────────────────────────────┘
 Errores: "Escribe tu correo." / "Escribe un correo válido…" /
   sin red / "No pudimos enviar el enlace. Inténtalo de nuevo en
   unos minutos."
```

**B19. Revisa tu correo (`/recuperar-contrasena/revisa-tu-correo`).** El texto es genérico.
```
┌────────────────────────────────────┐
│ Revisa tu correo                   │
│ Si existe una cuenta con           │
│ ana@correo.com, te enviamos un     │
│ enlace para crear una nueva        │
│ contraseña. Vence en 30 minutos y  │
│ solo sirve una vez.                │
│ ¿No lo ves? Busca en "Spam" o      │
│ "Promociones".                     │
│ ┌────────────────────────────────┐ │
│ │     Enviarme otro enlace       │ │
│ └────────────────────────────────┘ │
│ ✓ Listo. Si existe una cuenta con  │
│   ese correo, te llegará un enlace │
│   nuevo en unos minutos. Si lo     │
│   pediste hace menos de 2 minutos, │
│   espera un poco.                  │
│ Ir a iniciar sesión                │
└────────────────────────────────────┘
```

**B20. Nueva contraseña (`/restablecer-contrasena#token=…`)**
```
┌────────────────────────────────────┐   ┌────────────────────────────────────┐
│ Revisando tu enlace…               │   │ Crea una nueva contraseña          │
│ (role="status")                    │   │ Al guardarla cerraremos tu sesión  │
│                                    │   │ en todos tus dispositivos.         │
│                                    │   │ Nueva contraseña                   │
│                                    │   │ ┌────────────────────────────────┐ │
│                                    │   │ │ ••••••                         │ │
│                                    │   │ └────────────────────────────────┘ │
│                                    │   │ ! Usa al menos 8 caracteres.       │
│                                    │   │   Te faltan 2.                     │
│                                    │   │ [ ] Mostrar contraseña             │
│                                    │   │ ┌────────────────────────────────┐ │
│                                    │   │ │      Guardar contraseña        │ │
│                                    │   │ └────────────────────────────────┘ │
└────────────────────────────────────┘   └────────────────────────────────────┘
 Al guardar con éxito: navega a B17 con el aviso de éxito.
 Error 422 invalid-password: "Usa una contraseña de 8 a 128 caracteres."
 Si el enlace vence mientras escribe (422 del token): pasa a B21.
```

**B21. Enlace vencido, usado o inválido**
```
┌────────────────────────────────────┐
│ Este enlace ya no sirve            │
│ Venció, ya se usó o no es válido.  │
│ Los enlaces duran 30 minutos y     │
│ sirven una sola vez. Escribe tu    │
│ correo y te enviamos uno nuevo.    │
│ Correo                             │
│ ┌────────────────────────────────┐ │
│ │                                │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │     Enviarme otro enlace       │ │
│ └────────────────────────────────┘ │
│ Ir a iniciar sesión                │
└────────────────────────────────────┘
```
