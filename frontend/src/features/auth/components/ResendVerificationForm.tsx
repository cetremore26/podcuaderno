import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import { TextField } from '@/shared/components/TextField';
import { useResendVerification } from '../hooks/useResendVerification';
import { resendErrorMessage } from '../model/errorMessages';
import { resendSchema, type ResendFormValues } from '../model/schemas';

interface ResendVerificationFormProps {
  /** Known email: only the button is shown. Without it, the form asks for the email. */
  readonly email?: string;
}

/** "Enviarme otro enlace": the answer is the same whether or not the account is pending. */
export function ResendVerificationForm({ email }: ResendVerificationFormProps) {
  const resend = useResendVerification();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResendFormValues>({
    resolver: zodResolver(resendSchema),
    defaultValues: { email: email ?? '' },
  });

  const onSubmit = handleSubmit((values) => resend.mutate(values.email));

  return (
    <form className="stack" noValidate onSubmit={onSubmit}>
      {email ? null : (
        <TextField
          label="Correo"
          type="email"
          autoComplete="email"
          error={errors.email?.message}
          {...register('email')}
        />
      )}
      <Button type="submit" variant="secondary" block disabled={resend.isPending}>
        Enviarme otro enlace
      </Button>
      {resend.isSuccess ? (
        <Alert tone="success">
          Listo. Si tu cuenta está pendiente, te llegará un enlace nuevo en unos minutos.
        </Alert>
      ) : null}
      {resend.isError ? <Alert tone="error">{resendErrorMessage(resend.error)}</Alert> : null}
    </form>
  );
}
