import type { Notebook } from '../model/types';
import { NotebookItem } from './NotebookItem';

interface NotebookListProps {
  readonly notebooks: readonly Notebook[];
  readonly onRename: (notebook: Notebook) => void;
  readonly onDelete: (notebook: Notebook) => void;
}

/** Keeps the order of the backend: most recently modified first. */
export function NotebookList({ notebooks, onRename, onDelete }: NotebookListProps) {
  return (
    <ul className="notebook-list" aria-label="Cuadernos">
      {notebooks.map((notebook) => (
        <NotebookItem
          key={notebook.id}
          notebook={notebook}
          onRename={onRename}
          onDelete={onDelete}
        />
      ))}
    </ul>
  );
}
