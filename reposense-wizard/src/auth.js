const GATEKEEPER_URL = 'http://localhost:9999/authenticate';
const CLIENT_ID = 'c498493d4c565ced8d0b';

export function oAuthAuthenticate() {
  window.location = `https://github.com/login/oauth/authorize?client_id=${CLIENT_ID}`;
}

export function checkRedirect() {
  const codeFound = window.location.href.match(/\?code=(.*)/);
  if (codeFound) {
    window.history.replaceState({}, document.title, window.location.pathname);
    return codeFound[1];
  }
  return null;
}

export async function requestToken(code) {
  const response = await fetch(`${GATEKEEPER_URL}/${code}`);
  const json = await response.json();
  return json.token;
}

export async function handleAuthRedirect(githubApi) {
  const code = checkRedirect();
  if (code == null) {
    return false;
  }
  try {
    const token = await requestToken(code);
    githubApi.authenticate(token);
  } catch {
    return false;
  }
  return true;
}
