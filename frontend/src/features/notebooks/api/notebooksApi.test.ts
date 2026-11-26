import { afterEach, describe, expect, it, vi } from 'vitest';
import { requestJson } from '@/core/http/httpClient';
import { createNotebook, deleteNotebook, listNotebooks, renameNotebook } from './notebooksApi';

vi.mock('@/core/http/httpClient', () => ({ requestJson: vi.fn() }));

describe('notebooksApi', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('lists, creates, renames and deletes with the session', async () => {
    await listNotebooks();
    await createNotebook('Bases de datos');
    await renameNotebook({ id: 'n/1', title: 'BD' });
    await deleteNotebook('n-1');

    const calls = vi
      .mocked(requestJson)
      .mock.calls.map(([path, init, options]) => [path, init?.method, init?.body, options]);
    expect(calls).toEqual([
      ['/notebooks', undefined, undefined, undefined],
      ['/notebooks', 'POST', '{"title":"Bases de datos"}', undefined],
      ['/notebooks/n%2F1', 'PATCH', '{"title":"BD"}', undefined],
      ['/notebooks/n-1', 'DELETE', undefined, undefined],
    ]);
  });
});
