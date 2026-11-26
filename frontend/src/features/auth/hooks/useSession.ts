import { useSyncExternalStore } from 'react';
import { getAccessToken, subscribeToSession, type AccessToken } from '@/core/http/session';

/** The in-memory session; re-renders when it starts, renews or ends. */
export function useSession(): AccessToken | null {
  return useSyncExternalStore(subscribeToSession, getAccessToken);
}
