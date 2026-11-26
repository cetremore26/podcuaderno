import { useState } from 'react';
import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import { usePageTitle } from '@/shared/hooks/usePageTitle';
import { useCreateNotebook } from '../hooks/useCreateNotebook';
import { useDeleteNotebook } from '../hooks/useDeleteNotebook';
import { useNotebooks } from '../hooks/useNotebooks';
import { useRenameNotebook } from '../hooks/useRenameNotebook';
import {
  deleteNotebookErrorMessage,
  isNotebookGone,
  NOTEBOOK_GONE_MESSAGE,
  saveNotebookErrorMessage,
} from '../model/errorMessages';
import type { Notebook } from '../model/types';
import { DeleteNotebookDialog } from './DeleteNotebookDialog';
import { NotebookList } from './NotebookList';
import { NotebookTitleDialog } from './NotebookTitleDialog';

type OpenDialog =
  | { readonly kind: 'create' }
  | { readonly kind: 'rename'; readonly notebook: Notebook }
  | { readonly kind: 'delete'; readonly notebook: Notebook }
  | null;

/** "Mis cuadernos" (HU-06, sketches B6 to B9). */
export function NotebooksPage() {
  usePageTitle('Mis cuadernos');
  const notebooks = useNotebooks();
  const create = useCreateNotebook();
  const rename = useRenameNotebook();
  const remove = useDeleteNotebook();
  const [dialog, setDialog] = useState<OpenDialog>(null);
  const [notice, setNotice] = useState<string | null>(null);

  function open(next: OpenDialog) {
    create.reset();
    rename.reset();
    remove.reset();
    setNotice(null);
    setDialog(next);
  }

  function close() {
    setDialog(null);
  }

  function closeIfGone(error: unknown) {
    if (isNotebookGone(error)) {
      setDialog(null);
      setNotice(NOTEBOOK_GONE_MESSAGE);
    }
  }

  const renaming = dialog?.kind === 'rename' ? dialog.notebook : null;
  const deleting = dialog?.kind === 'delete' ? dialog.notebook : null;

  return (
    <section className="stack">
      <h1>Mis cuadernos</h1>
      <Button block onClick={() => open({ kind: 'create' })}>
        <span aria-hidden="true">+&nbsp;</span>Nuevo cuaderno
      </Button>
      {notice ? <Alert tone="error">{notice}</Alert> : null}
      <NotebooksContent
        query={notebooks}
        onRename={(notebook) => open({ kind: 'rename', notebook })}
        onDelete={(notebook) => open({ kind: 'delete', notebook })}
      />

      <NotebookTitleDialog
        open={dialog?.kind === 'create'}
        heading="Nuevo cuaderno"
        submitLabel="Crear"
        pending={create.isPending}
        errorMessage={create.isError ? saveNotebookErrorMessage(create.error) : undefined}
        onSubmit={(title) => create.mutate(title, { onSuccess: close })}
        onClose={close}
      />
      <NotebookTitleDialog
        open={renaming !== null}
        heading="Cambiar nombre"
        submitLabel="Guardar"
        initialTitle={renaming?.title}
        pending={rename.isPending}
        errorMessage={rename.isError ? saveNotebookErrorMessage(rename.error) : undefined}
        onSubmit={(title) => {
          if (renaming) {
            rename.mutate({ id: renaming.id, title }, { onSuccess: close, onError: closeIfGone });
          }
        }}
        onClose={close}
      />
      <DeleteNotebookDialog
        notebook={deleting}
        pending={remove.isPending}
        errorMessage={remove.isError ? deleteNotebookErrorMessage(remove.error) : undefined}
        onConfirm={(notebook) =>
          remove.mutate(notebook.id, { onSuccess: close, onError: closeIfGone })
        }
        onClose={close}
      />
    </section>
  );
}

interface NotebooksContentProps {
  readonly query: ReturnType<typeof useNotebooks>;
  readonly onRename: (notebook: Notebook) => void;
  readonly onDelete: (notebook: Notebook) => void;
}

function NotebooksContent({ query, onRename, onDelete }: NotebooksContentProps) {
  if (query.isPending) {
    return <p role="status">Cargando tus cuadernos…</p>;
  }
  if (query.isError) {
    return (
      <div className="stack">
        <Alert tone="error">No pudimos cargar tus cuadernos.</Alert>
        <Button variant="secondary" onClick={() => void query.refetch()}>
          Intentar de nuevo
        </Button>
      </div>
    );
  }
  if (query.data.length === 0) {
    return (
      <div className="empty">
        <p>Todavía no tienes cuadernos.</p>
        <p className="muted">Crea uno para cada materia.</p>
      </div>
    );
  }
  return <NotebookList notebooks={query.data} onRename={onRename} onDelete={onDelete} />;
}
