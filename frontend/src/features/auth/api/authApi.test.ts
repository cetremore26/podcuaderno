import { afterEach, describe, expect, it, vi } from 'vitest';
import { requestJson } from '@/core/http/httpClient';
import { confirmEmail, register, requestVerificationEmail } from './authApi';

vi.mock('@/core/http/httpClient', () => ({ requestJson: vi.fn() }));

function lastCall() {
  const [path, init, options] = vi.mocked(requestJson).mock.calls.at(-1) ?? [];
  return { path, method: init?.method, body: JSON.parse(String(init?.body)), options };
}

describe('authApi', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('registers without sending the session', async () => {
    await register({
      email: 'ana@correo.com',
      password: 'clave-segura',
      privacyPolicyAccepted: true,
      privacyPolicyVersion: '1.0',
    });

    expect(lastCall()).toEqual({
      path: '/auth/registrations',
      method: 'POST',
      body: {
        email: 'ana@correo.com',
        password: 'clave-segura',
        privacyPolicyAccepted: true,
        privacyPolicyVersion: '1.0',
      },
      options: { authenticated: false },
    });
  });

  it('asks for a new verification link and confirms a token', async () => {
    await requestVerificationEmail('ana@correo.com');
    expect(lastCall()).toMatchObject({
      path: '/auth/email-verifications',
      body: { email: 'ana@correo.com' },
      options: { authenticated: false },
    });

    await confirmEmail('abc');
    expect(lastCall()).toMatchObject({
      path: '/auth/email-verifications/confirmations',
      body: { token: 'abc' },
      options: { authenticated: false },
    });
  });
});
