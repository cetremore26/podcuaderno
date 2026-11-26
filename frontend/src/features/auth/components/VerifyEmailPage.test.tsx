import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { StrictMode } from 'react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@/core/http/apiError';
import { LocationProbe } from '@/test/LocationProbe';
import { renderWithProviders } from '@/test/renderWithProviders';
import { VerifyEmailPage } from './VerifyEmailPage';

const api = vi.hoisted(() => ({
  register: vi.fn(),
  requestVerificationEmail: vi.fn(),
  confirmEmail: vi.fn(),
}));
vi.mock('../api/authApi', () => api);

function renderVerify(route: string) {
  return renderWithProviders(
    <StrictMode>
      <VerifyEmailPage />
      <LocationProbe />
    </StrictMode>,
    { route },
  );
}

const invalidToken = new ApiError({
  type: '/errors/business-rule',
  title: 'Business rule violated',
  status: 422,
  code: 'invalid-verification-token',
});

describe('VerifyEmailPage', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('activates the account once and removes the token from the address bar', async () => {
    let finish: () => void = () => undefined;
    api.confirmEmail.mockReturnValue(
      new Promise<void>((resolve) => {
        finish = resolve;
      }),
    );
    renderVerify('/verificar-correo#token=abc-123');

    expect(screen.getByRole('status')).toHaveTextContent('Activando tu cuenta…');
    expect(screen.getByTestId('location')).toHaveTextContent(/^\/verificar-correo$/);
    finish();

    expect(await screen.findByRole('heading', { name: 'Tu cuenta está activa' })).toBeVisible();
    expect(screen.getByRole('link', { name: 'Iniciar sesión' })).toHaveAttribute(
      'href',
      '/iniciar-sesion',
    );
    expect(api.confirmEmail).toHaveBeenCalledTimes(1);
    expect(api.confirmEmail.mock.calls[0][0]).toBe('abc-123');
  });

  it('offers a new link when the token is no longer valid', async () => {
    api.confirmEmail.mockRejectedValue(invalidToken);
    api.requestVerificationEmail.mockResolvedValue(undefined);
    renderVerify('/verificar-correo#token=used');

    expect(await screen.findByRole('heading', { name: 'Este enlace ya no sirve' })).toBeVisible();
    await userEvent.type(screen.getByLabelText('Correo'), 'ana@correo.com');
    await userEvent.click(screen.getByRole('button', { name: 'Enviarme otro enlace' }));

    expect(await screen.findByRole('status')).toHaveTextContent(/te llegará un enlace nuevo/);
    expect(api.requestVerificationEmail.mock.calls[0][0]).toBe('ana@correo.com');
  });

  it('does not call the backend when the link has no token', () => {
    renderVerify('/verificar-correo');

    expect(screen.getByRole('heading', { name: 'Este enlace ya no sirve' })).toBeVisible();
    expect(api.confirmEmail).not.toHaveBeenCalled();
  });

  it('lets the user try again after an unexpected failure', async () => {
    api.confirmEmail
      .mockRejectedValueOnce(new TypeError('Failed to fetch'))
      .mockResolvedValueOnce(undefined);
    renderVerify('/verificar-correo#token=abc');

    expect(await screen.findByRole('alert')).toHaveTextContent('No hay conexión.');
    await userEvent.click(screen.getByRole('button', { name: 'Intentar de nuevo' }));

    expect(await screen.findByRole('heading', { name: 'Tu cuenta está activa' })).toBeVisible();
    expect(api.confirmEmail).toHaveBeenCalledTimes(2);
  });
});
