export type ButtonVariant = 'primary' | 'secondary' | 'danger';

/** Class names of a button, also used by links that look like buttons. */
export function buttonClassName(
  variant: ButtonVariant = 'primary',
  block = false,
  extra?: string,
): string {
  return [
    'button',
    variant === 'primary' ? '' : `button--${variant}`,
    block ? 'button--block' : '',
    extra,
  ]
    .filter(Boolean)
    .join(' ');
}
