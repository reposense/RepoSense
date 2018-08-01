# RepoSense - Guide to Setup Standalone Configuration
### Quick Start
1. Add [_reposense/config.json](../_reposense/config.json) to the root of your repository.
1. Fill it up with author information, such as the GitHub Id, Display Name (Optional), [Author Names](UserGuide.md#git-author-name)  (Optional), [Ignore Glob List](UserGuide.md#csv-config-file) (Optional).

### Detailed explanation of the structure of repo-config.json
```
{
  "ignoreGlobList": ["**.dat", "**.js"],    <-- Repository level's list of file formats to ignore 
  "authors": [                              <-- If a setting is common between Repository and Author,
  {                                             configuration of Author level will always take precedence
    "githubId": "alice",                        over Repository level.
    "displayName": "Alice T.",
    "authorNames": ["AT", "A"],
    "ignoreGlobList": [""]                  <-- Author level's list of file formats to ignore.
  },                                            Use "" to override Repository level settings.
  {
    "githubId": "bob"                       
                                            <-- Optional information can be left out to prevent clutter.
  }
 ]
}
```

### Verify your standalone configuration
Download the latest executable Jar on our [release](https://github.com/reposense/RepoSense/releases/latest).

#### Using github repository url location(s)
1. Run RepoSense using the url link to your repository i.e. `https://github.com/reposense/RepoSense.git`. RepoSense will run the analysis on the `master` branch and use the authors provided in your repository for analysis.

`java -jar RepoSense.jar -repos https://github.com/reposense/RepoSense.git`
