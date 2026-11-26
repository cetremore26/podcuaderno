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
