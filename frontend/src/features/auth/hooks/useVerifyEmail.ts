import { useMutation } from '@tanstack/react-query';
import { useEffect, useRef } from 'react';
import { confirmEmail } from '../api/authApi';

/**
 * Confirms the email once per page view. The ref keeps StrictMode's second effect run from sending the token
 * twice (the backend is idempotent anyway).
 */
export function useVerifyEmail(token: string | null) {
  const mutation = useMutation({ mutationFn: confirmEmail });
  const { mutate } = mutation;
  const sent = useRef(false);

  useEffect(() => {
    if (token && !sent.current) {
      sent.current = true;
      mutate(token);
    }
  }, [token, mutate]);

  return mutation;
}
