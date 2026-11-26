import { Link, Outlet, useLocation } from 'react-router';
import { useLogout, useSession } from '@/features/auth';
import { Button } from '@/shared/components/Button';

/** Common header, main landmark and footer with the link to the privacy policy. */
export function AppLayout() {
  return (
    <div className="layout">
      <header className="layout__header">
        <nav className="layout__bar" aria-label="Principal">
          <Link to="/" className="layout__brand">
            Podcuaderno
          </Link>
          <SessionAction />
        </nav>
      </header>
      <main className="layout__main">
        <Outlet />
      </main>
      <footer className="layout__footer">
        <Link to="/politica-de-datos">Política de datos</Link>
      </footer>
    </div>
  );
}

function SessionAction() {
  const session = useSession();
  const logout = useLogout();
  const { pathname } = useLocation();

  if (session) {
    return (
      <Button variant="secondary" onClick={() => logout.mutate()} disabled={logout.isPending}>
        Cerrar sesión
      </Button>
    );
  }
  return pathname === '/iniciar-sesion' ? (
    <Link to="/registro">Crear cuenta</Link>
  ) : (
    <Link to="/iniciar-sesion">Iniciar sesión</Link>
  );
}
