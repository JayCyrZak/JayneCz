#!/bin/sh

API_KEY="A"

# get own friends list
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/"
echo
# get friends list
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/"

TARGET_UID=4
# add
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/add/$TARGET_UID"
#API_KEY="AAAA"
#TARGET_UID=1
# accept
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/accept/$TARGET_UID"
# remove
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/remove/$TARGET_UID"
