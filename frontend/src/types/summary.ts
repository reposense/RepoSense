export enum FilterGroupSelection {
  GroupByNone = 'groupByNone',
  GroupByRepos = 'groupByRepos',
  GroupByAuthors = 'groupByAuthors',
}

export enum SortGroupSelection {
  GroupTitle = 'groupTitle',
  GroupTitleDsc = 'groupTitle dsc',
  TotalCommits = 'totalCommits',
  TotalCommitsDsc = 'totalCommits dsc',
  Variance = 'variance',
  VarianceDsc = 'variance dsc',
}

export enum SortWithinGroupSelection {
  Title = 'title',
  TitleDsc = 'title dsc',
  TotalCommits = 'totalCommits',
  TotalCommitsDsc = 'totalCommits dsc',
  Variance = 'variance',
  VarianceDsc = 'variance dsc',
}

export enum FilterTimeFrame {
  Commit = 'commit',
  Day = 'day',
  Week = 'week',
}
