import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from './apiError';
import { requestJson } from './httpClient';

function stubFetch(response: Response) {
  const fetchMock = vi.fn<typeof fetch>().mockResolvedValue(response);
  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}

describe('requestJson', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('returns the JSON body and calls the versioned API with Accept', async () => {
    const fetchMock = stubFetch(
      new Response(JSON.stringify({ id: 'n-1' }), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      }),
    );

    const body = await requestJson<{ id: string }>('/notebooks/n-1');

    expect(body).toEqual({ id: 'n-1' });
    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/v1/notebooks/n-1');
    expect(new Headers(init?.headers).get('Accept')).toBe('application/json');
  });

  it('throws ApiError with the problem details sent by the backend', async () => {
    stubFetch(
      new Response(
        JSON.stringify({
          type: '/errors/not-found',
          title: 'Resource not found',
          status: 404,
          correlationId: 'abc-123',
        }),
        { status: 404, headers: { 'Content-Type': 'application/problem+json' } },
      ),
    );

    const error = await requestJson('/notebooks/missing').catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    const apiError = error as ApiError;
    expect(apiError.problem.type).toBe('/errors/not-found');
    expect(apiError.problem.correlationId).toBe('abc-123');
    expect(apiError.message).toBe('Resource not found');
  });

  it('falls back to about:blank when the error body is not problem details', async () => {
    stubFetch(
      new Response('upstream down', {
        status: 502,
        statusText: 'Bad Gateway',
        headers: { 'Content-Type': 'text/plain' },
      }),
    );

    const error = await requestJson('/notebooks').catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect((error as ApiError).problem).toEqual({
      type: 'about:blank',
      title: 'Bad Gateway',
      status: 502,
    });
  });
});
