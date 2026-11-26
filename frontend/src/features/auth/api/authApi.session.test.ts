import { afterEach, describe, expect, it, vi } from 'vitest';
import { requestJson } from '@/core/http/httpClient';
import { refreshSession } from '@/core/http/refreshSession';
import { getAccessToken, setAccessToken } from '@/core/http/session';
import { logIn, logOut, restoreSession } from './authApi';

vi.mock('@/core/http/httpClient', () => ({ requestJson: vi.fn() }));
vi.mock('@/core/http/refreshSession', async (importOriginal) => ({
  ...(await importOriginal<typeof import('@/core/http/refreshSession')>()),
  refreshSession: vi.fn(),
}));

describe('authApi session', () => {
  afterEach(() => {
    setAccessToken(null);
    vi.resetAllMocks();
  });

  it('logs in without a previous session and keeps the access token in memory', async () => {
    vi.mocked(requestJson).mockResolvedValue({
      accessToken: 'abc',
      tokenType: 'Bearer',
      expiresIn: 900,
    });

    await logIn({ email: 'ana@correo.com', password: 'clave-segura' });

    const [path, init, options] = vi.mocked(requestJson).mock.calls[0];
    expect(path).toBe('/auth/login');
    expect(init?.method).toBe('POST');
    expect(JSON.parse(String(init?.body))).toEqual({
      email: 'ana@correo.com',
      password: 'clave-segura',
    });
    expect(options).toEqual({ authenticated: false });
    expect(getAccessToken()?.value).toBe('abc');
  });

  it('forgets the session even when the logout request fails', async () => {
    setAccessToken({ value: 'abc', expiresAt: Date.now() + 60_000 });
    vi.mocked(requestJson).mockRejectedValue(new TypeError('Failed to fetch'));

    await expect(logOut()).rejects.toThrow(TypeError);

    expect(vi.mocked(requestJson).mock.calls[0][0]).toBe('/auth/logout');
    expect(getAccessToken()).toBeNull();
  });

  it('restores the session with the refresh cookie', async () => {
    vi.mocked(refreshSession).mockResolvedValue(null);

    await expect(restoreSession()).resolves.toBeNull();
    expect(refreshSession).toHaveBeenCalledTimes(1);
  });
});
