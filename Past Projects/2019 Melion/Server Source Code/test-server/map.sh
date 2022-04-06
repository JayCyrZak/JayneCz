#!/bin/sh

API_KEY="AAAAAA"

# post a point
curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"longitude":123,"latitude":89,"size":0.001,"color":"FF001100"}' https://socialgaming.deine.cloud/api/update

# get map
MODE="global"
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/map/$MODE/-1.0,0.000000;180.0"

curl -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/map/bot-delete/10"
