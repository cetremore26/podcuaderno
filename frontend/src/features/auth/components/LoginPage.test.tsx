import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Route, Routes } from 'react-router';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@/core/http/apiError';
import { setAccessToken } from '@/core/http/session';
import { renderWithProviders } from '@/test/renderWithProviders';
import { LoginPage } from './LoginPage';

const api = vi.hoisted(() => ({
  register: vi.fn(),
  requestVerificationEmail: vi.fn(),
  confirmEmail: vi.fn(),
  logIn: vi.fn(),
  logOut: vi.fn(),
  restoreSession: vi.fn(),
}));
vi.mock('../api/authApi', () => api);

function renderLogin(route: Parameters<typeof renderWithProviders>[1] = {}) {
  renderWithProviders(
    <Routes>
      <Route path="/iniciar-sesion" element={<LoginPage />} />
      <Route path="/cuadernos" element={<h1>Mis cuadernos</h1>} />
      <Route path="/otra" element={<h1>Otra página</h1>} />
    </Routes>,
    { route: '/iniciar-sesion', ...route },
  );
}

async function submit(email = 'ana@correo.com', password = 'clave-segura') {
  await userEvent.type(screen.getByLabelText('Correo'), email);
  await userEvent.type(screen.getByLabelText('Contraseña'), password);
  await userEvent.click(screen.getByRole('button', { name: 'Entrar' }));
}

function problem(type: string, status: number, extra: object = {}): ApiError {
  return new ApiError({ type, title: 'Problem', status, ...extra });
}

describe('LoginPage', () => {
  afterEach(() => {
    setAccessToken(null);
    vi.resetAllMocks();
  });

  it('logs in with Enter and opens the notebooks', async () => {
    api.logIn.mockResolvedValue(undefined);
    renderLogin();

    await userEvent.type(screen.getByLabelText('Correo'), 'Ana@Correo.com');
    await userEvent.type(screen.getByLabelText('Contraseña'), 'clave-segura{Enter}');

    expect(await screen.findByRole('heading', { name: 'Mis cuadernos' })).toBeInTheDocument();
    expect(api.logIn.mock.calls[0][0]).toEqual({
      email: 'ana@correo.com',
      password: 'clave-segura',
    });
  });

  it('returns to the private page the user tried to open', async () => {
    api.logIn.mockResolvedValue(undefined);
    renderLogin({ route: { pathname: '/iniciar-sesion', state: { from: '/otra' } } });

    await submit();

    expect(await screen.findByRole('heading', { name: 'Otra página' })).toBeInTheDocument();
  });

  it('asks for both fields before calling the backend', async () => {
    renderLogin();

    await userEvent.click(screen.getByRole('button', { name: 'Entrar' }));

    expect(await screen.findByText('Escribe tu correo.')).toBeInTheDocument();
    expect(screen.getByText('Escribe tu contraseña.')).toBeInTheDocument();
    expect(api.logIn).not.toHaveBeenCalled();
  });

  it('shows a generic message for wrong credentials', async () => {
    api.logIn.mockRejectedValue(problem('/errors/unauthenticated', 401));
    renderLogin();

    await submit();

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'El correo o la contraseña no son correctos.',
    );
  });

  it('asks to activate the account and offers a new link', async () => {
    api.logIn.mockRejectedValue(
      problem('/errors/business-rule', 422, { code: 'email-not-verified' }),
    );
    api.requestVerificationEmail.mockResolvedValue(undefined);
    renderLogin();

    await submit('Ana@Correo.com');
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Primero activa tu cuenta con el enlace que te enviamos al correo.',
    );
    await userEvent.click(screen.getByRole('button', { name: 'Enviarme otro enlace' }));

    expect(await screen.findByRole('status')).toHaveTextContent(/te llegará un enlace nuevo/);
    expect(api.requestVerificationEmail.mock.calls[0][0]).toBe('ana@correo.com');
  });

  it('tells how long to wait after too many attempts', async () => {
    api.logIn.mockRejectedValue(problem('/errors/rate-limit', 429, { retryAfterSeconds: 841 }));
    renderLogin();

    await submit();

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Hiciste demasiados intentos. Espera 15 minutos y vuelve a intentarlo.',
    );
  });

  it('explains when there is no connection', async () => {
    api.logIn.mockRejectedValue(new TypeError('Failed to fetch'));
    renderLogin();

    await submit();

    expect(await screen.findByRole('alert')).toHaveTextContent('No hay conexión.');
  });

  it('sends a user who already has a session to the notebooks', () => {
    setAccessToken({ value: 'abc', expiresAt: Date.now() + 60_000 });
    renderLogin();

    expect(screen.getByRole('heading', { name: 'Mis cuadernos' })).toBeInTheDocument();
  });

  it('can show the password', async () => {
    renderLogin();

    await userEvent.click(screen.getByRole('checkbox', { name: 'Mostrar contraseña' }));

    expect(screen.getByLabelText('Contraseña')).toHaveAttribute('type', 'text');
    expect(screen.getByRole('link', { name: 'Crea una' })).toHaveAttribute('href', '/registro');
  });
});
