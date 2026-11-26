# shared/

Reusable UI building blocks with no business knowledge: buttons, form fields,
layout primitives, generic hooks and formatting helpers.

What belongs here:

- Components used by two or more features.
- Hooks and utilities that know nothing about notebooks, users or audio.

What does not belong here:

- Anything tied to a single feature: keep it inside that feature.
- Network access: use a feature `api/` folder backed by `core/http` (docs/05 §6.2, rule 1).

Rules enforced by ESLint:

- `shared/` may import from `core/` but never from `app/` or `features/`.
- Do not climb more than one directory; use the `@/` alias across areas.
- Promote code here only when a second feature needs it.
