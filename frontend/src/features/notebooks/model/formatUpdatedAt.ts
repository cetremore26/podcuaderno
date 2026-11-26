const relativeTime = new Intl.RelativeTimeFormat('es', { numeric: 'auto' });

const UNITS: readonly [Intl.RelativeTimeFormatUnit, number][] = [
  ['year', 365 * 24 * 60 * 60],
  ['month', 30 * 24 * 60 * 60],
  ['week', 7 * 24 * 60 * 60],
  ['day', 24 * 60 * 60],
  ['hour', 60 * 60],
  ['minute', 60],
];

/** "Modificado hace 5 minutos", "Modificado ayer"... relative to now, in Spanish. */
export function formatUpdatedAt(updatedAt: string, now: number = Date.now()): string {
  const elapsedSeconds = Math.max(0, (now - new Date(updatedAt).getTime()) / 1000);
  const unit = UNITS.find(([, seconds]) => elapsedSeconds >= seconds);
  const text = unit
    ? relativeTime.format(-Math.round(elapsedSeconds / unit[1]), unit[0])
    : relativeTime.format(0, 'second');
  return `Modificado ${text}`;
}
