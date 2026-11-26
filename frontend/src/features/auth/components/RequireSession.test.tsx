import { screen } from '@testing-library/react';
import { Route, Routes } from 'react-router';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { setAccessToken } from '@/core/http/session';
import { LocationProbe } from '@/test/LocationProbe';
import { renderWithProviders } from '@/test/renderWithProviders';
import { RequireSession } from './RequireSession';

const api = vi.hoisted(() => ({ restoreSession: vi.fn() }));
vi.mock('../api/authApi', () => api);

function LoginProbe() {
  return (
    <>
      <h1>Inicia sesión</h1>
      <LocationProbe />
    </>
  );
}

function renderPrivate() {
  renderWithProviders(
    <Routes>
      <Route element={<RequireSession />}>
        <Route path="/cuadernos" element={<h1>Mis cuadernos</h1>} />
      </Route>
      <Route path="/iniciar-sesion" element={<LoginProbe />} />
    </Routes>,
    { route: '/cuadernos' },
  );
}

describe('RequireSession', () => {
  afterEach(() => {
    setAccessToken(null);
    vi.resetAllMocks();
  });

  it('shows the private page when there is a session', () => {
    setAccessToken({ value: 'abc', expiresAt: Date.now() + 60_000 });
    renderPrivate();

    expect(screen.getByRole('heading', { name: 'Mis cuadernos' })).toBeInTheDocument();
    expect(api.restoreSession).not.toHaveBeenCalled();
  });

  it('recovers the session after a reload before showing the page', async () => {
    api.restoreSession.mockImplementation(
      () =>
        new Promise((resolve) => {
          setTimeout(() => {
            const token = { value: 'renewed', expiresAt: Date.now() + 60_000 };
            setAccessToken(token);
            resolve(token);
          }, 0);
        }),
    );
    renderPrivate();

    expect(screen.getByRole('status')).toHaveTextContent('Cargando…');
    expect(await screen.findByRole('heading', { name: 'Mis cuadernos' })).toBeInTheDocument();
  });

  it('sends the user to log in, remembering the page, when there is no session', async () => {
    api.restoreSession.mockResolvedValue(null);
    renderPrivate();

    expect(await screen.findByRole('heading', { name: 'Inicia sesión' })).toBeInTheDocument();
    expect(screen.getByTestId('location')).toHaveTextContent('/iniciar-sesion');
  });
});
