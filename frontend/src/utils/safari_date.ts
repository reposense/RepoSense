// date keys for handling safari date input //

/**
 * Checks if a key represents an integer when coerced to a number.
 * @param {string} key - The keyboard key to check.
 * @returns {boolean} True if the key is a valid integer, false otherwise.
 */
export function isIntegerKey(key: string) {
  return !Number.isNaN(+key);
}

/**
 * Determines if a key is an arrow key or the Enter key.
 * @param {string} key - The keyboard key to check.
 * @returns {boolean} True if the key is an arrow key or Enter, false otherwise.
 */
export function isArrowOrEnterKey(key: string) {
  return key === 'ArrowDown' || key === 'ArrowLeft' || key === 'ArrowRight' || key === 'ArrowUp' || key === 'Enter';
}

/**
 * Checks if a key is Backspace or Delete.
 * @param {string} key - The keyboard key to check.
 * @returns {boolean} True if the key is Backspace or Delete, false otherwise.
 */
export function isBackSpaceOrDeleteKey(key: string) {
  return key === 'Backspace' || key === 'Delete';
}

/**
 * Validates input for a date field, allowing only integers, navigation keys, and deletion keys.
 * @param {KeyboardEvent} event - The keyboard event triggered by user input.
 */
export function validateInputDate(event: KeyboardEvent) {
  const key = event.key;
  // only allow integer, backspace, delete, arrow or enter keys
  if (!(isIntegerKey(key) || isBackSpaceOrDeleteKey(key) || isArrowOrEnterKey(key))) {
    event.preventDefault();
  }
}

/**
 * Handles deletion of a dash character in a date input field by removing the preceding character as well.
 * @param {KeyboardEvent} event - The keyboard event triggered by user input.
 */
export function deleteDashInputDate(event: KeyboardEvent) {
  const key = event.key;
  // remove two chars before the cursor's position if deleting dash character
  if (isBackSpaceOrDeleteKey(key) && event.target !== null && 'value' in event.target
    && 'selectionStart' in event.target) {
    const date = event.target.value as string;
    const cursorPosition = event.target.selectionStart as number;
    if (date[cursorPosition - 1] === '-') {
      event.target.value = date.slice(0, cursorPosition - 1);
    }
  }
}

/**
 * Formats a date input field on keydown by validating input and handling dash deletion.
 * @param {KeyboardEvent} event - The keyboard event triggered by user input.
 */
export function formatInputDateOnKeyDown(event: KeyboardEvent) {
  validateInputDate(event);
  deleteDashInputDate(event);
}

/**
 * Appends a dash to a date input field when the input matches specific patterns (e.g., 'yyyy' or 'yyyy-mm').
 * @param {KeyboardEvent} event - The keyboard event triggered by user input.
 */
export function appendDashInputDate(event: KeyboardEvent) {
  // append dash to date with format yyyy-mm-dd
  if (event.target !== null && 'value' in event.target) {
    const date = event.target.value as string;
    if (date.match(/^\d{4}$/) !== null) {
      event.target.value = `${event.target.value}-`;
    } else if (date.match(/^\d{4}-\d{2}$/) !== null) {
      event.target.value = `${event.target.value}-`;
    }
  }
}

/**
 * Assigns date formatting functions to the global window object.
 * @type {Object}
 */
Object.assign(window, { formatInputDateOnKeyDown, appendDashInputDate });

export default 'test';
