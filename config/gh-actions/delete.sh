#!/bin/bash

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

# Function to get deployment ID from Github response
# $1: Response from Github
# $2: Deployment environment name
get_id_from_response() {
  echo "$1" | python3 -c "import sys, json; print(next(filter(lambda j: j['environment']=='${2}', json.load(sys.stdin)))['id'])"
}

# Function to delete GitHub deployment via a cURL command (deployment must be set inactive before it can be deleted)
# $1: The deployment ID
delete_deployment() {
  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments/$1" \
  -X DELETE \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Authorization: token ${GITHUB_TOKEN}"
}

REPO_SLUG_ARRAY=(${GITHUB_REPOSITORY//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
ACTIONS_DASHBOARD_ENV="dashboard-${ACTIONS_PULL_REQUEST_NUMBER}"
ACTIONS_DOCS_ENV="docs-${ACTIONS_PULL_REQUEST_NUMBER}"

# Get deployment data from Github
RES=$(curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments -X GET")

# Extract deployment IDs
DASHBOARD_ID=$(get_id_from_response "$RES" "$ACTIONS_DASHBOARD_ENV")
DOCS_ID=$(get_id_from_response "$RES" "$ACTIONS_DOCS_ENV")

delete_deployment "$DASHBOARD_ID"
delete_deployment "$DOCS_ID"
