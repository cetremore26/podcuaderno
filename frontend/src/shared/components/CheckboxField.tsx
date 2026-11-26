import { useId, type ComponentPropsWithRef, type ReactNode } from 'react';

interface CheckboxFieldProps extends Omit<ComponentPropsWithRef<'input'>, 'id' | 'type'> {
  readonly label: ReactNode;
  readonly description?: ReactNode;
  readonly error?: string;
}

/** Checkbox with its label, an optional description and an error announced through aria-describedby. */
export function CheckboxField({ label, description, error, ...props }: CheckboxFieldProps) {
  const id = useId();
  const descriptionId = `${id}-description`;
  const errorId = `${id}-error`;
  const describedBy = [description ? descriptionId : '', error ? errorId : '']
    .filter(Boolean)
    .join(' ');

  return (
    <div className="field">
      <div className="checkbox">
        <input
          id={id}
          type="checkbox"
          className="checkbox__input"
          aria-invalid={error ? true : undefined}
          aria-describedby={describedBy || undefined}
          {...props}
        />
        <label htmlFor={id}>{label}</label>
      </div>
      {description ? (
        <div id={descriptionId} className="field__hint">
          {description}
        </div>
      ) : null}
      {error ? (
        <span id={errorId} className="field__error">
          {error}
        </span>
      ) : null}
    </div>
  );
}
