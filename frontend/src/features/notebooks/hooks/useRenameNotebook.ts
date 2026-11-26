import { useMutation, useQueryClient } from '@tanstack/react-query';
import { renameNotebook } from '../api/notebooksApi';
import { NOTEBOOKS_KEY } from './useNotebooks';

export function useRenameNotebook() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: renameNotebook,
    // Also after a 404: the list must drop a notebook deleted elsewhere.
    onSettled: () => queryClient.invalidateQueries({ queryKey: NOTEBOOKS_KEY }),
  });
}
