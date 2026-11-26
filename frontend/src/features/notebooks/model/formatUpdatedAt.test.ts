import { describe, expect, it } from 'vitest';
import { formatUpdatedAt } from './formatUpdatedAt';

const NOW = Date.parse('2026-12-14T12:00:00Z');

function ago(seconds: number): string {
  return new Date(NOW - seconds * 1000).toISOString();
}

describe('formatUpdatedAt', () => {
  it.each([
    [10, 'Modificado ahora'],
    [5 * 60, 'Modificado hace 5 minutos'],
    [3 * 60 * 60, 'Modificado hace 3 horas'],
    [24 * 60 * 60, 'Modificado ayer'],
    [3 * 24 * 60 * 60, 'Modificado hace 3 días'],
    [14 * 24 * 60 * 60, 'Modificado hace 2 semanas'],
    [60 * 24 * 60 * 60, 'Modificado hace 2 meses'],
    [400 * 24 * 60 * 60, 'Modificado el año pasado'],
  ])('describes a change %i seconds ago', (seconds, text) => {
    expect(formatUpdatedAt(ago(seconds), NOW)).toBe(text);
  });

  it('treats a date slightly in the future (clock skew) as now', () => {
    expect(formatUpdatedAt(ago(-30), NOW)).toBe('Modificado ahora');
  });
});
