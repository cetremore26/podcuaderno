import { isProblem, ProblemType } from '@/core/http/problems';

export const NETWORK_ERROR_MESSAGE = 'No hay conexión. Revisa tu internet e inténtalo de nuevo.';

/** fetch rejects with a TypeError when there is no connection or the server cannot be reached. */
export function isNetworkError(error: unknown): boolean {
  return error instanceof TypeError;
}

export function registrationErrorMessage(error: unknown): string {
  if (isNetworkError(error)) {
    return NETWORK_ERROR_MESSAGE;
  }
  if (isProblem(error, ProblemType.businessRule, 'outdated-privacy-policy')) {
    return 'La política de datos cambió. Recarga la página, léela de nuevo y acéptala para continuar.';
  }
  if (isProblem(error, ProblemType.businessRule, 'invalid-email')) {
    return 'Escribe un correo válido, por ejemplo nombre@correo.com.';
  }
  if (isProblem(error, ProblemType.businessRule, 'invalid-password')) {
    return 'Usa una contraseña de 8 a 128 caracteres.';
  }
  return 'No pudimos crear tu cuenta. Inténtalo de nuevo en unos minutos.';
}

export function resendErrorMessage(error: unknown): string {
  return isNetworkError(error)
    ? NETWORK_ERROR_MESSAGE
    : 'No pudimos enviar el enlace. Inténtalo de nuevo en unos minutos.';
}

export function verificationErrorMessage(error: unknown): string {
  return isNetworkError(error)
    ? NETWORK_ERROR_MESSAGE
    : 'Algo falló de nuestro lado. Inténtalo de nuevo en unos minutos.';
}

/** True when the link expired, was already replaced or never existed: the user needs a new one. */
export function isInvalidVerificationLink(error: unknown): boolean {
  return (
    isProblem(error, ProblemType.businessRule, 'invalid-verification-token') ||
    isProblem(error, ProblemType.validation)
  );
}

export function isEmailNotVerified(error: unknown): boolean {
  return isProblem(error, ProblemType.businessRule, 'email-not-verified');
}

/** Message of sketch B4: generic on wrong credentials, so it never reveals whether the email exists. */
export function loginErrorMessage(error: unknown): string {
  if (isNetworkError(error)) {
    return NETWORK_ERROR_MESSAGE;
  }
  if (isProblem(error, ProblemType.unauthenticated)) {
    return 'El correo o la contraseña no son correctos.';
  }
  if (isEmailNotVerified(error)) {
    return 'Primero activa tu cuenta con el enlace que te enviamos al correo.';
  }
  if (isProblem(error, ProblemType.rateLimit)) {
    return `Hiciste demasiados intentos. Espera ${waitTime(error.problem.retryAfterSeconds)} y vuelve a intentarlo.`;
  }
  return 'No pudimos iniciar sesión. Inténtalo de nuevo en unos minutos.';
}

const DEFAULT_LOCKOUT_SECONDS = 15 * 60;

function waitTime(retryAfterSeconds: number | undefined): string {
  const minutes = Math.max(1, Math.ceil((retryAfterSeconds ?? DEFAULT_LOCKOUT_SECONDS) / 60));
  return minutes === 1 ? '1 minuto' : `${minutes} minutos`;
}
