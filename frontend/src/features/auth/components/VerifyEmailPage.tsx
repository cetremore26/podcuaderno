import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router';
import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import { buttonClassName } from '@/shared/components/buttonClassName';
import { usePageTitle } from '@/shared/hooks/usePageTitle';
import { useVerifyEmail } from '../hooks/useVerifyEmail';
import { isInvalidVerificationLink, verificationErrorMessage } from '../model/errorMessages';
import { verificationTokenFrom } from '../model/schemas';
import { ResendVerificationForm } from './ResendVerificationForm';

/**
 * Opened from the email link (/verificar-correo#token=...). The token travels in the fragment so it never
 * reaches server logs, and it is removed from the address bar as soon as it is read.
 */
export function VerifyEmailPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const [token] = useState(() => verificationTokenFrom(location.hash));
  const verification = useVerifyEmail(token);

  useEffect(() => {
    if (location.hash) {
      navigate(location.pathname, { replace: true });
    }
  }, [location.hash, location.pathname, navigate]);

  if (verification.isSuccess) {
    return <AccountActive />;
  }
  if (!token || (verification.isError && isInvalidVerificationLink(verification.error))) {
    return <LinkExpired />;
  }
  if (verification.isError) {
    return (
      <section className="stack">
        <VerifyTitle title="No pudimos activar tu cuenta" />
        <Alert tone="error">{verificationErrorMessage(verification.error)}</Alert>
        <Button block onClick={() => verification.mutate(token)}>
          Intentar de nuevo
        </Button>
      </section>
    );
  }
  return (
    <section role="status">
      <VerifyTitle title="Activando tu cuenta…" />
    </section>
  );
}

function VerifyTitle({ title }: { readonly title: string }) {
  usePageTitle(title);
  return <h1>{title}</h1>;
}

function AccountActive() {
  return (
    <section className="stack">
      <VerifyTitle title="Tu cuenta está activa" />
      <p>Ya puedes guardar tus cuadernos.</p>
      <Link to="/iniciar-sesion" className={buttonClassName('primary', true)}>
        Iniciar sesión
      </Link>
    </section>
  );
}

function LinkExpired() {
  return (
    <section className="stack">
      <VerifyTitle title="Este enlace ya no sirve" />
      <p>Venció o no es válido. Escribe tu correo y te enviamos uno nuevo.</p>
      <ResendVerificationForm />
    </section>
  );
}
