import { requestJson } from '@/core/http/httpClient';
import type { Notebook } from '../model/types';

const BASE = '/notebooks';

function notebookPath(id: string): string {
  return `${BASE}/${encodeURIComponent(id)}`;
}

export function listNotebooks(): Promise<Notebook[]> {
  return requestJson<Notebook[]>(BASE);
}

export function createNotebook(title: string): Promise<Notebook> {
  return requestJson<Notebook>(BASE, { method: 'POST', body: JSON.stringify({ title }) });
}

export interface RenameNotebook {
  readonly id: string;
  readonly title: string;
}

export function renameNotebook({ id, title }: RenameNotebook): Promise<Notebook> {
  return requestJson<Notebook>(notebookPath(id), {
    method: 'PATCH',
    body: JSON.stringify({ title }),
  });
}

/** Deletes the notebook with its topics and concepts. */
export function deleteNotebook(id: string): Promise<void> {
  return requestJson<void>(notebookPath(id), { method: 'DELETE' });
}
