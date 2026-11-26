import { useLocation } from 'react-router';

/** Shows the current URL so tests can assert navigation (path, search and fragment). */
export function LocationProbe() {
  const { pathname, search, hash } = useLocation();
  return <div data-testid="location">{`${pathname}${search}${hash}`}</div>;
}
