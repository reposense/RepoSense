import { User } from '../types/types';
import { FilterGroupSelection } from '../types/summary';

function getTotalCommits(total: number, group: User): number {
  // If group.checkedFileTypeContribution === undefined, then we treat it as 0 contribution
  return total + (group.checkedFileTypeContribution ?? 0);
}

function getGroupCommitsVariance(total: number, group: User): number {
  return total + group.variance;
}

function checkKeyAndGetValue<T extends object>(element: T, key?: string) {
  // invalid key provided
  if (key === undefined || !(key in element)) {
    return undefined;
  }
  return element[key as keyof T];
}

/**
 * Returns an empty string if an invalid/no key is provided or if the value retrieved is not a ComparablePrimitive.
 * This permits and results in no sorting being done in the above cases. If function is to be made stricter, assertions
 * or errors may be thrown where empty strings are returned.
 */
function getComparablePrimitive(element: User, key?: string): string | number {
  const val = checkKeyAndGetValue(element, key);
  // value retrieved is not a comparable primitive
  if (typeof val !== 'string' && typeof val !== 'number') {
    return '';
  }
  return val;
}

/**
 * Array is not sorted when sortingOption is not provided. sortingOption is optional to allow it to fit the
 * SortingFunction<T> interface.
 * */
function sortingHelper(element: User[], sortingOption?: string): string | number {
  switch (sortingOption) {
  case 'totalCommits':
    return element.reduce(getTotalCommits, 0);
  case 'variance':
    return element.reduce(getGroupCommitsVariance, 0);
  case 'displayName':
    return window.getAuthorDisplayName(element);
  default:
    return getComparablePrimitive(element[0], sortingOption);
  }
}

function groupByRepos(
  repos: User[][],
  sortingControl: {
    sortingOption: string,
    sortingWithinOption: string,
    isSortingDsc: string,
    isSortingWithinDsc: string, },
): User[][] {
  const sortedRepos: User[][] = [];
  const {
    sortingWithinOption, sortingOption, isSortingDsc, isSortingWithinDsc,
  } = sortingControl;
  const sortWithinOption = sortingWithinOption === 'title' ? 'displayName' : sortingWithinOption;
  const sortOption = sortingOption === 'groupTitle' ? 'searchPath' : sortingOption;
  repos.forEach((users) => {
    if (sortWithinOption === 'totalCommits') {
      users.sort(window.comparator((ele) => ele.checkedFileTypeContribution ?? 0));
    } else {
      users.sort(window.comparator((ele) => getComparablePrimitive(ele, sortWithinOption)));
    }

    if (isSortingWithinDsc) {
      users.reverse();
    }
    sortedRepos.push(users);
  });
  sortedRepos.sort(window.comparator(sortingHelper, sortOption));
  if (isSortingDsc) {
    sortedRepos.reverse();
  }
  return sortedRepos;
}

function groupByNone(
  repos: User[][],
  sortingControl: {
    sortingOption: string,
    isSortingDsc: string, },
): User[] {
  const sortedRepos: User[] = [];
  const { sortingOption, isSortingDsc } = sortingControl;
  const isSortingGroupTitle = sortingOption === 'groupTitle';
  repos.forEach((users) => {
    users.forEach((user) => {
      sortedRepos.push(user);
    });
  });
  sortedRepos.sort(window.comparator((repo) => {
    if (isSortingGroupTitle) {
      return `${repo.searchPath}${repo.name}`;
    }
    if (sortingOption === 'totalCommits') {
      return repo.checkedFileTypeContribution ?? 0;
    }
    return getComparablePrimitive(repo, sortingOption);
  }));
  if (isSortingDsc) {
    sortedRepos.reverse();
  }

  return sortedRepos;
}

function groupByAuthors(
  repos: User[][],
  sortingControl: {
    sortingOption: string,
    sortingWithinOption: string,
    isSortingDsc: string,
    isSortingWithinDsc: string, },
): User[][] {
  const authorMap: { [userName: string]: User[] } = {};
  const filtered: User[][] = [];
  const {
    sortingWithinOption, sortingOption, isSortingDsc, isSortingWithinDsc,
  } = sortingControl;
  const sortWithinOption = sortingWithinOption === 'title' ? 'searchPath' : sortingWithinOption;
  const sortOption = sortingOption === 'groupTitle' ? 'displayName' : sortingOption;
  repos.forEach((users) => {
    users.forEach((user) => {
      if (Object.keys(authorMap).includes(user.name)) {
        authorMap[user.name].push(user);
      } else {
        authorMap[user.name] = [user];
      }
    });
  });
  Object.keys(authorMap).forEach((author) => {
    if (sortWithinOption === 'totalCommits') {
      authorMap[author].sort(window.comparator((repo) => repo.checkedFileTypeContribution ?? 0));
    } else {
      authorMap[author].sort(window.comparator((repo) => getComparablePrimitive(repo, sortingWithinOption)));
    }
    if (isSortingWithinDsc) {
      authorMap[author].reverse();
    }
    filtered.push(authorMap[author]);
  });

  filtered.sort(window.comparator(sortingHelper, sortOption));
  if (isSortingDsc) {
    filtered.reverse();
  }
  return filtered;
}

function sortFiltered(
  filtered: User[][],
  filterControl: {
    filterGroupSelection: FilterGroupSelection,
    sortingOption: string,
    sortingWithinOption: string,
    isSortingDsc: string,
    isSortingWithinDsc: string, },
): User[][] {
  const { filterGroupSelection } = filterControl;
  let full = [];

  if (filterGroupSelection === 'groupByNone') {
    // push all repos into the same group
    full[0] = groupByNone(filtered, filterControl);
  } else if (filterGroupSelection === 'groupByAuthors') {
    full = groupByAuthors(filtered, filterControl);
  } else {
    full = groupByRepos(filtered, filterControl);
  }

  return full;
}

export default sortFiltered;
