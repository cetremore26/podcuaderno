import { useEffect, useId, useRef, type ReactNode } from 'react';

interface DialogProps {
  readonly open: boolean;
  readonly title: string;
  readonly onClose: () => void;
  readonly children: ReactNode;
}

/**
 * Native modal dialog (ADR-0011): showModal() traps the focus and Esc closes it.
 * The content is mounted only while it is open, so every opening starts with a fresh form.
 */
export function Dialog({ open, title, onClose, children }: DialogProps) {
  const ref = useRef<HTMLDialogElement>(null);
  const titleId = useId();

  useEffect(() => {
    const dialog = ref.current;
    if (!dialog) {
      return;
    }
    if (open && !dialog.open) {
      dialog.showModal();
    } else if (!open && dialog.open) {
      dialog.close();
    }
  }, [open]);

  return (
    <dialog
      ref={ref}
      aria-labelledby={titleId}
      onCancel={(event) => {
        // Esc: React state decides when the dialog closes.
        event.preventDefault();
        onClose();
      }}
    >
      {open ? (
        <>
          <h2 id={titleId}>{title}</h2>
          {children}
        </>
      ) : null}
    </dialog>
  );
}
