import { Navigate, Outlet, useLocation } from 'react-router';
import { useSession } from '../hooks/useSession';
import { useSessionBootstrap } from '../hooks/useSessionBootstrap';

/** Guard of the private routes: recovers the session after a reload or sends the user to log in. */
export function RequireSession() {
  const bootstrap = useSessionBootstrap();
  const session = useSession();
  const location = useLocation();

  if (bootstrap.isPending && !session) {
    return <p role="status">Cargando…</p>;
  }
  if (!session) {
    return <Navigate to="/iniciar-sesion" replace state={{ from: location.pathname }} />;
  }
  return <Outlet />;
}
