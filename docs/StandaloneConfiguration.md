# RepoSense - Guide to Setup Standalone Configuration

1. Add [_reposense/config.json](../_reposense/config.json) to the root of your repository.
1. Fill it up with author information, such as the GitHub Id, Display Name (Optional) and the [Author Names](UserGuide.md#git-author-name) (Optional).

### Verify your standalone configuration
1. Create a [`repo-config.csv`](example.csv) and include the location and branch of the your repository. For example, `https://github.com/reposense/RepoSense.git,master`.
1. Then, run RepoSense with the above created `repo-config.csv`. RepoSense will use the authors provided in your repository for analysis.
