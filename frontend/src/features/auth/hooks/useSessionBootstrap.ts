import { useQuery } from '@tanstack/react-query';
import { restoreSession } from '../api/authApi';
import { useSession } from './useSession';

/** After a reload the token is gone from memory: try once to recover it with the refresh cookie. */
export function useSessionBootstrap() {
  const session = useSession();
  return useQuery({
    queryKey: ['session', 'bootstrap'],
    queryFn: restoreSession,
    enabled: session === null,
    staleTime: Infinity,
    retry: false,
    refetchOnWindowFocus: false,
  });
}
