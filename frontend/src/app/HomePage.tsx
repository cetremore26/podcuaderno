import { Link } from 'react-router';
import { useSession } from '@/features/auth';
import { buttonClassName } from '@/shared/components/buttonClassName';
import { usePageTitle } from '@/shared/hooks/usePageTitle';

export function HomePage() {
  usePageTitle();
  const session = useSession();

  return (
    <section className="stack">
      <h1>Podcuaderno</h1>
      <p>Convierte tus apuntes en un audiolibro organizado por temas.</p>
      {session ? (
        <Link to="/cuadernos" className={buttonClassName('primary', true)}>
          Mis cuadernos
        </Link>
      ) : (
        <>
          <Link to="/registro" className={buttonClassName('primary', true)}>
            Crear cuenta
          </Link>
          <Link to="/iniciar-sesion" className={buttonClassName('secondary', true)}>
            Iniciar sesión
          </Link>
        </>
      )}
    </section>
  );
}
