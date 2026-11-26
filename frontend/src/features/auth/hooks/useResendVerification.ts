import { useMutation } from '@tanstack/react-query';
import { requestVerificationEmail } from '../api/authApi';

export function useResendVerification() {
  return useMutation({ mutationFn: requestVerificationEmail });
}
