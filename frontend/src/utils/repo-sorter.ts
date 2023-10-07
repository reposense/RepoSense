import { User } from '../types/types';
import { FilterGroupSelection } from '../types/summary';

function getTotalCommits(total: number, group: User) {
  // If group.checkedFileTypeContribution === undefined, then we treat it as 0 contribution
  return total + (group.checkedFileTypeContribution ?? 0);
}

function getGroupCommitsVariance(total: number, group: User) {
  return total + group.variance;
}

function checkKeyAndGetValue(element: object, key: string | undefined) {
  if (key === undefined || !(key in element)) {
    return undefined;
  }
  /** Casting here is safe as guard clause above ensures that element has an attribute with the same name as the string
   *  represented by key. */
  return (element as any)[key];
}

/** Array is not sorted when sortingOption is not provided. sortingOption is optional to allow it to fit the
 *  SortingFunction<T> interface. */
function sortingHelper(element: User[], sortingOption?: string): string | number {
  if (sortingOption === 'totalCommits') {
    return element.reduce(getTotalCommits, 0);
  }
  if (sortingOption === 'variance') {
    return element.reduce(getGroupCommitsVariance, 0);
  }
  if (sortingOption === 'displayName') {
    return window.getAuthorDisplayName(element);
  }
  return checkKeyAndGetValue(element[0], sortingOption);
}

function groupByRepos(
  repos: User[][],
  sortingControl: {
    sortingOption: string;
    sortingWithinOption: string;
    isSortingDsc: string;
    isSortingWithinDsc: string; },
) {
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
      users.sort(window.comparator((ele) => checkKeyAndGetValue(ele, sortWithinOption)));
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
    sortingOption: string;
    isSortingDsc: string; },
) {
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
      return repo.checkedFileTypeContribution;
    }
    return checkKeyAndGetValue(repo, sortingOption);
  }));
  if (isSortingDsc) {
    sortedRepos.reverse();
  }

  return sortedRepos;
}

function groupByAuthors(
  repos: User[][],
  sortingControl: {
    sortingOption: string;
    sortingWithinOption: string;
    isSortingDsc: string;
    isSortingWithinDsc: string; },
) {
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
      authorMap[author].sort(window.comparator((repo) => checkKeyAndGetValue(repo, sortingWithinOption)));
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
    filterGroupSelection: FilterGroupSelection;
    sortingOption: string;
    sortingWithinOption: string;
    isSortingDsc: string;
    isSortingWithinDsc: string; },
) {
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
