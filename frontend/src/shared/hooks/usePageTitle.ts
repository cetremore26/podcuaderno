import { useEffect } from 'react';

const PRODUCT_NAME = 'Podcuaderno';

/** Sets the browser tab title so each page is announced and distinguishable (WCAG 2.4.2). */
export function usePageTitle(title?: string): void {
  useEffect(() => {
    document.title = title ? `${title} · ${PRODUCT_NAME}` : PRODUCT_NAME;
  }, [title]);
}
