/**
 * Importing: reads TXT, Markdown and DOCX files and detects their structure. Stores nothing: returns a preview.
 * Communicates with other modules only through domain events (ADR-0003, ADR-0008).
 */
@ApplicationModule(displayName = "Importing", allowedDependencies = "shared")
package com.podcuaderno.importing;

import org.springframework.modulith.ApplicationModule;
