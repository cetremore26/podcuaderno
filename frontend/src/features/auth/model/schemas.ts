import { z } from 'zod';

// Same limits as the backend (plan §1.3): RegistrationRequest and the Email and Password value objects.
export const EMAIL_MAX_LENGTH = 254;
export const PASSWORD_MIN_LENGTH = 8;
export const PASSWORD_MAX_LENGTH = 128;
export const VERIFICATION_TOKEN_MAX_LENGTH = 100;

export const emailSchema = z
  .string()
  .trim()
  .toLowerCase()
  .min(1, 'Escribe tu correo.')
  .max(EMAIL_MAX_LENGTH, 'El correo es demasiado largo.')
  .pipe(z.email('Escribe un correo válido, por ejemplo nombre@correo.com.'));

export const registerSchema = z.object({
  email: emailSchema,
  password: z
    .string()
    .min(PASSWORD_MIN_LENGTH, 'Usa al menos 8 caracteres.')
    .max(PASSWORD_MAX_LENGTH, 'Usa máximo 128 caracteres.'),
  privacyPolicyAccepted: z
    .boolean()
    .refine((accepted) => accepted, 'Acepta la política de datos para continuar.'),
});

export type RegisterFormValues = z.output<typeof registerSchema>;

export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, 'Escribe tu contraseña.'),
});

export type LoginFormValues = z.output<typeof loginSchema>;

export const resendSchema = z.object({ email: emailSchema });

export type ResendFormValues = z.output<typeof resendSchema>;

/** "Te faltan N." while the password is shorter than the minimum, so the rule is clear while typing. */
export function passwordHint(password: string): string {
  const missing = PASSWORD_MIN_LENGTH - password.length;
  const rule = 'Usa al menos 8 caracteres.';
  if (password.length === 0 || missing <= 0) {
    return rule;
  }
  return `${rule} Te faltan ${missing}.`;
}

/** Reads the token from the fragment of the verification link (#token=...), or null when it is unusable. */
export function verificationTokenFrom(hash: string): string | null {
  const fragment = hash.startsWith('#') ? hash.slice(1) : hash;
  const token = new URLSearchParams(fragment).get('token')?.trim();
  return token && token.length <= VERIFICATION_TOKEN_MAX_LENGTH ? token : null;
}
