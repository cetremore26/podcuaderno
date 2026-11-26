import { afterEach, describe, expect, it, vi } from 'vitest';
import { getAccessToken, setAccessToken, subscribeToSession } from './session';

describe('session', () => {
  afterEach(() => {
    setAccessToken(null);
  });

  it('keeps the token in memory and notifies subscribers until they unsubscribe', () => {
    const listener = vi.fn();
    const unsubscribe = subscribeToSession(listener);

    const token = { value: 'abc', expiresAt: 1 };
    setAccessToken(token);
    expect(getAccessToken()).toBe(token);
    expect(listener).toHaveBeenCalledTimes(1);

    unsubscribe();
    setAccessToken(null);
    expect(getAccessToken()).toBeNull();
    expect(listener).toHaveBeenCalledTimes(1);
  });
});
