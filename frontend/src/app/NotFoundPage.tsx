import { Link } from 'react-router';
import { usePageTitle } from '@/shared/hooks/usePageTitle';

export function NotFoundPage() {
  usePageTitle('Página no encontrada');

  return (
    <section>
      <h1>No encontramos esta página</h1>
      <p>Revisa la dirección o vuelve al inicio.</p>
      <p>
        <Link to="/">Volver al inicio</Link>
      </p>
    </section>
  );
}
