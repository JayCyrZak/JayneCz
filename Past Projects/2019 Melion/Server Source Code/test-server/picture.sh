#!/bin/sh

API_KEY="A"

# get picture list
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/picture/list/global"

# submit a picture
DATA='{"name":"Hallo Welt","description":"test description","latitude":1.00,"longitude":2.00,"profile":true,"image":"SGFsbG8gV2VsdA=="}'
#curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d "$DATA" "https://socialgaming.deine.cloud/api/picture/submit/"

# rate picture
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/picture/rate/4/1337"

# get picture
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/picture/9"
