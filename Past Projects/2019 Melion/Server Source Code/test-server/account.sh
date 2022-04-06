#!/bin/sh

#API_KEY="A"
API_KEY="7kYSn1GgluKBw07mmSGWVCdTdyBjrg"

# get account
curl -X POST -H "Content-Type: application/json" -d '{"phone-number":133713131371}' https://socialgaming.deine.cloud/api/register

# verify
#curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"sms-key":"1234"}' https://socialgaming.deine.cloud/api/validate

# get status
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/status
