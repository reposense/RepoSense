## Using RepoSense with Netlify

[Netlify](https://www.netlify.com) enables you to run RepoSense with your configured files without downloading the latest jar file.

1. Fork the RepoSense repository using this [link](https://github.com/repoSense/RepoSense/fork).
1. Set up Netlify for your forked repository. We recommend this [site](https://www.netlify.com/blog/2016/09/29/a-step-by-step-guide-deploying-on-netlify/) for ease of setup. For the `Build Command` in **Step 5** use:
`./gradlew run -Dargs="--config ./ --output output_path/  --formats java adoc js"` and for `Publishing Directory` in **Step 5** use `/output_path/reposense-report`
1. Click on **Settings** in the top, choose **Build & deploy** from the right panel and scroll to **Build hooks**.
![Build hooks](images/using-netlify-build-hooks.png)
1. Click **Add build hook**, give your webhook a name, and choose the `master` branch to build. 
1. Go to your target repository (the repository you want to analyze) and click on **Settings**.
1. Select **Webhooks** on left panel and click on **Add webhook**. 
![Add webhook](images/using-netlify-add-hook.png)
1. Copy the Netlify URL and paste it in the URL form field.
![Webhook url](images/using-netlify-url.png)
1. Select **application.json** as content type
1. Select **Let me select individual events** and based on your requirements check the checkboxes.
1. Leave the **Active** checkbox checked.
1. Click on **Add webhook** to save the webhook and add it.

> This should cause Netlify to deploy your site each and every time based on your checked checkboxes.

**Note:** After setting up, you just need to make sure your forked repository is same as upstream every time.


