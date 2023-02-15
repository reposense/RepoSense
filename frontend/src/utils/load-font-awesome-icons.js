import { library } from '@fortawesome/fontawesome-svg-core';
import {
  faCaretDown, faCaretRight, faChevronCircleDown, faChevronCircleUp,
  faChevronDown, faChevronUp, faCircle, faCode,
  faEllipsisH, faExclamation, faHistory, faListUl,
  faPlusCircle, faSpinner, faTags, faUser, faUserEdit,
  faDatabase,
} from '@fortawesome/free-solid-svg-icons';

import {
  faGithub, faGitlab, faBitbucket,
} from '@fortawesome/free-brands-svg-icons';

// app
library.add(faExclamation, faSpinner, faCode, faListUl);

// c-resizer
library.add(faCaretRight);

// c-summary
library.add(faExclamation);

// c-summary-charts
library.add(
  faChevronUp,
  faChevronDown,
  faDatabase,
  faUser,
  faCode,
  faListUl,
  faCircle,
  faGithub,
  faGitlab,
  faBitbucket,
);

// c-zoom
library.add(faTags, faEllipsisH);

// c-authorship
library.add(faCaretDown, faCaretRight, faHistory, faUserEdit);

// c-segment
library.add(faPlusCircle, faChevronCircleUp, faChevronCircleDown);
