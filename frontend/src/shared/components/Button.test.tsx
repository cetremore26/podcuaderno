import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { Button } from './Button';
import { buttonClassName } from './buttonClassName';

describe('Button', () => {
  it('is a primary non-submitting button by default', () => {
    render(<Button>Guardar</Button>);

    const button = screen.getByRole('button', { name: 'Guardar' });
    expect(button).toHaveAttribute('type', 'button');
    expect(button).toHaveClass('button');
    expect(button).not.toHaveClass('button--secondary');
  });

  it('accepts a submit type, a variant, full width and extra classes', () => {
    render(
      <Button type="submit" variant="danger" block className="extra">
        Eliminar
      </Button>,
    );

    const button = screen.getByRole('button', { name: 'Eliminar' });
    expect(button).toHaveAttribute('type', 'submit');
    expect(button).toHaveClass('button', 'button--danger', 'button--block', 'extra');
  });

  it('builds the same classes for links that look like buttons', () => {
    expect(buttonClassName()).toBe('button');
    expect(buttonClassName('secondary', true)).toBe('button button--secondary button--block');
  });
});
