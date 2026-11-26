export const DEFAULT_PRIVATE_PATH = '/cuadernos';

/**
 * Where RequireSession sent the user from, so logging in brings them back there.
 * Only same-site paths are accepted, never another origin.
 */
export function redirectTarget(state: unknown): string {
  if (typeof state === 'object' && state !== null && 'from' in state) {
    const { from } = state;
    if (typeof from === 'string' && from.startsWith('/') && !from.startsWith('//')) {
      return from;
    }
  }
  return DEFAULT_PRIVATE_PATH;
}
