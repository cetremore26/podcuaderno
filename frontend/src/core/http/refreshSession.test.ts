import { afterEach, describe, expect, it, vi } from 'vitest';
import { refreshSession, toAccessToken } from './refreshSession';
import { getAccessToken, setAccessToken } from './session';

function stubRefresh(response: Response) {
  const fetchMock = vi.fn<typeof fetch>().mockResolvedValue(response);
  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}

function tokenResponse(): Response {
  return new Response(JSON.stringify({ accessToken: 'abc', tokenType: 'Bearer', expiresIn: 900 }), {
    status: 200,
    headers: { 'Content-Type': 'application/json' },
  });
}

describe('refreshSession', () => {
  afterEach(() => {
    setAccessToken(null);
    vi.unstubAllGlobals();
    vi.useRealTimers();
  });

  it('turns the token response into an access token with its expiry instant', () => {
    vi.useFakeTimers({ now: 1_000 });

    expect(toAccessToken({ accessToken: 'abc', expiresIn: 900 })).toEqual({
      value: 'abc',
      expiresAt: 901_000,
    });
  });

  it('posts to the refresh endpoint with the cookie and stores the new token', async () => {
    const fetchMock = stubRefresh(tokenResponse());

    const token = await refreshSession();

    expect(token?.value).toBe('abc');
    expect(getAccessToken()).toEqual(token);
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/v1/auth/refresh');
    expect(init?.method).toBe('POST');
    expect(init?.credentials).toBe('same-origin');
    expect(new Headers(init?.headers).has('Authorization')).toBe(false);
  });

  it('ends the session when the refresh cookie is not valid', async () => {
    setAccessToken({ value: 'old', expiresAt: Date.now() + 60_000 });
    stubRefresh(new Response(null, { status: 401 }));

    await expect(refreshSession()).resolves.toBeNull();
    expect(getAccessToken()).toBeNull();
  });

  it('serializes the renewal across tabs with the Web Locks API when available', async () => {
    stubRefresh(tokenResponse());
    const request = vi.fn((_name: string, task: () => Promise<unknown>) => task());
    vi.stubGlobal('navigator', { ...navigator, locks: { request } });

    await refreshSession();

    expect(request).toHaveBeenCalledWith('podcuaderno-session-refresh', expect.any(Function));
  });

  it('starts a new renewal once the previous one has finished', async () => {
    const fetchMock = vi
      .fn<typeof fetch>()
      .mockImplementation(() => Promise.resolve(tokenResponse()));
    vi.stubGlobal('fetch', fetchMock);

    await refreshSession();
    await refreshSession();

    expect(fetchMock).toHaveBeenCalledTimes(2);
  });
});
