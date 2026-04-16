export interface LocalAuthor {
  gitId: string;
  displayName: string;
  emails: string[];
  gitAuthorName: string[];
}

export interface LocalBranch {
  branch: string;
  blurb: string;
  ignoreGlobList: string[];
  ignoreAuthorsList: string[];
  fileSizeLimit: string;
  sinceDate: string;     // yyyy-MM-dd (native date input format)
  sinceTime: string;     // HH:mm (native time input format), empty if not set
  showSinceTime: boolean;
  untilDate: string;
  untilTime: string;
  showUntilTime: boolean;
  authors: LocalAuthor[];
}

export interface LocalRepo {
  repo: string;
  error: string;
  valid: boolean;
  validating: boolean;
  branches: LocalBranch[];
}

export const newAuthor = (): LocalAuthor => ({
  gitId: '',
  displayName: '',
  emails: [],
  gitAuthorName: [],
});

export const newBranch = (): LocalBranch => ({
  branch: '',
  blurb: '',
  ignoreGlobList: [],
  ignoreAuthorsList: [],
  fileSizeLimit: '',
  sinceDate: '',
  sinceTime: '',
  showSinceTime: false,
  untilDate: '',
  untilTime: '',
  showUntilTime: false,
  authors: [],
});

export const newRepo = (): LocalRepo => ({
  repo: '',
  error: '',
  valid: false,
  validating: false,
  branches: [newBranch()],
});
