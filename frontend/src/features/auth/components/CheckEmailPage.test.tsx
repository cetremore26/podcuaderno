import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '@/test/renderWithProviders';
import { CheckEmailPage } from './CheckEmailPage';

const api = vi.hoisted(() => ({
  register: vi.fn(),
  requestVerificationEmail: vi.fn(),
  confirmEmail: vi.fn(),
}));
vi.mock('../api/authApi', () => api);

describe('CheckEmailPage', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('sends another link to the email just registered', async () => {
    api.requestVerificationEmail.mockResolvedValue(undefined);
    renderWithProviders(<CheckEmailPage />, {
      route: { pathname: '/registro/revisa-tu-correo', state: { email: 'ana@correo.com' } },
    });

    expect(screen.getByText(/Si ana@correo\.com no tenía cuenta/)).toBeInTheDocument();
    expect(screen.queryByLabelText('Correo')).toBeNull();
    await userEvent.click(screen.getByRole('button', { name: 'Enviarme otro enlace' }));

    expect(await screen.findByRole('status')).toHaveTextContent(
      'Listo. Si tu cuenta está pendiente, te llegará un enlace nuevo en unos minutos.',
    );
    expect(api.requestVerificationEmail.mock.calls[0][0]).toBe('ana@correo.com');
    expect(screen.getByRole('link', { name: 'Ir a iniciar sesión' })).toHaveAttribute(
      'href',
      '/iniciar-sesion',
    );
  });

  it('asks for the email when the page is opened directly, and reports failures', async () => {
    api.requestVerificationEmail.mockRejectedValue(new TypeError('Failed to fetch'));
    renderWithProviders(<CheckEmailPage />, { route: '/registro/revisa-tu-correo' });

    expect(screen.getByText(/Si tu correo no tenía cuenta/)).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Enviarme otro enlace' }));
    expect(await screen.findByText('Escribe tu correo.')).toBeInTheDocument();

    await userEvent.type(screen.getByLabelText('Correo'), 'ana@correo.com');
    await userEvent.click(screen.getByRole('button', { name: 'Enviarme otro enlace' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('No hay conexión.');
  });
});
