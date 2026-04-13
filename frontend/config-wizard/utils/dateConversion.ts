/**
 * Converts a stored dd/MM/yyyy[ HH:mm] string back into native input values.
 * Used when re-entering Step 2 after previously saving to the store.
 */
export const parseStoredDate = (stored: string | null): { date: string, time: string } => {
  if (!stored) return { date: '', time: '' };
  const [datePart, timePart] = stored.split(' ');
  const [d, m, y] = datePart.split('/');
  return {
    date: `${y}-${m.padStart(2, '0')}-${d.padStart(2, '0')}`,
    // Keep only HH:mm — <input type="time"> cannot produce seconds, so we drop them.
    // Edge case: a config manually written with HH:mm:ss will silently lose the seconds
    // on round-trip through the wizard. Acceptable given the picker-only input model.
    time: timePart ? timePart.substring(0, 5) : '',
  };
};

/**
 * Converts native date/time input values to the dd/MM/yyyy[ HH:mm] format
 * expected by LocalDateTimeParser. Returns null if no date selected.
 */
export const toStoredDate = (date: string, time: string): string | null => {
  if (!date) return null;
  const [y, m, d] = date.split('-');
  const datePart = `${d}/${m}/${y}`;
  return time ? `${datePart} ${time}` : datePart;
};
