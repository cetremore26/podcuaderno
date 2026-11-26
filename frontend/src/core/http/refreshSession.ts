import { API_BASE_PATH } from './apiBasePath';
import { setAccessToken, type AccessToken } from './session';

/** Body of a successful login or renewal (the refresh token travels only in the HttpOnly cookie). */
export interface TokenResponse {
  readonly accessToken: string;
  readonly expiresIn: number;
}

const LOCK_NAME = 'podcuaderno-session-refresh';

let inFlight: Promise<AccessToken | null> | null = null;

export function toAccessToken(body: TokenResponse): AccessToken {
  return { value: body.accessToken, expiresAt: Date.now() + body.expiresIn * 1000 };
}

/**
 * Renews the access token with the refresh cookie and stores it, or ends the session when the cookie is not
 * valid. One renewal at a time in this tab, and across tabs when Web Locks exist: the backend rotates the
 * refresh token and treats a reused one as theft (ADR-0009).
 */
export function refreshSession(): Promise<AccessToken | null> {
  inFlight ??= exclusively(requestNewToken).finally(() => {
    inFlight = null;
  });
  return inFlight;
}

function exclusively<T>(task: () => Promise<T>): Promise<T> {
  const locks = globalThis.navigator?.locks;
  return locks ? locks.request(LOCK_NAME, task) : task();
}

async function requestNewToken(): Promise<AccessToken | null> {
  const response = await fetch(`${API_BASE_PATH}/auth/refresh`, {
    method: 'POST',
    credentials: 'same-origin',
    headers: { Accept: 'application/json' },
  });
  const token = response.ok ? toAccessToken((await response.json()) as TokenResponse) : null;
  setAccessToken(token);
  return token;
}
