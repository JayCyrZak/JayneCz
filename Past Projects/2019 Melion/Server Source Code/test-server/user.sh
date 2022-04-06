#!/bin/sh

API_KEY="A"

# change name
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/config/name/HalloWet"
# change team
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/config/team/2"
# change message
curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"msg":"Hallo2 Welt"}' "https://socialgaming.deine.cloud/api/config/message"
