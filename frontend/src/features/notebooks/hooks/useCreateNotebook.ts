import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createNotebook } from '../api/notebooksApi';
import { NOTEBOOKS_KEY } from './useNotebooks';

export function useCreateNotebook() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createNotebook,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: NOTEBOOKS_KEY }),
  });
}
