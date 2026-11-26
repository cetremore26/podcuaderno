/** Access token kept only in memory (ADR-0011): never in localStorage or sessionStorage. */
export interface AccessToken {
  readonly value: string;
  /** Epoch milliseconds after which the backend rejects the token. */
  readonly expiresAt: number;
}

type Listener = () => void;

let current: AccessToken | null = null;
const listeners = new Set<Listener>();

export function getAccessToken(): AccessToken | null {
  return current;
}

/** Replaces the token (null ends the session) and notifies every subscriber. */
export function setAccessToken(token: AccessToken | null): void {
  current = token;
  listeners.forEach((listener) => listener());
}

/** Subscribes to session changes; returns the function that unsubscribes. */
export function subscribeToSession(listener: Listener): () => void {
  listeners.add(listener);
  return () => {
    listeners.delete(listener);
  };
}
