# Documentación del proyecto Podcuaderno

Aplicación web que convierte apuntes de estudio en un audiolibro con marcadores por tema y concepto. Modelo freemium: voz gratuita en el navegador y voz premium por suscripción (v2). Stack: Java 25 + Spring Boot 4, React + TypeScript, PostgreSQL.

| # | Documento | Qué responde |
|---|---|---|
| 01 | [Formulación y constitución](01_Formulacion_y_Constitucion.md) | ¿Por qué existe el proyecto, qué se va a lograr, con qué límites y para cuándo? |
| 02 | [Alcance y especificación de requisitos](02_Alcance_y_Especificacion_de_Requisitos.md) | ¿Qué debe hacer el sistema y con qué calidad? ¿Qué queda por fuera? |
| 03 | [Backlog, historias de usuario y casos de uso](03_Backlog_Historias_y_Casos_de_Uso.md) | ¿En qué piezas se divide el trabajo y en qué orden se entrega? |
| 04 | [Gestión de interesados y del proyecto](04_Gestion_de_Interesados_y_del_Proyecto.md) | ¿Quién está involucrado y cómo se planea, controla y cierra el trabajo? |
| 05 | [Arquitectura y diseño](05_Arquitectura_y_Diseno.md) | ¿Cómo se construye? Clean Architecture, SOLID, estándares de código y pruebas. |

Trazabilidad: **Objetivos (01) → Requisitos (02) → Historias y casos de uso (03) → Sprints y riesgos (04)**. El documento 05 define cómo se construye todo lo anterior.

Carpetas que se crearán durante el proyecto:

- [`adr/`](adr/): registros de decisiones de arquitectura (ADR-0001 a ADR-0011 ya registrados; `0000-plantilla.md` para los nuevos).
- [`bocetos/`](bocetos/): bocetos aprobados de las pantallas de cada sprint.
- `retros/`: retrospectivas de cada sprint.
