import { Link, Outlet, useLocation } from 'react-router';

/** Common header, main landmark and footer with the link to the privacy policy. */
export function AppLayout() {
  const { pathname } = useLocation();
  const onLoginPage = pathname === '/iniciar-sesion';

  return (
    <div className="layout">
      <header className="layout__header">
        <nav className="layout__bar" aria-label="Principal">
          <Link to="/" className="layout__brand">
            Podcuaderno
          </Link>
          {onLoginPage ? (
            <Link to="/registro">Crear cuenta</Link>
          ) : (
            <Link to="/iniciar-sesion">Iniciar sesión</Link>
          )}
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
