import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '@/test/renderWithProviders';
import { App } from './App';

describe('App', () => {
  it('renders the home page with the product heading and the entry points', () => {
    renderWithProviders(<App />);

    expect(screen.getByRole('heading', { level: 1, name: 'Podcuaderno' })).toBeInTheDocument();
    expect(
      screen.getByText('Convierte tus apuntes en un audiolibro organizado por temas.'),
    ).toBeInTheDocument();
    const main = screen.getByRole('main');
    expect(main.querySelector('a[href="/registro"]')).toHaveTextContent('Crear cuenta');
    expect(main.querySelector('a[href="/iniciar-sesion"]')).toHaveTextContent('Iniciar sesión');
    expect(document.title).toBe('Podcuaderno');
  });
});
