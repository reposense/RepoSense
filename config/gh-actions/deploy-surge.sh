#!/bin/bash
# Split on "/", ref: http://stackoverflow.com/a/5257398/689223
REPO_SLUG_ARRAY=(${GITHUB_REPOSITORY//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
DASHBOARD_DEPLOY_PATH=./reposense-report
MARKBIND_DEPLOY_PATH=./docs/_site

# Set to false if unset, ref: http://stackoverflow.com/a/39296583/1320290
ACTION_IS_PULL_REQUEST=${GITHUB_HEAD_REF:-false}

# Get middle section of refs. Branches are "heads" and tags are "tags"
TEMP_REPO_REFS=${GITHUB_REF#*/}
ACTION_BRANCH_TAG_INDICATOR=${TEMP_REPO_REFS%/*}
# Get the actual branch or tag name
ACTION_BRANCH_TAG=${TEMP_REPO_REFS#*/}

DEPLOY_SUBDOMAIN_UNFORMATTED_LIST=()
if [ "$ACTION_IS_PULL_REQUEST" != "false" ]
then
  DEPLOY_SUBDOMAIN_UNFORMATTED_LIST+=(${ACTION_PULL_REQUEST_NUMBER}-pr)
elif [ "$ACTION_BRANCH_TAG_INDICATOR" == "tags" ]
then
  #sorts the tags and picks the latest
  #sort -V does not work on the travis machine
  #sort -V              ref: http://stackoverflow.com/a/14273595/689223
  #sort -t ...          ref: http://stackoverflow.com/a/4495368/689223
  #reverse with sed     ref: http://stackoverflow.com/a/744093/689223
  #git tags | ignore release candidates | sort versions | reverse | pick first line
  LATEST_TAG=$(git tag | grep -v rc | sort -t. -k 1,1n -k 2,2n -k 3,3n -k 4,4n | sed '1!G;h;$!d' | sed -n 1p)
  echo $LATEST_TAG
  if [ "$ACTION_BRANCH_TAG" == "$LATEST_TAG" ]
  then
    DEPLOY_SUBDOMAIN_UNFORMATTED_LIST+=(latest)
  fi
  DEPLOY_SUBDOMAIN_UNFORMATTED_LIST+=(${ACTION_BRANCH_TAG}-tag)
else
  DEPLOY_SUBDOMAIN_UNFORMATTED_LIST+=(${ACTION_BRANCH_TAG}-branch)
fi

for DEPLOY_SUBDOMAIN_UNFORMATTED in "${DEPLOY_SUBDOMAIN_UNFORMATTED_LIST[@]}"
do
  # replaces "/" or "." with "-"
  # sed -r is only supported in linux, ref http://stackoverflow.com/a/2871217/689223
  # Domain names follow the RFC1123 spec [a-Z] [0-9] [-]
  # The length is limited to 253 characters
  # https://en.wikipedia.org/wiki/Domain_Name_System#Domain_name_syntax
  DEPLOY_SUBDOMAIN=$(echo "$DEPLOY_SUBDOMAIN_UNFORMATTED" | sed -r 's/[\/|\.]+/\-/g')

  if [ -z "${DEPLOY_SUBDOMAIN}" ] # empty deploy subdomains, skip deployment
  then
    continue
  fi

  DASHBOARD_DEPLOY_DOMAIN=https://dashboard-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
  echo "Deploy domain: ${DASHBOARD_DEPLOY_DOMAIN}"
  surge --project ${DASHBOARD_DEPLOY_PATH} --domain $DASHBOARD_DEPLOY_DOMAIN;

  MARKBIND_DEPLOY_DOMAIN=https://docs-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
  echo "Deploy domain: ${MARKBIND_DEPLOY_DOMAIN}"
  surge --project ${MARKBIND_DEPLOY_PATH} --domain $MARKBIND_DEPLOY_DOMAIN;

  if [ "$ACTION_IS_PULL_REQUEST" != "false" ] # only create github statuses when it is a PR
  then
    # Create github statuses that redirects users to the deployed dashboard and markbind docs
    curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/statuses/${GITHUB_SHA}?access_token=${GITHUB_TOKEN}" \
    -H "Content-Type: application/json" \
    -X POST \
    -d "{\"state\": \"success\",\"context\": \"dashboard/surge/deploy/${DEPLOY_SUBDOMAIN}\", \"description\": \"Deploy domain: ${DASHBOARD_DEPLOY_DOMAIN}\", \"target_url\": \"${DASHBOARD_DEPLOY_DOMAIN}\"}"

    curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/statuses/${GITHUB_SHA}?access_token=${GITHUB_TOKEN}" \
    -H "Content-Type: application/json" \
    -X POST \
    -d "{\"state\": \"success\",\"context\": \"docs/surge/deploy/${DEPLOY_SUBDOMAIN}\", \"description\": \"Deploy domain: ${MARKBIND_DEPLOY_DOMAIN}\", \"target_url\": \"${MARKBIND_DEPLOY_DOMAIN}\"}"
  fi
done
