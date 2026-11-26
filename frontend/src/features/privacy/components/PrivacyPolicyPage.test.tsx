import { screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '@/test/renderWithProviders';
import { PrivacyPolicyPage } from './PrivacyPolicyPage';

describe('PrivacyPolicyPage', () => {
  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it('shows the version, the controller, every section and the draft notice', () => {
    renderWithProviders(<PrivacyPolicyPage />);

    expect(
      screen.getByText('Versión 1.0 · Vigente desde el 14 de diciembre de 2026'),
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Manuel Sebastián Cetre Ramirez es el responsable/),
    ).toBeInTheDocument();
    expect(screen.getByRole('status')).toHaveTextContent(/Borrador/);
    expect(screen.getAllByRole('heading', { level: 2 }).map((h) => h.textContent)).toEqual([
      '¿Quién es el responsable?',
      '¿Qué datos guardamos?',
      '¿Para qué los usamos?',
      'Lo que nunca hacemos',
      '¿Cuánto tiempo los guardamos?',
      'Tus derechos (Ley 1581 de 2012)',
      '¿Cómo ejercerlos?',
      'Cambios a esta política',
    ]);
    expect(screen.getByText(/usamos Resend/)).toHaveTextContent(/transferencia internacional/);
    expect(document.title).toBe('Política de datos · Podcuaderno');
  });

  it('says the contact email will be published when it is not configured', () => {
    vi.stubEnv('VITE_PRIVACY_CONTACT_EMAIL', '');
    renderWithProviders(<PrivacyPolicyPage />);

    expect(
      screen.getByText('Pronto publicaremos el correo para ejercer estos derechos.'),
    ).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: /@/ })).not.toBeInTheDocument();
  });

  it('links to the contact email when it is configured', () => {
    vi.stubEnv('VITE_PRIVACY_CONTACT_EMAIL', ' datos@ejemplo.test ');
    renderWithProviders(<PrivacyPolicyPage />);

    expect(screen.getByRole('link', { name: 'datos@ejemplo.test' })).toHaveAttribute(
      'href',
      'mailto:datos@ejemplo.test',
    );
    expect(screen.queryByText(/Pronto publicaremos/)).not.toBeInTheDocument();
  });
});
