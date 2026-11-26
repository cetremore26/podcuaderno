import { ApiError } from './apiError';

/** Problem types sent by the backend (backend ProblemTypes, docs/05 §5.6). */
export const ProblemType = {
  validation: '/errors/validation',
  unauthenticated: '/errors/unauthenticated',
  notFound: '/errors/not-found',
  businessRule: '/errors/business-rule',
  rateLimit: '/errors/rate-limit',
  internal: '/errors/internal',
} as const;

/** True when the error is an {@link ApiError} of that problem type and, if given, that business rule code. */
export function isProblem(error: unknown, type: string, code?: string): error is ApiError {
  return (
    error instanceof ApiError &&
    error.problem.type === type &&
    (code === undefined || error.problem.code === code)
  );
}
