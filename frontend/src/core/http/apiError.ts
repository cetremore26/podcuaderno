import type { ProblemDetail } from './problemDetail';

/** Error thrown by the HTTP client when the backend answers with a non-2xx status. */
export class ApiError extends Error {
  readonly problem: ProblemDetail;

  constructor(problem: ProblemDetail) {
    super(problem.title);
    this.name = 'ApiError';
    this.problem = problem;
  }
}
