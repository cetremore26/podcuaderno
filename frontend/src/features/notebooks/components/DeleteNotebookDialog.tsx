import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import type { Notebook } from '../model/types';
import { Dialog } from './Dialog';

interface DeleteNotebookDialogProps {
  readonly notebook: Notebook | null;
  readonly pending: boolean;
  readonly errorMessage?: string;
  readonly onConfirm: (notebook: Notebook) => void;
  readonly onClose: () => void;
}

/**
 * Sketch B9. "Cancelar" is the first focusable element, so showModal() gives it the initial focus and Enter
 * never deletes by accident.
 */
export function DeleteNotebookDialog({
  notebook,
  pending,
  errorMessage,
  onConfirm,
  onClose,
}: DeleteNotebookDialogProps) {
  return (
    <Dialog
      open={notebook !== null}
      title={notebook ? `¿Eliminar «${notebook.title}»?` : ''}
      onClose={onClose}
    >
      {errorMessage ? <Alert tone="error">{errorMessage}</Alert> : null}
      <p>Se borrarán todos sus temas y conceptos. No se puede deshacer.</p>
      <div className="dialog__actions">
        <Button variant="secondary" onClick={onClose}>
          Cancelar
        </Button>
        <Button
          variant="danger"
          disabled={pending}
          onClick={() => {
            if (notebook) {
              onConfirm(notebook);
            }
          }}
        >
          Eliminar
        </Button>
      </div>
    </Dialog>
  );
}
