# Formulación y constitución del proyecto

**Proyecto:** Podcuaderno
**Documento:** Acta de constitución (Project Charter)
**Versión:** 0.3 (borrador para aprobación)
**Fecha:** 25 de noviembre de 2026
**Autor y responsable:** Sebastián

---

## 1. Resumen

Podcuaderno es una **aplicación web** que convierte apuntes de estudio en un **audiolibro para escuchar en el celular**. Cualquier persona, sin conocimientos técnicos, entra desde el navegador, pega o sube sus apuntes (texto, Markdown o Word), revisa los temas que la aplicación detecta y descarga un audiolibro con un marcador por tema y por concepto, listo para escucharlo en cualquier app de audiolibros.

El modelo es **freemium**:

- **Gratis:** la voz se genera en el propio navegador del usuario. Es menos natural, pero no le cuesta nada al usuario ni al proyecto, y los apuntes no salen del dispositivo.
- **Premium (versión 2):** voz neuronal de alta calidad generada en el servidor, con suscripción mensual y límites de uso según el plan.

## 2. Antecedentes y problema

Estudiar exige tiempo frente a una pantalla o un cuaderno, y muchas personas que estudian también trabajan. El tiempo de transporte, de ejercicio o de tareas manuales se pierde para el estudio.

Convertir apuntes propios en audio hoy exige una de estas opciones:

1. **Leerlos y grabarse.** Toma horas.
2. **Usar el lector de voz del celular.** No genera un archivo, no recuerda dónde quedaste y no permite saltar a un tema.
3. **Usar herramientas de texto a voz genéricas.** Están pensadas para documentos corridos, no para material de estudio organizado por temas, y las de buena calidad son de pago y en inglés.

El autor ya comprobó el valor de la idea con su propio glosario: 185 conceptos convertidos en un audiolibro de 98 minutos con marcadores. Pero ese proceso fue manual y solo lo puede repetir alguien que sepa programar.

## 3. Propuesta de valor

> **Tus apuntes, convertidos en un audiolibro organizado por temas, en minutos y desde el navegador.**

| Para | Que | Podcuaderno | A diferencia de |
|---|---|---|---|
| Estudiantes que también trabajan o se desplazan mucho | quieren repasar sin mirar una pantalla | convierte sus propios apuntes en un audiolibro con un marcador por tema y concepto | los lectores de voz y las herramientas genéricas, que no organizan el contenido ni generan un archivo para estudiar |

## 4. Justificación

- **Valor para el usuario:** aprovechar tiempo muerto para estudiar, con material propio y navegable.
- **Valor profesional:** es un producto full stack completo con Java 25 y Spring Boot 4, React con TypeScript, PostgreSQL, seguridad, procesamiento de voz en el navegador, CI/CD y despliegue. Coincide con el perfil "Java full stack" que el autor busca para su transición a trabajo remoto.
- **Valor económico potencial:** el plan premium puede generar ingresos. Antes de construirlo se valida que haya usuarios reales (ver objetivo OE-5).

## 5. Objetivos

### 5.1 Objetivo general

Lanzar, a más tardar el **18 de abril de 2027**, una versión pública gratuita (`v1.0.0`) que permita a cualquier persona convertir sus apuntes en un audiolibro con marcadores, y medir durante 30 días si hay demanda suficiente para construir el plan premium.

### 5.2 Objetivos específicos

| ID | Objetivo | Indicador de cumplimiento |
|---|---|---|
| OE-1 | Importar apuntes y detectar su estructura de temas y conceptos. | Detecta correctamente ≥ 90 % de los títulos en un conjunto de 10 documentos de prueba. |
| OE-2 | Generar la voz gratuita en el navegador, sin costo de servidor. | Un cuaderno de 20 000 caracteres se genera en menos de 10 minutos en un celular de gama media (meta a confirmar en el Sprint 0). |
| OE-3 | Entregar el audiolibro en formatos estándar. | El M4B abre con todos sus marcadores en BookPlayer y Apple Books; los MP3 en cualquier reproductor. |
| OE-4 | Proteger las cuentas y los datos de los usuarios. | Cero hallazgos críticos o altos en OWASP ZAP y SonarQube; cumplimiento de la Ley 1581 de 2012. |
| OE-5 | Validar la demanda antes de invertir en el plan premium. | 20 usuarios en la beta y ≥ 30 % de ellos genera un segundo audiolibro dentro de 30 días. |
| OE-6 | Construir con calidad profesional. | Cobertura ≥ 80 % en la lógica del backend, pipeline de CI/CD y documentación completa. |

## 6. Alcance por versiones

| Versión | Contenido | Estado |
|---|---|---|
| **v1.0 (MVP gratuito)** | Cuentas de usuario, cuadernos de apuntes, importación de TXT, Markdown y Word, detección y edición de temas, voz gratuita en el navegador, exportación M4B y MP3, modo invitado, panel de métricas. | Planificada en detalle en este documento y en el 03. |
| **v2 (premium)** | Voz premium en el servidor, planes y suscripción mensual con Mercado Pago, límites de uso, almacenamiento de audiolibros. | Se decide después de medir la v1.0 (punto de decisión, sección 11). |
| **v3** | Podcast privado por usuario, compartir cuadernos, importación de PDF, otros idiomas. | Ideas, sin compromiso. |

**No incluye:** aplicación móvil nativa, generación de contenido con IA, edición colaborativa en tiempo real ni voces clonadas.

El detalle está en **02_Alcance_y_Especificacion_de_Requisitos**.

## 7. Entregables principales de la v1.0

1. Aplicación web en producción, con dominio propio y HTTPS.
2. API REST documentada con OpenAPI.
3. Repositorio en GitHub (monorepo: `frontend/`, `backend/`, `docs/`) con CI/CD.
4. Política de tratamiento de datos personales publicada en la aplicación.
5. Informe de la beta con las métricas de OE-5 y la recomendación sobre la v2.
6. Documentación del proyecto en `docs/`.

## 8. Criterios de éxito

- Una persona sin conocimientos técnicos genera su primer audiolibro en menos de 5 minutos sin ayuda (prueba con 5 personas).
- El audiolibro se escucha en un iPhone y en un Android con sus marcadores.
- La operación de la v1.0 no supera el presupuesto mensual definido (sección 12).
- Se cumplen los indicadores de OE-1 a OE-6 o se documenta por qué no.

## 9. Supuestos

- El autor dispone de **6 a 8 horas por semana**.
- Existe un modelo de voz en español, de código abierto y con licencia que permite uso comercial, que corre en el navegador con calidad aceptable. **Se valida en el Sprint 0.**
- Es posible ensamblar el M4B con marcadores dentro del navegador. **Se valida en el Sprint 0.**
- Las APIs de Azure AI Speech y Mercado Pago Suscripciones siguen disponibles en Colombia para la v2.
- El nombre "Podcuaderno" no está registrado como marca por un tercero en la clase relevante. **Se verifica en el sistema de marcas de la SIC antes de comprar el dominio o hacer publicidad.**

## 10. Restricciones

- **Equipo:** una sola persona.
- **Presupuesto de operación v1.0:** máximo 10 USD al mes (propuesto; lo confirma el patrocinador).
- **Tecnología:** Java 25 + Spring Boot 4 en el backend, React + TypeScript en el frontend, PostgreSQL.
- **Legal:** Ley 1581 de 2012 de protección de datos personales. Para la v2, obligaciones tributarias por cobrar suscripciones en Colombia, a validar con un contador.
- **Pagos:** Stripe no está disponible para empresas en Colombia; la v2 usará Mercado Pago.

## 11. Punto de decisión de la v2

Treinta días después del lanzamiento de la v1.0 (alrededor del **18 de mayo de 2027**) se revisan las métricas de OE-5:

- **Si se cumplen:** se planifica la v2 con los datos reales de uso (caracteres promedio por usuario y frecuencia) para definir los planes y sus límites.
- **Si no se cumplen:** se analizan las causas con los usuarios de la beta. Se decide entre mejorar la v1.0, cambiar el enfoque o cerrar el proyecto como pieza de portafolio. Las tres opciones son válidas.

## 12. Presupuesto

| Concepto | v1.0 | v2 |
|---|---|---|
| Voz | 0 USD (en el navegador) | Por caracteres, según el precio oficial de Azure; se define con los planes. |
| Hosting de backend y base de datos, y dominio | ≤ 10 USD al mes (propuesto) | Se recalcula. |
| CI/CD, repositorio | 0 USD (GitHub) | 0 USD |
| Esfuerzo | ~ 100 horas del autor (20 semanas × 5 h efectivas) | Se estima después de la v1.0. |
| Contingencia | 15 % del esfuerzo | — |

## 13. Roles

| Rol | Persona | Responsabilidad |
|---|---|---|
| Patrocinador y Product Owner | Sebastián | Define prioridades, presupuesto y aprueba entregables. |
| Desarrollador full stack | Sebastián | Diseña, programa, prueba, despliega y documenta. |
| Usuarios beta | 20 estudiantes (compañeros y conocidos) | Prueban la aplicación y dan retroalimentación. |
| Revisores externos (opcional) | Mentores o compañeros | Revisan pull requests. |
| Asesor contable (v2) | Por definir | Valida las obligaciones tributarias antes de cobrar. |

## 14. Aprobación

| Nombre | Rol | Fecha | Firma |
|---|---|---|---|
| Sebastián | Patrocinador y Product Owner | | |

## Historial de versiones

| Versión | Fecha | Cambio |
|---|---|---|
| 0.1 | 25 de noviembre de 2026 | Versión inicial: herramienta de línea de comandos para uso técnico. |
| 0.2 | 25 de noviembre de 2026 | Cambio de enfoque: aplicación web para cualquier persona, modelo freemium, stack Java + React. |
| 0.3 | 26 de noviembre de 2026 | Nombre definitivo del proyecto: Podcuaderno (antes AudioGlosario, provisional). |
