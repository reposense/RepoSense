/**
 * Loaded immediately after the highlight stylesheet <link> elements.
 * Disables the stylesheet that doesn't match the active theme.
 *
 * Reads from window.__MARKBIND_THEME__ (set by theme-manager.js) or falls back
 * to the data-code-theme attribute on <html> set by the server.
 */
(function () {
  const theme = window.__MARKBIND_THEME__
    || document.documentElement.getAttribute('data-code-theme')
    || 'light';

  const lightSheet = document.getElementById('markbind-highlight-light');
  const darkSheet = document.getElementById('markbind-highlight-dark');

  if (lightSheet && darkSheet) {
    lightSheet.disabled = theme !== 'light';
    darkSheet.disabled = theme !== 'dark';
  }
}());
