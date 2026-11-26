# ADR-0005: Código en inglés, documentación en español

- **Estado:** Aceptado
- **Fecha:** 2026-11-26
- **Decisor:** Sebastián

## Contexto

Los usuarios y la documentación están en español, pero el proyecto también es una pieza de portafolio para conseguir trabajo remoto, donde los equipos suelen trabajar en inglés. Mezclar idiomas en el código (`getCuaderno`, `saveConcepto`) reduce la legibilidad.

## Decisión

- **En inglés:** nombres de clases, métodos, variables, paquetes, tablas y columnas, endpoints, mensajes de commit y comentarios en el código.
- **En español:** documentación del proyecto (`docs/`), textos de la interfaz y mensajes de error que ve el usuario.
- **Excepción:** el `README.md` de la raíz va en inglés, porque es la portada del repositorio para reclutadores y revisores internacionales; enlaza a la documentación en español.
- La tabla de lenguaje ubicuo del documento 05 relaciona los términos del negocio con los del código.

## Alternativas consideradas

| Alternativa | Por qué no |
|---|---|
| Todo en español | Natural para el negocio, pero menos útil para un portafolio internacional y choca con las palabras reservadas y librerías en inglés. |
| Todo en inglés, incluida la documentación | La documentación también sirve al autor y a los usuarios de la beta, que hablan español. |

## Consecuencias

**Positivas:** código consistente y legible para cualquier revisor; práctica del inglés técnico.

**Negativas (costos aceptados):** hay que mantener la tabla de lenguaje ubicuo al día.

**Cómo se verifica:** revisión de código con el checklist del pull request.
