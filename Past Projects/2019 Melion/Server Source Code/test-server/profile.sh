#!/bin/sh

API_KEY="A"

# get own profile
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/profile/"
echo
echo
# get profile
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/profile/4"
