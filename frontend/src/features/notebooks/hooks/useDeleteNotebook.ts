import { useMutation, useQueryClient } from '@tanstack/react-query';
import { deleteNotebook } from '../api/notebooksApi';
import { NOTEBOOKS_KEY } from './useNotebooks';

export function useDeleteNotebook() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteNotebook,
    // Also after a 404: the list must drop a notebook deleted elsewhere.
    onSettled: () => queryClient.invalidateQueries({ queryKey: NOTEBOOKS_KEY }),
  });
}
