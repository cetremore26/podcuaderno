import { zodResolver } from '@hookform/resolvers/zod';
import { useId, useState } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { Link, useNavigate } from 'react-router';
import { PRIVACY_POLICY_VERSION } from '@/features/privacy';
import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import { CheckboxField } from '@/shared/components/CheckboxField';
import { TextField } from '@/shared/components/TextField';
import { usePageTitle } from '@/shared/hooks/usePageTitle';
import { useRegister } from '../hooks/useRegister';
import { registrationErrorMessage } from '../model/errorMessages';
import { passwordHint, registerSchema, type RegisterFormValues } from '../model/schemas';

/** Account creation with explicit consent to the privacy policy (HU-01, HU-05). */
export function RegisterPage() {
  usePageTitle('Crear cuenta');
  const navigate = useNavigate();
  const registration = useRegister();
  const [showPassword, setShowPassword] = useState(false);
  const consentHelpId = useId();
  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { email: '', password: '', privacyPolicyAccepted: false },
  });
  const password = useWatch({ control, name: 'password' }) ?? '';
  const accepted = useWatch({ control, name: 'privacyPolicyAccepted' }) === true;
  const hint = passwordHint(password);
  const passwordError = errors.password?.type === 'too_small' ? hint : errors.password?.message;

  const onSubmit = handleSubmit((values) =>
    registration.mutate(
      {
        email: values.email,
        password: values.password,
        privacyPolicyAccepted: true,
        privacyPolicyVersion: PRIVACY_POLICY_VERSION,
      },
      {
        onSuccess: () => navigate('/registro/revisa-tu-correo', { state: { email: values.email } }),
      },
    ),
  );

  return (
    <section className="stack">
      <h1>Crea tu cuenta</h1>
      <p>Guarda tus cuadernos y escúchalos desde cualquier dispositivo.</p>
      {registration.isError ? (
        <Alert tone="error">{registrationErrorMessage(registration.error)}</Alert>
      ) : null}
      <form className="stack" noValidate onSubmit={onSubmit}>
        <TextField
          label="Correo"
          type="email"
          autoComplete="email"
          placeholder="nombre@correo.com"
          error={errors.email?.message}
          {...register('email')}
        />
        <TextField
          label="Contraseña"
          type={showPassword ? 'text' : 'password'}
          autoComplete="new-password"
          hint={passwordError ? undefined : hint}
          error={passwordError}
          {...register('password')}
        />
        <CheckboxField
          label="Mostrar contraseña"
          checked={showPassword}
          onChange={(event) => setShowPassword(event.target.checked)}
        />
        <CheckboxField
          label={`Leí y acepto la política de datos (versión ${PRIVACY_POLICY_VERSION}).`}
          description={
            <Link to="/politica-de-datos" target="_blank" rel="noopener noreferrer">
              Leer la política (se abre en otra pestaña)
            </Link>
          }
          error={errors.privacyPolicyAccepted?.message}
          {...register('privacyPolicyAccepted')}
        />
        <Button
          type="submit"
          block
          disabled={!accepted || registration.isPending}
          aria-describedby={accepted ? undefined : consentHelpId}
        >
          Crear cuenta
        </Button>
        {accepted ? null : (
          <p id={consentHelpId} className="field__hint">
            Para crear tu cuenta, acepta la política de datos.
          </p>
        )}
      </form>
      <p>
        ¿Ya tienes cuenta? <Link to="/iniciar-sesion">Inicia sesión</Link>
      </p>
    </section>
  );
}
