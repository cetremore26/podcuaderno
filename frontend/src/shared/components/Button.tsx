import type { ComponentPropsWithRef } from 'react';
import { buttonClassName, type ButtonVariant } from './buttonClassName';

interface ButtonProps extends ComponentPropsWithRef<'button'> {
  readonly variant?: ButtonVariant;
  readonly block?: boolean;
}

/** Native button with the shared look; defaults to type="button" so it never submits by accident. */
export function Button({
  variant = 'primary',
  block = false,
  type = 'button',
  className,
  ...props
}: ButtonProps) {
  return <button type={type} className={buttonClassName(variant, block, className)} {...props} />;
}
