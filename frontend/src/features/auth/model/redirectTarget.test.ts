import { describe, expect, it } from 'vitest';
import { redirectTarget } from './redirectTarget';

describe('redirectTarget', () => {
  it('returns to the private page the user tried to open', () => {
    expect(redirectTarget({ from: '/cuadernos' })).toBe('/cuadernos');
  });

  it.each([
    [null],
    [undefined],
    ['x'],
    [{}],
    [{ from: 3 }],
    [{ from: 'https://otro.test' }],
    [{ from: '//otro.test' }],
  ])('falls back to the notebooks for %j', (state) => {
    expect(redirectTarget(state)).toBe('/cuadernos');
  });
});
