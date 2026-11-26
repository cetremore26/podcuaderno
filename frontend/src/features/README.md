# features/

One folder per business feature, mirroring the backend modules and user stories.
Planned features: `auth`, `notebooks`, `editor`, `import`, `generation` and `admin`.
Each feature is created with the story that needs it (HU-01, HU-06, ...), not before.

Layout of a feature:

```text
features/<name>/
├── components/   React components (UI only)
├── hooks/        TanStack Query and form hooks; the only layer that calls api/
├── api/          Calls to the backend through core/http
├── model/        Types and pure functions of the feature
└── index.ts      Public API: the only file other areas may import
```

Rules from docs/05 §6.2, enforced by ESLint:

- Rule 1: components do not call `fetch`; they use hooks, and only hooks call `api/`.
- Rule 3: other features are imported only through their `index.ts` (`@/features/<name>`).
- Features never import from `app/`; use the `@/` alias instead of climbing directories.
