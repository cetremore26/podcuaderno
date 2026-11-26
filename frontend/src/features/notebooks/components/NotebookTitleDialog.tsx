import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Alert } from '@/shared/components/Alert';
import { Button } from '@/shared/components/Button';
import { TextField } from '@/shared/components/TextField';
import { notebookTitleSchema, type NotebookTitleValues } from '../model/schemas';
import { Dialog } from './Dialog';

interface NotebookTitleDialogProps {
  readonly open: boolean;
  /** "Nuevo cuaderno" or "Cambiar nombre". */
  readonly heading: string;
  /** "Crear" or "Guardar". */
  readonly submitLabel: string;
  readonly initialTitle?: string;
  readonly pending: boolean;
  readonly errorMessage?: string;
  readonly onSubmit: (title: string) => void;
  readonly onClose: () => void;
}

/** Sketch B8: create a notebook or rename one (the title is prefilled). */
export function NotebookTitleDialog({
  open,
  heading,
  onClose,
  ...formProps
}: NotebookTitleDialogProps) {
  return (
    <Dialog open={open} title={heading} onClose={onClose}>
      <TitleForm onClose={onClose} {...formProps} />
    </Dialog>
  );
}

function TitleForm({
  submitLabel,
  initialTitle = '',
  pending,
  errorMessage,
  onSubmit,
  onClose,
}: Omit<NotebookTitleDialogProps, 'open' | 'heading'>) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<NotebookTitleValues>({
    resolver: zodResolver(notebookTitleSchema),
    defaultValues: { title: initialTitle },
  });

  return (
    <form className="stack" noValidate onSubmit={handleSubmit((values) => onSubmit(values.title))}>
      {errorMessage ? <Alert tone="error">{errorMessage}</Alert> : null}
      <TextField
        label="Nombre del cuaderno"
        autoComplete="off"
        error={errors.title?.message}
        {...register('title')}
      />
      <div className="dialog__actions">
        <Button variant="secondary" onClick={onClose}>
          Cancelar
        </Button>
        <Button type="submit" disabled={pending}>
          {submitLabel}
        </Button>
      </div>
    </form>
  );
}
