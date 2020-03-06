#!/bin/bash
# Split on "/", ref: http://stackoverflow.com/a/5257398/689223
REPO_SLUG_ARRAY=(${TRAVIS_REPO_SLUG//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
DEPLOY_PATH=./reposense-report

# debugging purposes
echo "Owner: ${REPO_OWNER}"
echo "Repo: ${REPO_NAME}"

DEPLOY_SUBDOMAIN=`echo "${TRAVIS_BRANCH}-branch" | sed -r 's/[\/|\.]+/\-/g'`
DEPLOY_DOMAIN=https://${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ -z "${TRAVIS_TAG// }" ] # if it is a branch
then
  echo "Deploy domain: ${DEPLOY_DOMAIN}"
  surge --project ${DEPLOY_PATH} --domain $DEPLOY_DOMAIN;
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]  # if it is a PR, create comment with link on the PR
then
  # modify github pr description
  GITHUB_PR=https://api.github.com/repos/${TRAVIS_REPO_SLUG}/pulls/${TRAVIS_PULL_REQUEST}
  curl -H "Authorization: token ${GITHUB_API_TOKEN}" -X PATCH \
  -d "{\"body\": \"Travis automatic deployment: ${DEPLOY_DOMAIN}\"}" \
  ${GITHUB_PR_COMMENTS}
  
#  GITHUB_PR_COMMENTS=https://api.github.com/repos/${TRAVIS_REPO_SLUG}/issues/${TRAVIS_PULL_REQUEST}/comments
#  # Post deployment link as comment
#  curl -H "Authorization: token ${GITHUB_API_TOKEN}" -X POST \
#  -d "{\"body\": \"Travis automatic deployment: ${DEPLOY_DOMAIN}\"}" \
#  ${GITHUB_PR_COMMENTS}
fi

#for DEPLOY_SUBDOMAIN_UNFORMATTED in "${DEPLOY_SUBDOMAIN_UNFORMATTED_LIST[@]}"
#do
#  # replaces "/" or "." with "-"
#  # sed -r is only supported in linux, ref http://stackoverflow.com/a/2871217/689223
#  # Domain names follow the RFC1123 spec [a-Z] [0-9] [-]
#  # The length is limited to 253 characters
#  # https://en.wikipedia.org/wiki/Domain_Name_System#Domain_name_syntax
#  DEPLOY_SUBDOMAIN=`echo "${TRAVIS_BRANCH}-branch" | sed -r 's/[\/|\.]+/\-/g'`
#  DEPLOY_DOMAIN=https://${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
#  echo "Deploy domain: ${DEPLOY_DOMAIN}"
#  surge --project ${DEPLOY_PATH} --domain $DEPLOY_DOMAIN;
#  if [ "$TRAVIS_PULL_REQUEST" != "false" ]
#  then
#    # Using the Issues api instead of the PR api
#    # Done so because every PR is an issue, and the issues api allows to post general comments,
#    # while the PR api requires that comments are made to specific files and specific commits
##    GITHUB_PR_COMMENTS=https://api.github.com/repos/${TRAVIS_REPO_SLUG}/issues/${TRAVIS_PULL_REQUEST}/comments
##
##    # Check if there's existing comment for deployment link
##    curl ${GITHUB_PR_COMMENTS} -v
##
##    # Post deployment link as comment
##    curl -H "Authorization: token ${GITHUB_API_TOKEN}" -X POST \
##    -d "{\"body\": \"Travis automatic deployment: ${DEPLOY_DOMAIN}\"}" \
##    ${GITHUB_PR_COMMENTS}
#
#    echo "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/statuses/${TRAVIS_PULL_REQUEST_SHA}?access_token=${GITHUB_API_TOKEN}"
#    curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/statuses/${TRAVIS_PULL_REQUEST_SHA}?access_token=${GITHUB_API_TOKEN}" \
#    -H "Content-Type: application/json" \
#    -X POST \
#    -d "{\"state\": \"success\",\"context\": \"continuous-integration/travis\", \"description\": \"Deploy domain: ${DEPLOY_DOMAIN}\", \"target_url\": \"${DEPLOY_DOMAIN}\"}"
#  fi
#done
