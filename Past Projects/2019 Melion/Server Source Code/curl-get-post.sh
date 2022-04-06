#!/bin/sh

API_KEY="PLACE_THE_RECEIVED_API_KEY_HERRE"

# get account
#curl -X POST -H "Content-Type: application/json" -d '{"phone-number":133713131371}' https://socialgaming.deine.cloud/api/register

# verify
#curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"sms-key":"1234"}' https://socialgaming.deine.cloud/api/validate

# post a point
#curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"longitude":123,"latitude":89,"size":0.001,"color":"FF001100"}' https://socialgaming.deine.cloud/api/update

# get global
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/global
