import { ApiError } from './apiError';
import type { ProblemDetail } from './problemDetail';

export const API_BASE_PATH = '/api/v1';

/**
 * Sends a request to the backend API and parses the JSON body.
 * Non-2xx responses are thrown as {@link ApiError} carrying the problem details.
 */
export async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers);
  headers.set('Accept', 'application/json');

  const response = await fetch(`${API_BASE_PATH}${path}`, { ...init, headers });

  if (!response.ok) {
    throw new ApiError(await toProblemDetail(response));
  }
  return (await response.json()) as T;
}

async function toProblemDetail(response: Response): Promise<ProblemDetail> {
  const contentType = response.headers.get('Content-Type') ?? '';
  if (contentType.includes('problem+json')) {
    return (await response.json()) as ProblemDetail;
  }
  return { type: 'about:blank', title: response.statusText, status: response.status };
}
