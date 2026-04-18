/**
 * Lazy loader for Pagefind search functionality.
 * This script dynamically imports pagefind.js only when needed (when user opens search).
 * This avoids loading the search library on initial page load.
 */
window.__pagefind__ = null;

window.loadPagefind = async () => {
  if (!window.__pagefind__) {
    // baseUrl is defined in page.njk script tag
    const baseUrl = window.baseUrl || '';
    const module = await import(`${baseUrl}/markbind/pagefind/pagefind.js`);
    window.__pagefind__ = module;
    module.init();
  }
  return window.__pagefind__;
};