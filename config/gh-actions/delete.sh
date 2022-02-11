#!/bin/bash

CI=${CI:-false}

REPO_SLUG_ARRAY=(${GITHUB_REPOSITORY//\// })
REPO_OWNER=${REPO_SLUG_ARRAY[0]}
REPO_NAME=${REPO_SLUG_ARRAY[1]}
ACTIONS_DASHBOARD_ENV="dashboard-${ACTIONS_PULL_REQUEST_NUMBER}"
ACTIONS_DOCS_ENV="docs-${ACTIONS_PULL_REQUEST_NUMBER}"

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

get_deployment_data() {
  curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments" \
  -X GET \
  -H "Authorization: token ${GITHUB_TOKEN}" \
  -H "Accept: application/vnd.github.flash-preview+json,application/vnd.github.ant-man-preview+json,application/vnd.github.v3+json"
}

echo $REPO_OWNER
echo $REPO_NAME
echo $ACTIONS_DASHBOARD_ENV
echo $ACTIONS_DOCS_ENV

echo "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments"

# Get deployment data from Github
# RES=$(curl "https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/deployments -X GET")

echo $RES

# Extract deployment IDs
DASHBOARD_ID=$(get_id_from_response "$RES" "$ACTIONS_DASHBOARD_ENV")
DOCS_ID=$(get_id_from_response "$RES" "$ACTIONS_DOCS_ENV")

delete_deployment "$DASHBOARD_ID"
delete_deployment "$DOCS_ID"
