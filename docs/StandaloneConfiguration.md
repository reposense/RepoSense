# RepoSense - Guide to Setup Standalone Configuration

1. Add [_reposense/config.json](../_reposense/config.json) to the root of your repository.
1. Fill it up with author information, such as the GitHub Id, Display Name (Optional), [Author Names](UserGuide.md#git-author-name)  (Optional), [Ignore Glob List](UserGuide.md#csv-config-file) (Optional).

#### Detailed Explanation
```
{
  "ignoreGlobList": ["**.dat", "**.js"],    <-- Repository level's list of file format to ignore 
  "authors": [                              <-- If a setting is common between Repository and Author,
  {                                             configuration of Author level will always take precedence
    "githubId": "alice",                        over Repository level.
    "displayName": "Alice T.",
    "authorNames": ["AT", "A"],
    "ignoreGlobList": [""]                  <-- Author level's list of file format to ignore.
  },                                            Use "" to override Repository level settings.
  {
    "githubId": "bob"                       
                                            <-- Optional information can be left out to prevent clutter.
  }
 ]
}
```

### Verify your standalone configuration

#### Using github repository url location(s)
1. Run RepoSense using the url link to the repository i.e. `https://github.com/reposense/RepoSense.git`. RepoSense will run the analysis on the `master` branch and use the authors provided in your repository for analysis.

#### Using repo-config.csv
1. Create a [`repo-config.csv`](example.csv) and include the location and branch of the your repository. For example, `https://github.com/reposense/RepoSense.git,master`.
1. Then, run RepoSense with the above created `repo-config.csv`. RepoSense will use the authors provided in your repository for analysis.
