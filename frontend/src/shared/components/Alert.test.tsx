import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { Alert } from './Alert';

describe('Alert', () => {
  it('announces errors immediately', () => {
    render(<Alert tone="error">No hay conexión.</Alert>);

    const alert = screen.getByRole('alert');
    expect(alert).toHaveTextContent('No hay conexión.');
    expect(alert).toHaveClass('alert', 'alert--error');
  });

  it('announces success and information politely', () => {
    render(
      <>
        <Alert tone="success">Listo.</Alert>
        <Alert>Borrador.</Alert>
      </>,
    );

    const [success, info] = screen.getAllByRole('status');
    expect(success).toHaveClass('alert--success');
    expect(info).toHaveClass('alert');
    expect(info).not.toHaveClass('alert--info');
  });
});
