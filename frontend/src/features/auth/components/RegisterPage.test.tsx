import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Route, Routes } from 'react-router';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@/core/http/apiError';
import { renderWithProviders } from '@/test/renderWithProviders';
import { CheckEmailPage } from './CheckEmailPage';
import { RegisterPage } from './RegisterPage';

const api = vi.hoisted(() => ({
  register: vi.fn(),
  requestVerificationEmail: vi.fn(),
  confirmEmail: vi.fn(),
}));
vi.mock('../api/authApi', () => api);

function renderRegister() {
  renderWithProviders(
    <Routes>
      <Route path="/registro" element={<RegisterPage />} />
      <Route path="/registro/revisa-tu-correo" element={<CheckEmailPage />} />
    </Routes>,
    { route: '/registro' },
  );
  return {
    email: screen.getByLabelText('Correo'),
    password: screen.getByLabelText('Contraseña'),
    consent: screen.getByRole('checkbox', { name: /Leí y acepto la política de datos/ }),
    submit: screen.getByRole('button', { name: 'Crear cuenta' }),
  };
}

describe('RegisterPage', () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it('keeps the button disabled, with a visible reason, until the policy is accepted', async () => {
    const { consent, submit } = renderRegister();

    expect(submit).toBeDisabled();
    expect(submit).toHaveAccessibleDescription(
      'Para crear tu cuenta, acepta la política de datos.',
    );
    expect(screen.getByRole('link', { name: /Leer la política/ })).toHaveAttribute(
      'target',
      '_blank',
    );

    await userEvent.click(consent);

    expect(submit).toBeEnabled();
    expect(screen.queryByText('Para crear tu cuenta, acepta la política de datos.')).toBeNull();
  });

  it('tells how many characters the password is missing and can show it', async () => {
    const { password } = renderRegister();

    await userEvent.type(password, '123456');

    expect(password).toHaveAccessibleDescription('Usa al menos 8 caracteres. Te faltan 2.');
    expect(password).toHaveAttribute('type', 'password');
    await userEvent.click(screen.getByRole('checkbox', { name: 'Mostrar contraseña' }));
    expect(password).toHaveAttribute('type', 'text');
  });

  it('marks a short password as invalid on submit without calling the backend', async () => {
    const { email, password, consent, submit } = renderRegister();

    await userEvent.type(email, 'ana@correo.com');
    await userEvent.type(password, '1234567');
    await userEvent.click(consent);
    await userEvent.click(submit);

    expect(password).toHaveAttribute('aria-invalid', 'true');
    expect(password).toHaveAccessibleDescription('Usa al menos 8 caracteres. Te faltan 1.');
    expect(api.register).not.toHaveBeenCalled();
  });

  it('rejects an invalid email', async () => {
    const { email, password, consent, submit } = renderRegister();

    await userEvent.type(email, 'ana');
    await userEvent.type(password, 'clave-segura');
    await userEvent.click(consent);
    await userEvent.click(submit);

    expect(
      await screen.findByText('Escribe un correo válido, por ejemplo nombre@correo.com.'),
    ).toBeInTheDocument();
    expect(api.register).not.toHaveBeenCalled();
  });

  it('sends the accepted policy version and shows "Revisa tu correo" with the email', async () => {
    api.register.mockResolvedValue(undefined);
    const { email, password, consent, submit } = renderRegister();

    await userEvent.type(email, ' Ana@Correo.com');
    await userEvent.type(password, 'clave-segura');
    await userEvent.click(consent);
    await userEvent.click(submit);

    expect(await screen.findByRole('heading', { name: 'Revisa tu correo' })).toBeInTheDocument();
    expect(screen.getByText(/Si ana@correo\.com no tenía cuenta/)).toBeInTheDocument();
    expect(api.register).toHaveBeenCalledWith(
      {
        email: 'ana@correo.com',
        password: 'clave-segura',
        privacyPolicyAccepted: true,
        privacyPolicyVersion: '1.0',
      },
      expect.anything(),
    );
  });

  it('warns when the accepted policy is outdated', async () => {
    api.register.mockRejectedValue(
      new ApiError({
        type: '/errors/business-rule',
        title: 'Business rule violated',
        status: 422,
        code: 'outdated-privacy-policy',
      }),
    );
    const { email, password, consent, submit } = renderRegister();

    await userEvent.type(email, 'ana@correo.com');
    await userEvent.type(password, 'clave-segura');
    await userEvent.click(consent);
    await userEvent.click(submit);

    expect(await screen.findByRole('alert')).toHaveTextContent('La política de datos cambió');
  });
});
