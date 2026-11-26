import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router';
import { logOut } from '../api/authApi';

/** Ends the session and drops every cached query, so no data of this user stays in memory. */
export function useLogout() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  return useMutation({
    mutationFn: logOut,
    onSettled: () => {
      queryClient.clear();
      void navigate('/iniciar-sesion', { replace: true });
    },
  });
}
