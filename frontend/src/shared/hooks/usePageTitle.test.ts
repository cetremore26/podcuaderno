import { renderHook } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { usePageTitle } from './usePageTitle';

describe('usePageTitle', () => {
  it('uses the product name alone without a page title', () => {
    renderHook(() => usePageTitle());

    expect(document.title).toBe('Podcuaderno');
  });

  it('prefixes the page title and follows changes', () => {
    const { rerender } = renderHook(({ title }) => usePageTitle(title), {
      initialProps: { title: 'Registro' },
    });
    expect(document.title).toBe('Registro · Podcuaderno');

    rerender({ title: 'Mis cuadernos' });
    expect(document.title).toBe('Mis cuadernos · Podcuaderno');
  });
});
