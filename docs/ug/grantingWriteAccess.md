## Granting write-access to a repository

We recommend using a [personal access token](https://github.blog/2013-05-16-personal-api-tokens/) if aiming for the ease of setup and [deploy key](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys) if aiming for enhanced security.

### If you wish to use _personal access token_:
1. **Create a _personal access token_** by following this [guide](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) and give only `public_repo` permission.
1. **Copy the token** for later use.

### If you wish to use _deploy key_:

<box type="info" icon=":fab-windows:">

[Windows users] `ssh-keygen` and `base64` are accessible using [Git Bash](https://gitforwindows.org/).
</box>

1. **Create a public-private key pair** (without a passphrase) using the `ssh-keygen`. <br/>
   i.e., `ssh-keygen -t ecdsa -b 521 -f id_reposense -q -N ""`
1. **Create a deploy key** as follows:
   1. Go to the `settings` page of your publish-RepoSense fork
   1. Click on the `Deploy keys` item in the navigation menu in that page
   1. Click on the `Add deploy key` button and create a new deploy key with the contents of `id_reposense.pub`.
1. **Copy the private key** in base64 encoded format for later use. <br/>
   i.e., `cat id_reposense | base64 -w 0`
