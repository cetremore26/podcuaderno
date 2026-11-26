import { useQuery } from '@tanstack/react-query';
import { listNotebooks } from '../api/notebooksApi';

export const NOTEBOOKS_KEY = ['notebooks'] as const;

export function useNotebooks() {
  return useQuery({ queryKey: NOTEBOOKS_KEY, queryFn: listNotebooks });
}
