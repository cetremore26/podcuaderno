import { Link, useLocation } from 'react-router';
import { usePageTitle } from '@/shared/hooks/usePageTitle';
import { ResendVerificationForm } from './ResendVerificationForm';

function emailFromState(state: unknown): string | undefined {
  if (typeof state === 'object' && state !== null && 'email' in state) {
    const { email } = state;
    return typeof email === 'string' && email ? email : undefined;
  }
  return undefined;
}

/** Same text whether the email was new or already registered, so nobody can tell accounts apart (HU-01). */
export function CheckEmailPage() {
  usePageTitle('Revisa tu correo');
  const email = emailFromState(useLocation().state);

  return (
    <section className="stack">
      <h1>Revisa tu correo</h1>
      <p>
        Si {email ?? 'tu correo'} no tenía cuenta, te enviamos un enlace para activarla. Vence en 24
        horas.
      </p>
      <p>Si ya tenías una cuenta con ese correo, inicia sesión.</p>
      <p>¿No lo ves? Busca en &quot;Spam&quot; o &quot;Promociones&quot;.</p>
      <ResendVerificationForm email={email} />
      <p>
        <Link to="/iniciar-sesion">Ir a iniciar sesión</Link>
      </p>
    </section>
  );
}
