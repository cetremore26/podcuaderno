import { useMutation } from '@tanstack/react-query';
import { useLocation, useNavigate } from 'react-router';
import { logIn } from '../api/authApi';
import { redirectTarget } from '../model/redirectTarget';

export function useLogin() {
  const navigate = useNavigate();
  const { state } = useLocation();
  return useMutation({
    mutationFn: logIn,
    onSuccess: () => navigate(redirectTarget(state), { replace: true }),
  });
}
