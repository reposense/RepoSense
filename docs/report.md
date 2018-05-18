# File output format

## repo.json
```
{
	'CS2103JAN2018-W13-B3_main':{ 
		repo: 'main',
		organization: 'CS2103JAN2018-W13-B3',
		branch: 'master',
		displayName: 'CS2103JAN2018-W13-B3_main',
		fromDate: '2018-02-26' 
	} 
}
```

## BRANCH_NAME/summary.json
```
{
	authorWeeklyIntervalContributions: {...}
	authorDailyIntervalContributions: {...}
	authorFinalContributionMap: {...}
	authorContributionVariance: {...}
	authorDisplayNameMap: {...}
}
```

## BRANCH_NAME/gitblame.json
```
[
	{
		path: 'filepath/filename.ext',
		lines: [
			{...},
			{...}
		],
		authorContributionMap: {...} 	
	}
]
```

