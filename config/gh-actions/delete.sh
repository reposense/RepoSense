#!/bin/bash
# This script automatically deletes RepoSense and documentation deployments on closed PRs
# This is intended to be run for the pull_request workflow

# Set to false if unset, ref: http://stackoverflow.com/a/39296583/1320290
CI=${CI:-false}

if [ "$CI" == "false" ]
then
  echo "ERROR: This script is intended to be run on GitHub Actions only!"
  exit 1
fi

if [ "$GITHUB_EVENT_NAME" != "pull_request" ]
then
  echo "ERROR: This script is intended to be run for pull_request workflows only!"
  exit 1
fi

REPO_SLUG_ARRAY=(${GITHUB_REPOSITORY//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
ACTIONS_DASHBOARD_ENV="dashboard-${ACTIONS_PULL_REQUEST_NUMBER}"
ACTIONS_DOCS_ENV="docs-${ACTIONS_PULL_REQUEST_NUMBER}"

# Function to get deployment ID for environment name from Github response
# $1: Response from Github
# $2: Deployment environment name
get_ids_from_response() {
  echo "$1" | python3 -c "import sys, json; \
    print(' '.join( \
      map(lambda j: str(j['id']), \
          filter(lambda j: j['environment']=='${2}', \
                 json.load(sys.stdin) \
                 ) \
          )))"
}

# Function to delete GitHub deployment via a cURL command
# NOTE: deployment must be set inactive before it can be deleted
# $1: The deployment ID
delete_deployment() {
  echo "Deleting Deployment: ${1}"
  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments/$1" \
  -X DELETE \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Authorization: token ${GITHUB_TOKEN}"
}

# Function to update GitHub deployment status via a cURL command
# $1: The deployment ID to update the status for
mark_deployment_inactive() {
  echo "Marking Deployment Inactive: ${1}"
  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments/$1/statuses" \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Authorization: token ${GITHUB_TOKEN}" \
  -d "{\"state\": \"inactive\"}"
}

# Function to get deployment data about repo via a cURL command
get_deployment_data() {
  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments" \
  -X GET \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Authorization: token ${GITHUB_TOKEN}"
}

# Function to mark inactive and delete deployments for given list
# of deployment IDs
# $1 List of deployment IDs
delete_all_deployments() {
  for ID in "$@"
  do
    if [ -z "${ID}" ] # empty deploy subdomains, skip deployment
    then
      continue
    fi
    mark_deployment_inactive "$ID"
    delete_deployment "$ID"
  done
}

# Get deployment data from Github
RES=$(get_deployment_data)

# Extract deployment IDs
DASHBOARD_IDS=($(get_ids_from_response "$RES" "$ACTIONS_DASHBOARD_ENV"))
DOCS_IDS=($(get_ids_from_response "$RES" "$ACTIONS_DOCS_ENV"))

delete_all_deployments "${DASHBOARD_IDS[@]}"
delete_all_deployments "${DOCS_IDS[@]}"
