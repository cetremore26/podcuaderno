import { requestJson } from '@/core/http/httpClient';

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
