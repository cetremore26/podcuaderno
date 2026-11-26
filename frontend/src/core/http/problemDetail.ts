/** RFC 9457 problem details, as returned by the backend GlobalExceptionHandler. */
export interface ProblemDetail {
  readonly type: string;
  readonly title: string;
  readonly status: number;
  readonly detail?: string;
  readonly correlationId?: string;
  /** Business rule that failed, for 422 /errors/business-rule (for example email-not-verified). */
  readonly code?: string;
  /** Seconds to wait before trying again, for 429 /errors/rate-limit (also in the Retry-After header). */
  readonly retryAfterSeconds?: number;
}
