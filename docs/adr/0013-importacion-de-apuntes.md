# ADR-0013: Importación de apuntes (texto, TXT y Markdown)

- **Estado:** Aceptado
- **Fecha:** 2026-11-27
- **Decisor:** Sebastián

Enmienda el punto 4 de ADR-0010 (paquetes de adaptadores de salida).

## Contexto

El Sprint 2 entrega HU-07 (pegar texto o subir `.txt` y `.md`) y HU-09 (proponer una división cuando no hay títulos). Las fuerzas son:

- **RF-06 y RF-08:** `#` crea temas, `##` crea conceptos y, sin títulos, cada párrafo es un concepto titulado con sus primeras 6 palabras.
- **RNF-11:** archivo ≤ 5 MB y cuaderno ≤ 200 000 caracteres, sin decir qué se cuenta.
- **R-09 (abuso):** el endpoint recibe archivos grandes y gasta CPU parseando.
- **05 §5.5:** `importing` devuelve una vista previa y no conoce a `notebooks`; el frontend confirma la creación. Menciona una estrategia por formato, pero no dónde vive el *factory*, ni cómo llega el texto pegado (solo habla de *multipart*).
- **Vacíos de las historias:** HU-07 no define el texto antes del primer título ni los niveles `###` en adelante; HU-09 no da título al tema; CU-03 incluye al invitado, que es HU-21 (S6).
- **ADR-0010 §4:** ArchUnit solo reconoce los paquetes de adaptadores `out.persistence`, `out.event`, `out.email` y `out.security`.

## Decisión

### Lectura y formatos

1. **Markdown con commonmark-java 0.33.0** y su extensión de tablas GFM (BSD-2-Clause, sin dependencias en tiempo de ejecución). Se recorre el AST con un `AbstractVisitor` y **nunca se renderiza HTML**, así que no hay superficie XSS.
2. **Formatos:** `.md` y `.markdown` son Markdown; `.txt` es texto plano y sus marcas no se interpretan. El **texto pegado** se lee como Markdown si alguna línea empieza por `# ` o `## ` (con hasta 3 espacios delante, o es exactamente `#` o `##`); si no, como texto plano. Cualquier otra extensión responde 422 `unsupported-file-type`. El tipo MIME del navegador no se usa.
3. **Codificación de los archivos:** (1) con BOM de UTF-8, UTF-16LE o UTF-16BE se usa ese juego; (2) sin BOM, UTF-8 estricto; (3) si falla, Windows-1252 (el ANSI del Bloc de notas antiguo); (4) si el resultado tiene `\u0000` o más de un 1 % de `�`, 422 `unreadable-file`. El texto pegado ya llega en Unicode.
4. **Texto limpio para escuchar:** el título es el texto del encabezado y el concepto, sus párrafos unidos por una línea en blanco. Se conserva el texto del énfasis, la negrita y el código en línea; de un enlace queda el texto, sin la URL. Cada elemento de lista va en su línea, sin viñeta (las numeradas conservan «N.»). Las citas son párrafos normales. Los **bloques de código, tablas, imágenes y HTML se omiten y se cuentan** para informarlo. Se normaliza a NFC, se quitan los caracteres de control y de ancho cero y se colapsan los espacios. El tachado `~~x~~` queda literal.
5. **Párrafos del texto plano:** si hay alguna línea en blanco, un párrafo es un bloque separado por líneas en blanco; si no hay ninguna, cada línea no vacía es un párrafo.

### Reglas de detección (`StructureDetector`)

1. **Documento vacío** (sin bloques, o solo encabezados sin texto): 422 `empty-document`.
2. **Título sugerido:** el nombre del archivo sin ruta ni extensión, con `_` y `-` cambiados por espacio y la primera letra en mayúscula; si no hay nombre, el primer encabezado de nivel 1; si tampoco, «Mis apuntes».
3. **Modo títulos** si existe algún encabezado de nivel 1 o 2; si no, **modo párrafos** (HU-09).
4. **Modo títulos:** `#` abre un tema; `##` abre un concepto en el tema actual; los encabezados de nivel 3 o más, los párrafos y las listas se agregan como un párrafo más al concepto actual. El texto de un tema antes de su primer `##` es un concepto con el título del tema. El texto o los `##` antes del primer `#` forman un tema implícito: «Introducción» si el documento tiene algún `#`, o el título sugerido si no tiene ninguno. Un encabezado sin texto se titula «Tema sin título» o «Concepto sin título».
5. **Modo párrafos:** un solo tema con el título sugerido y un concepto por párrafo (una lista entera cuenta como un párrafo), titulado con sus **primeras 6 palabras** sin `.,;:` al final. El texto es el párrafo completo.
6. **Títulos de más de 150 caracteres:** se cortan en el último espacio y se añade «…», sin partir un par sustituto.
7. Los temas sin conceptos, los conceptos sin texto y los títulos repetidos **se conservan**: el editor (HU-11) permitirá completarlos o borrarlos.

### Límites (RNF-11)

- **Archivo ≤ 5 MiB.** El 413 se tipa como `/errors/payload-too-large`.
- **Cuaderno ≤ 200 000 caracteres**, contando la suma de `text.length()` de los conceptos (UTF-16, igual que `.length` en JavaScript): se cuenta lo que se escucha.
- Antes de parsear, el texto fuente no puede pasar de 400 000 caracteres (`text-too-long`).
- **≤ 500 temas y ≤ 5 000 conceptos** (`too-many-concepts`), para que 200 000 líneas de una letra no generen 200 000 filas.
- Las constantes se repiten a propósito en `importing`, `notebooks` y el frontend, porque los módulos no se conocen; cada copia lo dice en un comentario.

### API y despliegue

- **`POST /api/v1/imports`**, la misma ruta con dos contenidos: `multipart/form-data` con la parte `file` para archivos, y JSON `{"text": "…"}` para texto pegado. Responde **200 con la vista previa y no guarda nada**.
- **Exige sesión** (Bearer). Guardar exige cuenta y abrirlo a anónimos lo convertiría en un vector barato de denegación de servicio.
- **nginx:** `client_max_body_size 6m` (5 MB más el sobre *multipart*, y la creación de un cuaderno de 200 000 caracteres) y `limit_req` de 20 peticiones por minuto por IP con ráfaga de 10 en `/api/v1/imports` (429).
- **Creación con estructura:** `POST /api/v1/notebooks` gana un campo `topics` opcional, compatible con HU-06. El dominio de `notebooks` valida los mismos límites (422 `notebook-too-long` y `too-many-concepts`) y responde 201 con `Location`. `NotebookCreated` lleva los caracteres del cuaderno.
- **Vista de solo lectura:** `GET /api/v1/notebooks/{id}` y la pantalla `/cuadernos/:id`, para ver lo guardado. No es el editor.

### Arquitectura

- `DocumentReader` (estrategia por formato) y `DocumentReaderFactory` viven en `importing.application.port.out`. El *factory* es una clase Java pura que construye una `@Configuration` del adaptador con la lista de lectores. HU-08 solo agrega un lector DOCX.
- **Enmienda de ADR-0010 §4:** se reconoce el paquete de adaptadores de salida `adapter.out.document` (adaptador `documentReading` en `cleanLayers`, con su *fixture*).

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| flexmark-java | También BSD-2, pero mucho más grande y con decenas de módulos para lo que necesitamos. |
| Parser de Markdown propio | Frágil: títulos setext, `#` dentro de bloques de código, listas anidadas y escapes. |
| Leer los archivos en el navegador | Duplicaría la detección en TypeScript ahora; queda como opción para HU-21. |
| Endpoint de importación anónimo | Abre un vector de abuso (R-09) sin uso real en este sprint: guardar exige cuenta. |
| Detectar la codificación con ICU4J o juniversalchardet | Dependencias grandes para un caso que Windows-1252 resuelve. |
| Tabla de borradores de importación | Contradice que `importing` no guarda nada y obliga a depurar datos de apuntes. |

## Consecuencias

**Positivas:** reglas de detección explícitas y probadas; el texto queda listo para la voz y el usuario sabe qué se omitió; agregar Word (HU-08) no toca el servicio.

**Negativas (costos aceptados):**
- Un `.txt` nunca genera temas, aunque tenga `#`: hay que usar `.md` o pegarlo.
- El tachado queda literal.
- La decodificación con Windows-1252 puede equivocarse con otras codificaciones; se ve en la vista previa.
- La vista previa se pierde al recargar: no se guarda, lo que es más privado.
- El límite de nginx es por IP: detrás de un proxy futuro (HU-27) habrá que usar `real_ip`.

**Pendiente:**
- **HU-21 (invitado):** elegir entre (a) abrir `/api/v1/imports` a anónimos con un límite por IP en la aplicación, o (b) mover la lectura de TXT y MD al navegador, portando `StructureDetector` a TypeScript y dejando solo DOCX en el servidor. Para (a) basta un `requestMatchers(POST, "/api/v1/imports").permitAll()`.
- **HU-26:** límite de peticiones dentro de la aplicación.

**Cómo se verifica:** `StructureDetectorTests` (una prueba por regla), `ImportCorpusTests` (corpus sintético de 10 documentos, evidencia preliminar de OE-1), `ImportIntegrationTests` (413, 422 y 401) y el E2E del Sprint 2.
