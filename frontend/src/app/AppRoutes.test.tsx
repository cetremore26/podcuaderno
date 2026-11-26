import { screen, within } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '@/test/renderWithProviders';
import { AppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  it('shows the not found page for an unknown route', () => {
    renderWithProviders(<AppRoutes />, { route: '/no-existe' });

    expect(
      screen.getByRole('heading', { level: 1, name: 'No encontramos esta página' }),
    ).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Volver al inicio' })).toHaveAttribute('href', '/');
    expect(document.title).toBe('Página no encontrada · Podcuaderno');
  });

  it('links to the privacy policy from the footer of every page', () => {
    renderWithProviders(<AppRoutes />);

    const footer = screen.getByRole('contentinfo');
    expect(within(footer).getByRole('link', { name: 'Política de datos' })).toHaveAttribute(
      'href',
      '/politica-de-datos',
    );
  });

  it('offers to log in from the header, or to register when already on the login page', () => {
    const { unmount } = renderWithProviders(<AppRoutes />);
    const nav = screen.getByRole('navigation', { name: 'Principal' });
    expect(within(nav).getByRole('link', { name: 'Iniciar sesión' })).toHaveAttribute(
      'href',
      '/iniciar-sesion',
    );
    unmount();

    renderWithProviders(<AppRoutes />, { route: '/iniciar-sesion' });
    const loginNav = screen.getByRole('navigation', { name: 'Principal' });
    expect(within(loginNav).getByRole('link', { name: 'Crear cuenta' })).toHaveAttribute(
      'href',
      '/registro',
    );
  });
});
