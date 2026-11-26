import { useId, type ComponentPropsWithRef } from 'react';

interface TextFieldProps extends Omit<ComponentPropsWithRef<'input'>, 'id'> {
  readonly label: string;
  readonly hint?: string;
  readonly error?: string;
}

/** Labelled input whose hint and error are announced through aria-describedby. */
export function TextField({ label, hint, error, type = 'text', ...props }: TextFieldProps) {
  const id = useId();
  const hintId = `${id}-hint`;
  const errorId = `${id}-error`;
  const describedBy = [hint ? hintId : '', error ? errorId : ''].filter(Boolean).join(' ');

  return (
    <div className="field">
      <label className="field__label" htmlFor={id}>
        {label}
      </label>
      <input
        id={id}
        type={type}
        className="field__input"
        aria-invalid={error ? true : undefined}
        aria-describedby={describedBy || undefined}
        {...props}
      />
      {hint ? (
        <span id={hintId} className="field__hint">
          {hint}
        </span>
      ) : null}
      {error ? (
        <span id={errorId} className="field__error">
          {error}
        </span>
      ) : null}
    </div>
  );
}
