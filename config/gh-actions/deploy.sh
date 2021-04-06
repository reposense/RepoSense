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
elif [ "$ACTIONS_STATUS" != "failure" ] && [ "$ACTIONS_STATUS" != "in_progress" ] && [ "$ACTIONS_STATUS" != "pending" ] && [ "$ACTIONS_STATUS" != "success" ]
then
  echo "ERROR: The status supplied is not a valid status"
  echo "Available values: failure, in_progress, pending, success"
  exit 1
fi

if [ "$GITHUB_EVENT_NAME" != "workflow_run" ] && [ "$GITHUB_EVENT_NAME" != "pull_request" ] && [ "$GITHUB_EVENT_NAME" != "pull_request_target" ]
then
  echo "ERROR: This script is intended to be run for either the pull_request, pull_request_target or workflow_run workflows only!"
  exit 1
fi

# Function to create a deployment via a cURL command, then using Python to
# parse the JSON output to obtain the deployment ID.
# $1: Type of deployment to create (can be dashboard or docs)
# $2: Description of the deployment
# Returns the deployment ID that was created.
create_deployment() {
  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments" \
  -H "Content-Type: application/json" \
  -H "Authorization: token ${GITHUB_TOKEN}" \
  -X POST \
  -d "{\"ref\": \"${ACTIONS_PULL_REQUEST_HEAD}\",\"auto_merge\": false, \"required_contexts\": [], \"environment\": \"$1\", \"description\": \"$2\"}" | \
  python3 -c "import sys, json; print(json.load(sys.stdin)['id'])"
}

# Function to update GitHub deployment status via a cURL command
# $1: The deployment ID to update the status for
# $2: Status (can be failure, in_progress or success)
# $3: Description of the deployment status
# $4: Type of deployment (can be dashboard or docs)
# $5: URL for accessing the deployment (optional)
update_deployment() {
  if [ -z "$5" ]
  then
    ACTIONS_ENVIRONMENT_URL=""
  else
    ACTIONS_ENVIRONMENT_URL=$5
  fi

  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments/$1/statuses" \
  -H "Content-Type: application/json" \
  -H "Authorization: token ${GITHUB_TOKEN}" \
  -H "Accept: application/vnd.github.flash-preview+json,application/vnd.github.ant-man-preview+json" \
  -X POST \
  -d "{\"state\": \"$2\",\"description\": \"$3\", \"log_url\": \"${ACTIONS_WORKFLOW_RUN_URL}\", \"environment\": \"$4\", \"environment_url\": \"$5\"}"
}

# Split on "/", ref: http://stackoverflow.com/a/5257398/689223
REPO_SLUG_ARRAY=(${GITHUB_REPOSITORY//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
DASHBOARD_DEPLOY_PATH=./reposense-report
PROJECTIFY_DASHBOARD_DEPLOY_PATH=./new-reposense-report
MARKBIND_DEPLOY_PATH=./docs/_site
ACTIONS_DEPLOY="false"
ACTIONS_WORKFLOW_RUN_URL="$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID"

if [ "$GITHUB_EVENT_NAME" == "workflow_run" ]
then
  ACTIONS_DEPLOY="true"
  ACTIONS_PULL_REQUEST_HEAD=$(cat ./pr/SHA)
  ACTIONS_PULL_REQUEST_NUMBER=$(cat ./pr/NUMBER)
  ACTIONS_DASHBOARD_ID=$(cat ./pr/DASHBOARD_ID)
  ACTIONS_PROJECTIFY_DASHBOARD_ID=$(cat ./pr/PROJECTIFY_DASHBOARD_ID)
  ACTIONS_DOCS_ID=$(cat ./pr/DOCS_ID)
fi

ACTIONS_DASHBOARD_ENV="dashboard-${ACTIONS_PULL_REQUEST_NUMBER}"
ACTIONS_PROJECTIFY_DASHBOARD_ENV="pdashboard-${ACTIONS_PULL_REQUEST_NUMBER}"
ACTIONS_DOCS_ENV="docs-${ACTIONS_PULL_REQUEST_NUMBER}"

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
    update_deployment "${ACTIONS_DASHBOARD_ID}" "failure" "Dashboard deploy failed" "${ACTIONS_DASHBOARD_ENV}" "${ACTIONS_WORKFLOW_RUN_URL}"
    update_deployment "${ACTIONS_PROJECTIFY_DASHBOARD_ID}" "failure" "pDashboard deploy failed" "${ACTIONS_PROJECTIFY_DASHBOARD_ENV}" "${ACTIONS_WORKFLOW_RUN_URL}"
    update_deployment "${ACTIONS_DOCS_ID}" "failure" "Docs deploy failed" "${ACTIONS_DOCS_ENV}" "${ACTIONS_WORKFLOW_RUN_URL}"
  elif [ "$ACTIONS_STATUS" == "in_progress" ]
  then
    # Set GitHub status to in_progress to indicate that deployment is in progress
    update_deployment "${ACTIONS_DASHBOARD_ID}" "in_progress" "Dashboard deployment in progress..." "${ACTIONS_DASHBOARD_ENV}"
    update_deployment "${ACTIONS_PROJECTIFY_DASHBOARD_ID}" "in_progress" "Dashboard deployment in progress..." "${ACTIONS_PROJECTIFY_DASHBOARD_ENV}"
    update_deployment "${ACTIONS_DOCS_ID}" "in_progress" "Docs deployment in progress..." "${ACTIONS_DOCS_ENV}"
  elif [ "$ACTIONS_STATUS" == "pending" ]
  then
    # Set GitHub status to pending so that reviewers know that it is part of the checklist
    ACTIONS_DASHBOARD_ID=$(create_deployment "${ACTIONS_DASHBOARD_ENV}" "RepoSense dashboard preview")
    ACTIONS_PROJECTIFY_DASHBOARD_ID=$(create_deployment "${ACTIONS_PROJECTIFY_DASHBOARD_ENV}" "RepoSense pdashboard preview")
    ACTIONS_DOCS_ID=$(create_deployment "${ACTIONS_DOCS_ENV}" "RepoSense documentation preview")

    echo "$ACTIONS_DASHBOARD_ID" > ./pr/DASHBOARD_ID
    echo "$ACTIONS_PROJECTIFY_DASHBOARD_ID" > ./pr/PROJECTIFY_DASHBOARD_ID
    echo "$ACTIONS_DOCS_ID" > ./pr/DOCS_ID
  elif [ "$ACTIONS_STATUS" == "success" ] && [ "$ACTIONS_DEPLOY" == "true" ]
  then
    DASHBOARD_DEPLOY_DOMAIN=https://dashboard-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
    echo "Deploy domain: ${DASHBOARD_DEPLOY_DOMAIN}"
    surge --project ${DASHBOARD_DEPLOY_PATH} --domain $DASHBOARD_DEPLOY_DOMAIN;

    MARKBIND_DEPLOY_DOMAIN=https://docs-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
    echo "Deploy domain: ${MARKBIND_DEPLOY_DOMAIN}"
    surge --project ${MARKBIND_DEPLOY_PATH} --domain $MARKBIND_DEPLOY_DOMAIN;

    PROJECTIFY_DASHBOARD_DEPLOY_DOMAIN=https://pdashboard-${DEPLOY_SUBDOMAIN}-${REPO_NAME}-${REPO_OWNER}.surge.sh
    echo "Deploy domain: ${PROJECTIFY_DASHBOARD_DEPLOY_DOMAIN}"
    surge --project ${PROJECTIFY_DASHBOARD_DEPLOY_PATH} --domain $PROJECTIFY_DASHBOARD_DEPLOY_DOMAIN;

    # Create github statuses that redirects users to the deployed dashboard and markbind docs
    update_deployment "${ACTIONS_DASHBOARD_ID}" "success" "Deploy domain: ${DASHBOARD_DEPLOY_DOMAIN}" "${ACTIONS_DASHBOARD_ENV}" "${DASHBOARD_DEPLOY_DOMAIN}"
    update_deployment "${ACTIONS_PROJECTIFY_DASHBOARD_ID}" "success" "Deploy domain: ${PROJECTIFY_DASHBOARD_DEPLOY_DOMAIN}" "${ACTIONS_PROJECTIFY_DASHBOARD_ENV}" "${PROJECTIFY_DASHBOARD_DEPLOY_DOMAIN}"
    update_deployment "${ACTIONS_DOCS_ID}" "success" "Deploy domain: ${MARKBIND_DEPLOY_DOMAIN}" "${ACTIONS_DOCS_ENV}" "${MARKBIND_DEPLOY_DOMAIN}"
  fi
done
