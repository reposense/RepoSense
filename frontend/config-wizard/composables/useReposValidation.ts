import { reactive } from 'vue';
import { type LocalRepo } from '../types/wizard';

export const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Key format uses `|` as separator (safe: `|` is not valid in glob patterns or email addresses).
 *   globErrors:  `${ri}|${bi}|${pattern}`
 *   emailErrors: `${ri}|${bi}|${ai}|${email}`
 *
 * Using the value as part of the key means each invalid entry has its own slot, so:
 *   - Adding a valid entry only clears that specific entry's error (not others in the same list).
 *   - Removing a chip clears exactly that chip's error via clearGlobError / clearEmailError.
 *   - Removal of a repo/branch/author runs the corresponding cleanup function to delete stale
 *     keys and shift remaining indices so getOnNextError() never blocks on a phantom error.
 */

// Collects keys to delete (seg === removedVal) and rename (seg > removedVal, decrement by 1)
// for a given segment index across the entire flat map.
const shiftMapKeys = (
  map: Record<string, string>,
  segIdx: number,
  removedVal: number,
  parentFilter?: (segs: string[]) => boolean,
) => {
  const toDelete: string[] = [];
  const toRename: [string, string][] = [];

  for (const key of Object.keys(map)) {
    const segs = key.split('|');
    if (parentFilter && !parentFilter(segs)) continue;
    const n = parseInt(segs[segIdx], 10);
    if (n === removedVal) {
      toDelete.push(key);
    } else if (n > removedVal) {
      const newSegs = [...segs];
      newSegs[segIdx] = String(n - 1);
      toRename.push([key, newSegs.join('|')]);
    }
  }

  // Apply deletions first, then renames (all old keys were collected before any mutation).
  for (const key of toDelete) delete map[key];
  for (const [oldKey, newKey] of toRename) {
    map[newKey] = map[oldKey];
    delete map[oldKey];
  }
};

export function useReposValidation(repos: LocalRepo[]) {
  const globErrors = reactive<Record<string, string>>({});
  const emailErrors = reactive<Record<string, string>>({});

  // --- Display helpers ---

  /** Returns a joined error string for all invalid glob patterns on a branch. */
  const getBranchGlobError = (ri: number, bi: number): string => {
    const prefix = `${ri}|${bi}|`;
    return Object.entries(globErrors)
      .filter(([k]) => k.startsWith(prefix))
      .map(([, v]) => v)
      .join('\n');
  };

  /**
   * Returns email errors for a branch sliced by author index.
   * Key: String(ai), Value: joined error string for all invalid emails on that author.
   * Passed to BranchCard, which forwards the per-author string to each AuthorCard.
   */
  const getBranchEmailErrors = (ri: number, bi: number): Record<string, string> => {
    const prefix = `${ri}|${bi}|`;
    const byAuthor: Record<string, string[]> = {};
    for (const key of Object.keys(emailErrors)) {
      if (!key.startsWith(prefix)) continue;
      const rest = key.slice(prefix.length); // `${ai}|${email}`
      const pipeIdx = rest.indexOf('|');
      const ai = rest.substring(0, pipeIdx);
      (byAuthor[ai] ??= []).push(emailErrors[key]);
    }
    return Object.fromEntries(
      Object.entries(byAuthor).map(([ai, msgs]) => [ai, msgs.join('; ')]),
    );
  };

  // --- Stale-key cleanup on removal ---

  /** Call before splicing repos[ri]. Deletes its error keys; shifts higher repo indices down. */
  const cleanupOnRepoRemove = (ri: number) => {
    shiftMapKeys(globErrors, 0, ri);
    shiftMapKeys(emailErrors, 0, ri);
  };

  /** Call before splicing repo.branches[bi]. Deletes its error keys; shifts higher branch indices. */
  const cleanupOnBranchRemove = (ri: number, bi: number) => {
    const isThisRepo = (segs: string[]) => parseInt(segs[0], 10) === ri;
    shiftMapKeys(globErrors, 1, bi, isThisRepo);
    shiftMapKeys(emailErrors, 1, bi, isThisRepo);
  };

  /** Call before splicing branch.authors[ai]. Deletes its email error keys; shifts higher author indices. */
  const cleanupOnAuthorRemove = (ri: number, bi: number, ai: number) => {
    const isThisBranch = (segs: string[]) =>
      parseInt(segs[0], 10) === ri && parseInt(segs[1], 10) === bi;
    shiftMapKeys(emailErrors, 2, ai, isThisBranch);
  };

  // --- Tier 1 validators ---

  /** URL validation (backend). Bug 4 fix: resets valid/error state even when field is cleared. */
  const validateRepo = async (repo: LocalRepo): Promise<void> => {
    if (!repo.repo) {
      repo.valid = false;
      repo.error = '';
      return;
    }
    repo.validating = true;
    repo.error = '';
    repo.valid = false;
    try {
      const resp = await fetch('/api/validate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ location: repo.repo }),
      });
      const data = await resp.json();
      if (data.valid) {
        repo.valid = true;
      } else {
        repo.error = data.error || 'Invalid repository location';
      }
    } catch {
      repo.error = 'Could not validate — server unreachable';
    } finally {
      repo.validating = false;
    }
  };

  /** Glob syntax validation (backend). Called when a tag is added. */
  const validateGlob = async (pattern: string, ri: number, bi: number): Promise<void> => {
    const key = `${ri}|${bi}|${pattern}`;
    try {
      const resp = await fetch('/api/validate-glob', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pattern }),
      });
      const data = await resp.json();
      if (!data.valid) {
        globErrors[key] = `Invalid pattern "${pattern}": ${data.error}`;
      } else {
        delete globErrors[key];
      }
    } catch {
      // non-critical, silently ignore
    }
  };

  /** Clears the glob error for a specific pattern. Called when a tag chip is removed. */
  const clearGlobError = (pattern: string, ri: number, bi: number): void => {
    delete globErrors[`${ri}|${bi}|${pattern}`];
  };

  /**
   * Revalidates the entire email list for one author. Called on both tag-added and tag-removed
   * so the error state always matches the current chip list exactly.
   *
   * Re-validating all on removal (rather than clearing a single key) avoids fragile
   * value-matching between the removed chip and the stored error key, and is cheap
   * because email validation is a client-side regex with no async overhead.
   */
  const validateAllEmails = (emails: string[], ri: number, bi: number, ai: number): void => {
    // Clear all existing errors for this author first.
    const prefix = `${ri}|${bi}|${ai}|`;
    for (const key of Object.keys(emailErrors)) {
      if (key.startsWith(prefix)) delete emailErrors[key];
    }
    // Set errors for each currently invalid email.
    for (const email of emails) {
      if (!EMAIL_RE.test(email)) {
        emailErrors[`${prefix}${email}`] = `"${email}" is not a valid email address`;
      }
    }
  };

  // --- onNext gate ---

  /**
   * Runs all Tier 1 (required fields / format) and Tier 2 (duplicate) checks.
   * Returns the first error message found, or null if everything is valid.
   * Callers are responsible for displaying the message (e.g. alert()).
   */
  const getOnNextError = (): string | null => {
    // Tier 1: required fields
    if (repos.some((r) => !r.repo.trim())) {
      return 'Every repository must have a URL.';
    }
    if (repos.some((r) => r.branches.some((b) => b.branch.includes(' ')))) {
      return 'Branch names cannot contain spaces.';
    }
    if (repos.some((r) => r.branches.some((b) =>
      b.authors.some((a) => !a.gitId.trim() || a.gitId.includes(' ')),
    ))) {
      return 'Every author must have a valid Git Host ID (no spaces).';
    }
    if (Object.keys(globErrors).length > 0) {
      return 'Please fix invalid glob patterns before proceeding.';
    }
    if (Object.keys(emailErrors).length > 0) {
      return 'Please fix invalid email addresses before proceeding.';
    }
    for (const repo of repos) {
      for (const branch of repo.branches) {
        const { sinceDate, sinceTime, untilDate, untilTime } = branch;
        if (sinceDate && untilDate) {
          if (sinceDate > untilDate) {
            return 'Since date must be on or before until date.';
          }
          if (sinceDate === untilDate && sinceTime && untilTime && sinceTime > untilTime) {
            return 'Since time must be on or before until time on the same date.';
          }
        }
      }
    }

    // Tier 2: duplicate checks
    const urls = repos.map((r) => r.repo.trim());
    if (new Set(urls).size !== urls.length) {
      return 'Duplicate repository URLs are not allowed.';
    }
    for (const repo of repos) {
      const branchNames = repo.branches.map((b) => b.branch.trim());
      if (new Set(branchNames).size !== branchNames.length) {
        return `Repository "${repo.repo}" has duplicate branch names.`;
      }
      for (const branch of repo.branches) {
        const authorIds = branch.authors.map((a) => a.gitId.trim());
        if (new Set(authorIds).size !== authorIds.length) {
          return `Branch "${branch.branch || 'default'}" in "${repo.repo}" has duplicate author IDs.`;
        }
      }
    }

    return null;
  };

  return {
    getBranchGlobError,
    getBranchEmailErrors,
    cleanupOnRepoRemove,
    cleanupOnBranchRemove,
    cleanupOnAuthorRemove,
    validateRepo,
    validateGlob,
    clearGlobError,
    validateAllEmails,
    getOnNextError,
  };
}
