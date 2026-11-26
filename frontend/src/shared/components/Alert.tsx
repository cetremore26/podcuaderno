import type { ReactNode } from 'react';

export type AlertTone = 'error' | 'success' | 'info';

interface AlertProps {
  readonly tone?: AlertTone;
  readonly children: ReactNode;
}

/** Message box: errors interrupt screen readers (role="alert"), the rest are polite (role="status"). */
export function Alert({ tone = 'info', children }: AlertProps) {
  return (
    <div
      role={tone === 'error' ? 'alert' : 'status'}
      className={tone === 'info' ? 'alert' : `alert alert--${tone}`}
    >
      {children}
    </div>
  );
}
