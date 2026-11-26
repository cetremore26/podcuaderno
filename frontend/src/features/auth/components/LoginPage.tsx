import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, Navigate } from 'react-router';
import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import { CheckboxField } from '@/shared/components/CheckboxField';
import { TextField } from '@/shared/components/TextField';
import { usePageTitle } from '@/shared/hooks/usePageTitle';
import { useLogin } from '../hooks/useLogin';
import { useSession } from '../hooks/useSession';
import { isEmailNotVerified, loginErrorMessage } from '../model/errorMessages';
import { DEFAULT_PRIVATE_PATH } from '../model/redirectTarget';
import { loginSchema, type LoginFormValues } from '../model/schemas';
import { ResendVerificationForm } from './ResendVerificationForm';

/** Login with the generic, unverified-account, rate-limit and offline messages of sketch B4 (HU-02). */
export function LoginPage() {
  usePageTitle('Iniciar sesión');
  const session = useSession();
  const login = useLogin();
  const [showPassword, setShowPassword] = useState(false);
  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '' },
  });

  if (session && !login.isPending && !login.isSuccess) {
    return <Navigate to={DEFAULT_PRIVATE_PATH} replace />;
  }

  const onSubmit = handleSubmit((values) => login.mutate(values));

  return (
    <section className="stack">
      <h1>Inicia sesión</h1>
      {login.isError ? <Alert tone="error">{loginErrorMessage(login.error)}</Alert> : null}
      {isEmailNotVerified(login.error) ? (
        <ResendVerificationForm email={getValues('email').trim().toLowerCase()} />
      ) : null}
      <form className="stack" noValidate onSubmit={onSubmit}>
        <TextField
          label="Correo"
          type="email"
          autoComplete="email"
          error={errors.email?.message}
          {...register('email')}
        />
        <TextField
          label="Contraseña"
          type={showPassword ? 'text' : 'password'}
          autoComplete="current-password"
          error={errors.password?.message}
          {...register('password')}
        />
        <CheckboxField
          label="Mostrar contraseña"
          checked={showPassword}
          onChange={(event) => setShowPassword(event.target.checked)}
        />
        <Button type="submit" block disabled={login.isPending}>
          Entrar
        </Button>
      </form>
      <p>
        ¿No tienes cuenta? <Link to="/registro">Crea una</Link>
      </p>
    </section>
  );
}
