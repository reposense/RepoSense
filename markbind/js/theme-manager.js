/**
 * Loaded as early as possible in <head> to prevent flash of unstyled content (FOUC).
 * Sets data-bs-theme on <html> before any stylesheets are applied.
 *
 * Reads from <html> attributes set by the server:
 *   - data-dark-mode="true|false"
 */
(function () {
  if (document.documentElement.getAttribute('data-dark-mode') !== 'true') return;

  const STORAGE_KEY = 'markbind-theme';
  let stored;
  try { stored = localStorage.getItem(STORAGE_KEY); } catch (e) { /* ignore */ }

  let theme;
  if (stored === 'dark' || stored === 'light') {
    theme = stored;
  } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    theme = 'dark';
  } else {
    theme = 'light';
  }
  document.documentElement.setAttribute('data-bs-theme', theme);
  window.__MARKBIND_THEME__ = theme;
  window.__MARKBIND_DARK_MODE__ = true;
}());
