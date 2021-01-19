#!/bin/bash
# This script automatically deploys RepoSense and documentation to surge.sh
# This is intended to be run for pull_request and workflow_run workflows

# Set to false if unset, ref: http://stackoverflow.com/a/39296583/1320290
CI=${CI:-false}
ACTIONS_STATUS=${1:-false}

if [ "$CI" == "false" ]
then
  echo "ERROR: This script is intended to be run on GitHub Actions only!"
  exit 1
fi

if [ "$ACTIONS_STATUS" == "false" ]
then
  echo "ERROR: This script requires a status supplied as the first parameter"
  echo "Available values: failure, pending, success"
  exit 1
elif [ "$ACTIONS_STATUS" != "failure" ] && [ "$ACTIONS_STATUS" != "pending" ] && [ "$ACTIONS_STATUS" != "success" ]
then
  echo "ERROR: The status supplied is not a valid status"
  echo "Available values: failure, pending, success"
  exit 1
fi

if [ "$GITHUB_EVENT_NAME" != "workflow_run" ] && [ "$GITHUB_EVENT_NAME" != "pull_request" ]
then
  echo "ERROR: This script is intended to be run for either the pull_request or workflow_run workflows only!"
  exit 1
fi

# Function to update GitHub commit status via a cURL command
# $1: Type of status to update (can be dashboard or docs)
# $2: Status (can be failure, pending or success)
# $3: Description
# $4: Target URL
update_status() {
  ACTIONS_STATUS_CONTEXT="$1/surge/deploy/${DEPLOY_SUBDOMAIN}"

  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/statuses/${ACTIONS_PULL_REQUEST_HEAD}?access_token=${GITHUB_TOKEN}" \
  -H "Content-Type: application/json" \
  -X POST \
  -d "{\"state\": \"$2\",\"context\": \"${ACTIONS_STATUS_CONTEXT}\", \"description\": \"$3\", \"target_url\": \"$4\"}"
}

# Split on "/", ref: http://stackoverflow.com/a/5257398/689223
REPO_SLUG_ARRAY=(${GITHUB_REPOSITORY//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
DASHBOARD_DEPLOY_PATH=./reposense-report
MARKBIND_DEPLOY_PATH=./docs/_site
ACTIONS_DEPLOY="false"
ACTIONS_WORKFLOW_RUN_URL="$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID"

if [ "$GITHUB_EVENT_NAME" == "workflow_run" ]
then
  ACTIONS_DEPLOY="true"
  ACTIONS_PULL_REQUEST_HEAD=$(cat ./pr/SHA)
  ACTIONS_PULL_REQUEST_NUMBER=$(cat ./pr/NUMBER)
fi

DEPLOY_SUBDOMAIN_UNFORMATTED_LIST=()
DEPLOY_SUBDOMAIN_UNFORMATTED_LIST+=(${ACTIONS_PULL_REQUEST_NUMBER}-pr)

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

  if [ "$ACTIONS_STATUS" == "failure" ]
  then
    # Update GitHub status to failed
    update_status "dashboard" "failure" "Dashboard deploy failed" "${ACTIONS_WORKFLOW_RUN_URL}"
    update_status "docs" "failure" "Docs deploy failed" "${ACTIONS_WORKFLOW_RUN_URL}"
  elif [ "$ACTIONS_STATUS" == "pending" ]
  then
    # Set GitHub status to pending so that reviewers know that it is part of the checklist
    update_status "dashboard" "pending" "Dashboard deployment in progress..." "${ACTIONS_WORKFLOW_RUN_URL}"
    update_status "docs" "pending" "Docs deployment in progress..." "${ACTIONS_WORKFLOW_RUN_URL}"
  elif [ "$ACTIONS_STATUS" == "success" ] && [ "$ACTIONS_DEPLOY" == "true" ]
  then
    DASHBOARD_DEPLOY_DOMAIN=https://dashboard-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
    echo "Deploy domain: ${DASHBOARD_DEPLOY_DOMAIN}"
    surge --project ${DASHBOARD_DEPLOY_PATH} --domain $DASHBOARD_DEPLOY_DOMAIN;

    MARKBIND_DEPLOY_DOMAIN=https://docs-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
    echo "Deploy domain: ${MARKBIND_DEPLOY_DOMAIN}"
    surge --project ${MARKBIND_DEPLOY_PATH} --domain $MARKBIND_DEPLOY_DOMAIN;

    # Create github statuses that redirects users to the deployed dashboard and markbind docs
    update_status "dashboard" "success" "Deploy domain: ${DASHBOARD_DEPLOY_DOMAIN}" "${DASHBOARD_DEPLOY_DOMAIN}"
    update_status "docs" "success" "Deploy domain: ${MARKBIND_DEPLOY_DOMAIN}" "${MARKBIND_DEPLOY_DOMAIN}"
  fi
done
