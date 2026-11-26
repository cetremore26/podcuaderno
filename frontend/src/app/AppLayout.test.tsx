import { screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { requestJson } from '@/core/http/httpClient';
import { getAccessToken, setAccessToken } from '@/core/http/session';
import { renderWithProviders } from '@/test/renderWithProviders';
import { AppRoutes } from './AppRoutes';

vi.mock('@/core/http/httpClient', () => ({ requestJson: vi.fn() }));

describe('AppLayout with a session', () => {
  afterEach(() => {
    setAccessToken(null);
    vi.resetAllMocks();
  });

  it('links the home page to the notebooks and logs out from the header', async () => {
    vi.mocked(requestJson).mockResolvedValue(undefined);
    setAccessToken({ value: 'abc', expiresAt: Date.now() + 60_000 });
    renderWithProviders(<AppRoutes />);

    const main = screen.getByRole('main');
    expect(within(main).getByRole('link', { name: 'Mis cuadernos' })).toHaveAttribute(
      'href',
      '/cuadernos',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Cerrar sesión' }));

    expect(await screen.findByRole('heading', { name: 'Inicia sesión' })).toBeInTheDocument();
    expect(vi.mocked(requestJson).mock.calls[0][0]).toBe('/auth/logout');
    expect(getAccessToken()).toBeNull();
    expect(screen.queryByRole('button', { name: 'Cerrar sesión' })).toBeNull();
  });
});
