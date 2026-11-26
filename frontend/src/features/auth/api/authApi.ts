import { requestJson } from '@/core/http/httpClient';
import { refreshSession, toAccessToken, type TokenResponse } from '@/core/http/refreshSession';
import { setAccessToken, type AccessToken } from '@/core/http/session';

export interface RegistrationRequest {
  readonly email: string;
  readonly password: string;
  readonly privacyPolicyAccepted: true;
  readonly privacyPolicyVersion: string;
}

const NO_SESSION = { authenticated: false } as const;

function post(path: string, body: unknown): Promise<void> {
  return requestJson<void>(path, { method: 'POST', body: JSON.stringify(body) }, NO_SESSION);
}

/** Creates a pending account. The backend answers 202 whether or not the email already exists. */
export function register(request: RegistrationRequest): Promise<void> {
  return post('/auth/registrations', request);
}

/** Asks for a new verification link; always 202, whether or not the account is pending. */
export function requestVerificationEmail(email: string): Promise<void> {
  return post('/auth/email-verifications', { email });
}

/** Activates the account with the token of the verification link (idempotent once active). */
export function confirmEmail(token: string): Promise<void> {
  return post('/auth/email-verifications/confirmations', { token });
}

export interface Credentials {
  readonly email: string;
  readonly password: string;
}

/** Starts a session: the access token stays in memory and the refresh token in its HttpOnly cookie. */
export async function logIn(credentials: Credentials): Promise<void> {
  const body = await requestJson<TokenResponse>(
    '/auth/login',
    { method: 'POST', body: JSON.stringify(credentials) },
    { authenticated: false },
  );
  setAccessToken(toAccessToken(body));
}

/** Ends the session on the server (revokes the refresh token) and always forgets it in this tab. */
export async function logOut(): Promise<void> {
  try {
    await requestJson<void>('/auth/logout', { method: 'POST' }, { authenticated: false });
  } finally {
    setAccessToken(null);
  }
}

/** Recovers the session after a reload from the refresh cookie; null when there is none. */
export function restoreSession(): Promise<AccessToken | null> {
  return refreshSession();
}
