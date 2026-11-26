import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from './apiError';
import { requestJson } from './httpClient';
import { getAccessToken, setAccessToken } from './session';

const REFRESH_URL = '/api/v1/auth/refresh';

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}

function unauthorized(): Response {
  return new Response(
    JSON.stringify({
      type: '/errors/unauthenticated',
      title: 'Authentication failed',
      status: 401,
    }),
    { status: 401, headers: { 'Content-Type': 'application/problem+json' } },
  );
}

function tokenBody(accessToken: string) {
  return { accessToken, tokenType: 'Bearer', expiresIn: 900 };
}

function authorizationOf(init: RequestInit | undefined): string | null {
  return new Headers(init?.headers).get('Authorization');
}

/** Routes the refresh endpoint to `refresh` and every other call to `api`. */
function stubBackend(
  api: (init: RequestInit | undefined) => Response,
  refresh: () => Response = () => json(tokenBody('renewed')),
) {
  const fetchMock = vi.fn<typeof fetch>((input, init) =>
    Promise.resolve(String(input) === REFRESH_URL ? refresh() : api(init)),
  );
  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}

function refreshCalls(fetchMock: ReturnType<typeof stubBackend>) {
  return fetchMock.mock.calls.filter(([input]) => String(input) === REFRESH_URL);
}

describe('requestJson with a session', () => {
  beforeEach(() => {
    setAccessToken({ value: 'current', expiresAt: Date.now() + 10 * 60_000 });
  });

  afterEach(() => {
    setAccessToken(null);
    vi.unstubAllGlobals();
    vi.useRealTimers();
  });

  it('sends the bearer token, the JSON content type and same-origin credentials', async () => {
    const fetchMock = stubBackend(() => json({ id: 'n-1' }, 201));

    await requestJson('/notebooks', { method: 'POST', body: JSON.stringify({ title: 'BD' }) });

    const [, init] = fetchMock.mock.calls[0];
    expect(authorizationOf(init)).toBe('Bearer current');
    expect(new Headers(init?.headers).get('Content-Type')).toBe('application/json');
    expect(init?.credentials).toBe('same-origin');
  });

  it('renews the session after a 401 and retries the request once', async () => {
    const fetchMock = stubBackend((init) =>
      authorizationOf(init) === 'Bearer renewed' ? json([{ id: 'n-1' }]) : unauthorized(),
    );

    const body = await requestJson('/notebooks');

    expect(body).toEqual([{ id: 'n-1' }]);
    expect(refreshCalls(fetchMock)).toHaveLength(1);
    expect(fetchMock).toHaveBeenCalledTimes(3);
    expect(getAccessToken()?.value).toBe('renewed');
  });

  it('throws the 401 and ends the session when the renewal fails', async () => {
    const fetchMock = stubBackend(unauthorized, unauthorized);

    const error = await requestJson('/notebooks').catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect((error as ApiError).problem.status).toBe(401);
    expect(getAccessToken()).toBeNull();
    expect(fetchMock).toHaveBeenCalledTimes(2);
  });

  it('does not retry again when the renewed token is also rejected', async () => {
    const fetchMock = stubBackend(unauthorized);

    const error = await requestJson('/notebooks').catch((e: unknown) => e);

    expect((error as ApiError).problem.status).toBe(401);
    expect(refreshCalls(fetchMock)).toHaveLength(1);
    expect(fetchMock).toHaveBeenCalledTimes(3);
  });

  it('shares a single renewal between simultaneous requests', async () => {
    const fetchMock = stubBackend((init) =>
      authorizationOf(init) === 'Bearer renewed' ? json([]) : unauthorized(),
    );

    await Promise.all([requestJson('/notebooks'), requestJson('/notebooks')]);

    expect(refreshCalls(fetchMock)).toHaveLength(1);
  });

  it('renews a token about to expire before sending the request', async () => {
    vi.useFakeTimers({ now: new Date('2026-12-14T10:00:00Z') });
    setAccessToken({ value: 'expiring', expiresAt: Date.now() + 10_000 });
    const fetchMock = stubBackend(() => json([]));

    await requestJson('/notebooks');

    expect(refreshCalls(fetchMock)).toHaveLength(1);
    const [, init] = fetchMock.mock.calls[1];
    expect(authorizationOf(init)).toBe('Bearer renewed');
    expect(getAccessToken()?.expiresAt).toBe(Date.now() + 900_000);
  });

  it('returns undefined for a response without body', async () => {
    stubBackend(() => new Response(null, { status: 202 }));

    await expect(requestJson('/auth/registrations', { method: 'POST' })).resolves.toBeUndefined();
  });

  it('never sends Authorization nor renews when the request is not authenticated', async () => {
    const fetchMock = stubBackend(unauthorized);

    const error = await requestJson(
      '/auth/login',
      { method: 'POST', body: '{}' },
      { authenticated: false },
    ).catch((e: unknown) => e);

    expect((error as ApiError).problem.status).toBe(401);
    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(authorizationOf(fetchMock.mock.calls[0][1])).toBeNull();
  });

  it('keeps an explicit content type', async () => {
    const fetchMock = stubBackend(() => json({}));

    await requestJson('/notebooks', {
      method: 'POST',
      body: 'x',
      headers: { 'Content-Type': 'text/plain' },
    });

    expect(new Headers(fetchMock.mock.calls[0][1]?.headers).get('Content-Type')).toBe('text/plain');
  });
});
