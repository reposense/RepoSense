import { reactive } from 'vue';

export interface Author {
  gitId: string;
  displayName: string;
  emails: string[];
  gitAuthorName: string[];
  ignoreGlobList: string[];
}

export interface Branch {
  branch: string;
  blurb: string;
  'ignore-glob-list': string[];
  'ignore-authors-list': string[];
  'file-size-limit': number | null;
  authors: Author[];
}

export interface Group {
  'group-name': string;
  globs: string[];
}

export interface Repo {
  repo: string;
  groups: Group[];
  branches: Branch[];
}

export interface WizardConfig {
  title: string;
  repos: Repo[];
}

export const store = reactive({
  currentStep: 1,
  config: {
    title: 'RepoSense Report',
    repos: [],
  } as WizardConfig,
  nextStep() {
    this.currentStep++;
  },
  prevStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  },
  setStep(step: number) {
    this.currentStep = step;
  },
});
