import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, type RenderResult } from '@testing-library/react';
import type { ReactElement } from 'react';
import { MemoryRouter, type InitialEntry } from 'react-router';

interface RenderOptions {
  /** Initial URL of the in-memory router, or a location with state. */
  readonly route?: InitialEntry;
}

export interface RenderWithProvidersResult extends RenderResult {
  readonly queryClient: QueryClient;
}

/** Renders inside a MemoryRouter and a fresh QueryClient without retries, as the app does. */
export function renderWithProviders(
  ui: ReactElement,
  { route = '/' }: RenderOptions = {},
): RenderWithProvidersResult {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });
  const result = render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[route]}>{ui}</MemoryRouter>
    </QueryClientProvider>,
  );
  return { ...result, queryClient };
}
