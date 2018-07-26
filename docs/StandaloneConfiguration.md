# RepoSense - Guide to Setup Standalone Configuration

> This guide uses RepoSense's authors as an example `config.csv`

1. Add [_reposense/config.json](../_reposense/config.json) to the root of your repository.
1. Fill it up with author information, such as the GitHub Id, Display Name (Optional) and the [Author Names](../UserGuide.md#git-author-name) (Optional).
1. Then, create a [`repo-config.csv`](../sample.csv) and include the location and branch of the your repository. For example, `https://github.com/reposense/testrepo-Delta.git,master`.
1. Lastly, run RepoSense with the above created `repo-config.csv`. RepoSense will use the authors provided in your repository for analysis.
