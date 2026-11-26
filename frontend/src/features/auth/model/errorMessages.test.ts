import { describe, expect, it } from 'vitest';
import { ApiError } from '@/core/http/apiError';
import {
  isInvalidVerificationLink,
  loginErrorMessage,
  NETWORK_ERROR_MESSAGE,
  registrationErrorMessage,
  resendErrorMessage,
  verificationErrorMessage,
} from './errorMessages';

function businessRule(code: string): ApiError {
  return new ApiError({ type: '/errors/business-rule', title: 'Rule', status: 422, code });
}

const serverError = new ApiError({ type: '/errors/internal', title: 'Unexpected', status: 500 });
const offline = new TypeError('Failed to fetch');

describe('auth error messages', () => {
  it.each([
    [offline, NETWORK_ERROR_MESSAGE],
    [
      businessRule('outdated-privacy-policy'),
      expect.stringContaining('La política de datos cambió'),
    ],
    [businessRule('invalid-email'), 'Escribe un correo válido, por ejemplo nombre@correo.com.'],
    [businessRule('invalid-password'), 'Usa una contraseña de 8 a 128 caracteres.'],
    [serverError, 'No pudimos crear tu cuenta. Inténtalo de nuevo en unos minutos.'],
  ])('explains a failed registration (%#)', (error, message) => {
    expect(registrationErrorMessage(error)).toEqual(message);
  });

  it('explains a failed resend or verification', () => {
    expect(resendErrorMessage(offline)).toBe(NETWORK_ERROR_MESSAGE);
    expect(resendErrorMessage(serverError)).toMatch(/No pudimos enviar el enlace/);
    expect(verificationErrorMessage(offline)).toBe(NETWORK_ERROR_MESSAGE);
    expect(verificationErrorMessage(serverError)).toMatch(/Algo falló/);
  });

  it('treats an invalid token or a malformed request as an unusable link', () => {
    expect(isInvalidVerificationLink(businessRule('invalid-verification-token'))).toBe(true);
    expect(
      isInvalidVerificationLink(
        new ApiError({ type: '/errors/validation', title: 'Bad', status: 400 }),
      ),
    ).toBe(true);
    expect(isInvalidVerificationLink(serverError)).toBe(false);
  });

  it('explains a failed login, rounding the wait up to whole minutes', () => {
    const rateLimited = (retryAfterSeconds?: number) =>
      new ApiError({
        type: '/errors/rate-limit',
        title: 'Too many',
        status: 429,
        retryAfterSeconds,
      });

    expect(loginErrorMessage(rateLimited(30))).toContain('Espera 1 minuto y');
    expect(loginErrorMessage(rateLimited(61))).toContain('Espera 2 minutos y');
    expect(loginErrorMessage(rateLimited())).toContain('Espera 15 minutos y');
    expect(loginErrorMessage(serverError)).toBe(
      'No pudimos iniciar sesión. Inténtalo de nuevo en unos minutos.',
    );
  });
});
