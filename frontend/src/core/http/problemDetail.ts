/** RFC 9457 problem details, as returned by the backend GlobalExceptionHandler. */
export interface ProblemDetail {
  readonly type: string;
  readonly title: string;
  readonly status: number;
  readonly detail?: string;
  readonly correlationId?: string;
}
