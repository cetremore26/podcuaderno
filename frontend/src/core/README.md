# core/

Framework-free logic shared by every feature, testable without a browser.
Nothing here imports React, TanStack Query or React Hook Form, and `core/` depends only on `core/`.

Areas:

- `core/http`: the only code that talks to the network. `requestJson` adds the
  `/api/v1` base path and turns RFC 9457 problem details into `ApiError`.
- `core/audio`: in-browser voice engine and audiobook assembly, with `ports/`,
  `adapters/` (WebAssembly, Web Worker) and `services/` (created with HU-14 and HU-15).
- `core/storage`: IndexedDB behind an interface (created when the first story needs it).

Rules from docs/05 §6.2, enforced by ESLint:

- Rule 1: only `core/http` may call `fetch` or `XMLHttpRequest`.
- Rule 2: no framework imports (`react`, `@tanstack/*`, `react-hook-form`, ...).
- `core/` never imports from `app/`, `features/` or `shared/`.
- Rule 4: voice generation and audio assembly run in a Web Worker.
