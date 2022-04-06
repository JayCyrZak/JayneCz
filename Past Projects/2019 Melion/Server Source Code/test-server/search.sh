#!/bin/sh

API_KEY="A"

SEARCH_NAME="u4"
# search
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/search/name/$SEARCH_NAME"
