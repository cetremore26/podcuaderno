import { describe, expect, it } from 'vitest';
import { emailSchema, passwordHint, registerSchema, verificationTokenFrom } from './schemas';

describe('auth schemas', () => {
  it('trims and lowercases the email', () => {
    expect(emailSchema.parse('  Ana@Correo.COM ')).toBe('ana@correo.com');
  });

  it.each([
    ['', 'Escribe tu correo.'],
    ['ana', 'Escribe un correo válido, por ejemplo nombre@correo.com.'],
    [`${'a'.repeat(250)}@correo.com`, 'El correo es demasiado largo.'],
  ])('rejects the email %j with a plain message', (email, message) => {
    expect(emailSchema.safeParse(email).error?.issues[0].message).toBe(message);
  });

  it('requires the consent and a password of 8 to 128 characters', () => {
    const result = registerSchema.safeParse({
      email: 'ana@correo.com',
      password: 'x'.repeat(129),
      privacyPolicyAccepted: false,
    });

    expect(result.error?.issues.map((issue) => issue.message)).toEqual([
      'Usa máximo 128 caracteres.',
      'Acepta la política de datos para continuar.',
    ]);
  });

  it('tells how many characters are missing while the password is short', () => {
    expect(passwordHint('')).toBe('Usa al menos 8 caracteres.');
    expect(passwordHint('123456')).toBe('Usa al menos 8 caracteres. Te faltan 2.');
    expect(passwordHint('12345678')).toBe('Usa al menos 8 caracteres.');
  });

  it('reads the verification token from the link fragment', () => {
    expect(verificationTokenFrom('#token=abc-123')).toBe('abc-123');
    expect(verificationTokenFrom('token=abc')).toBe('abc');
    expect(verificationTokenFrom('')).toBeNull();
    expect(verificationTokenFrom('#token=')).toBeNull();
    expect(verificationTokenFrom(`#token=${'a'.repeat(101)}`)).toBeNull();
  });
});
