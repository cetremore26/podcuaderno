import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { TextField } from './TextField';

describe('TextField', () => {
  it('labels the input and keeps it valid without an error', () => {
    render(<TextField label="Correo" type="email" />);

    const input = screen.getByLabelText('Correo');
    expect(input).toHaveAttribute('type', 'email');
    expect(input).not.toHaveAttribute('aria-invalid');
    expect(input).not.toHaveAttribute('aria-describedby');
  });

  it('shows the error, marks the input invalid and describes it with hint and error', () => {
    render(<TextField label="Contraseña" hint="Usa al menos 8 caracteres." error="Te faltan 2." />);

    const input = screen.getByLabelText('Contraseña');
    expect(input).toHaveAttribute('aria-invalid', 'true');
    expect(input).toHaveAccessibleDescription('Usa al menos 8 caracteres. Te faltan 2.');
    expect(screen.getByText('Te faltan 2.')).toBeInTheDocument();
  });
});
