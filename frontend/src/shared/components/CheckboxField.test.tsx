import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { CheckboxField } from './CheckboxField';

describe('CheckboxField', () => {
  it('toggles with its label', async () => {
    render(<CheckboxField label="Mostrar contraseña" />);

    const checkbox = screen.getByRole('checkbox', { name: 'Mostrar contraseña' });
    expect(checkbox).not.toBeChecked();
    expect(checkbox).not.toHaveAttribute('aria-describedby');
    await userEvent.click(screen.getByText('Mostrar contraseña'));
    expect(checkbox).toBeChecked();
  });

  it('describes the checkbox with its description and error', () => {
    render(
      <CheckboxField
        label="Acepto la política"
        description="Se abre en otra pestaña"
        error="Acepta la política de datos para continuar."
      />,
    );

    const checkbox = screen.getByRole('checkbox', { name: 'Acepto la política' });
    expect(checkbox).toHaveAttribute('aria-invalid', 'true');
    expect(checkbox).toHaveAccessibleDescription(
      'Se abre en otra pestaña Acepta la política de datos para continuar.',
    );
  });
});
