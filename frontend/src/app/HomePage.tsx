import { Link } from 'react-router';
import { buttonClassName } from '@/shared/components/buttonClassName';
import { usePageTitle } from '@/shared/hooks/usePageTitle';

export function HomePage() {
  usePageTitle();

  return (
    <section className="stack">
      <h1>Podcuaderno</h1>
      <p>Convierte tus apuntes en un audiolibro organizado por temas.</p>
      <Link to="/registro" className={buttonClassName('primary', true)}>
        Crear cuenta
      </Link>
      <Link to="/iniciar-sesion" className={buttonClassName('secondary', true)}>
        Iniciar sesión
      </Link>
    </section>
  );
}
