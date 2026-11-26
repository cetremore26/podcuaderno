import { Button } from '@/shared/components/Button';
import { formatUpdatedAt } from '../model/formatUpdatedAt';
import type { Notebook } from '../model/types';

interface NotebookItemProps {
  readonly notebook: Notebook;
  readonly onRename: (notebook: Notebook) => void;
  readonly onDelete: (notebook: Notebook) => void;
}

export function NotebookItem({ notebook, onRename, onDelete }: NotebookItemProps) {
  return (
    <li className="notebook">
      <h2 className="notebook__title">{notebook.title}</h2>
      <p className="muted">{formatUpdatedAt(notebook.updatedAt)}</p>
      <div className="notebook__actions">
        <Button
          variant="secondary"
          aria-label={`Cambiar nombre de ${notebook.title}`}
          onClick={() => onRename(notebook)}
        >
          Cambiar nombre
        </Button>
        <Button
          variant="secondary"
          aria-label={`Eliminar ${notebook.title}`}
          onClick={() => onDelete(notebook)}
        >
          Eliminar
        </Button>
      </div>
    </li>
  );
}
