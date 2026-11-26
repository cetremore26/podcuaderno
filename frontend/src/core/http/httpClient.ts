import { ApiError } from './apiError';
import { API_BASE_PATH } from './apiBasePath';
import type { ProblemDetail } from './problemDetail';
import { refreshSession } from './refreshSession';
import { getAccessToken, type AccessToken } from './session';

export { API_BASE_PATH } from './apiBasePath';

/** A token this close to its expiry is renewed before the request instead of risking a 401. */
const EXPIRY_MARGIN_MS = 30_000;

export interface RequestOptions {
  /**
   * Sends the access token and renews the session once after a 401 (default true).
   * The authentication endpoints use false: they never receive an Authorization header.
   */
  readonly authenticated?: boolean;
}

/**
 * Sends a request to the backend API and parses the JSON body (undefined when there is none, as in 202 or 204).
 * Non-2xx responses are thrown as {@link ApiError} carrying the problem details.
 */
export async function requestJson<T>(
  path: string,
  init?: RequestInit,
  options: RequestOptions = {},
): Promise<T> {
  const authenticated = options.authenticated ?? true;
  let response = await send(path, init, authenticated);
  if (response.status === 401 && authenticated && (await refreshSession())) {
    response = await send(path, init, authenticated);
  }

  if (!response.ok) {
    throw new ApiError(await toProblemDetail(response));
  }
  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

async function send(
  path: string,
  init: RequestInit | undefined,
  authenticated: boolean,
): Promise<Response> {
  const headers = new Headers(init?.headers);
  headers.set('Accept', 'application/json');
  if (init?.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }
  const token = authenticated ? await currentToken() : null;
  if (token) {
    headers.set('Authorization', `Bearer ${token.value}`);
  }
  return fetch(`${API_BASE_PATH}${path}`, { ...init, headers, credentials: 'same-origin' });
}

async function currentToken(): Promise<AccessToken | null> {
  const token = getAccessToken();
  return token && token.expiresAt - Date.now() < EXPIRY_MARGIN_MS ? refreshSession() : token;
}

async function toProblemDetail(response: Response): Promise<ProblemDetail> {
  const contentType = response.headers.get('Content-Type') ?? '';
  if (contentType.includes('problem+json')) {
    return (await response.json()) as ProblemDetail;
  }
  return { type: 'about:blank', title: response.statusText, status: response.status };
}
