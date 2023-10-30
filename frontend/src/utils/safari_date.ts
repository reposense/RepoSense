// date keys for handling safari date input //
function isIntegerKey(key: string) {
  return !Number.isNaN(+key);
}

function isArrowOrEnterKey(key: string) {
  return key === 'ArrowDown' || key === 'ArrowLeft' || key === 'ArrowRight' || key === 'ArrowUp' || key === 'Enter';
}

function isBackSpaceOrDeleteKey(key: string) {
  return key === 'Backspace' || key === 'Delete';
}

function validateInputDate(event: KeyboardEvent) {
  const key = event.key;
  // only allow integer, backspace, delete, arrow or enter keys
  if (!(isIntegerKey(key) || isBackSpaceOrDeleteKey(key) || isArrowOrEnterKey(key))) {
    event.preventDefault();
  }
}

function deleteDashInputDate(event: KeyboardEvent) {
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

function formatInputDateOnKeyDown(event: KeyboardEvent) {
  validateInputDate(event);
  deleteDashInputDate(event);
}

function appendDashInputDate(event: KeyboardEvent) {
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

Object.assign(window, { formatInputDateOnKeyDown, appendDashInputDate });

export default 'test';
