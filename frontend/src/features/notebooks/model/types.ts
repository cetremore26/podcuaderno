/** A notebook as listed by GET /api/v1/notebooks (most recently modified first). */
export interface Notebook {
  readonly id: string;
  readonly title: string;
  /** ISO-8601 instant of the last change. */
  readonly updatedAt: string;
}
