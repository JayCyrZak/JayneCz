#!/bin/sh

#API_KEY="PLACE_THE_RECEIVED_API_KEY_HERRE"
#API_KEY="tdyfccf0PcMXEhyAMTKa18q3CBl5wW"

API_KEY="o6BlgzT4ggTAcMnzMKUFSMHL3mAR5Q"
API_KEY="8coG17pPyOMfu3RXl5ZuotmoWQFYp6"
API_KEY="eveKzu8UBdua7LExzL5bE1jlxa7O2v"
API_KEY="IehH8zaxo7SR722Yh3Y0a0ghhZ1lK1"
API_KEY="JjX6alVnjFeXsVZRZXp44KSflnTV5K"
API_KEY="9BY0672gOQp56y5abRMRLGthWGisMq"
API_KEY="WwSOTaQrlCEZls5S4GYo0Akxkd1mhB"
API_KEY="DzQv26AziSqneeGuZVTKoMdlRlfOG6"
API_KEY="SdD9I4CebUo73OiUoSRYHW14rLXsYN"
# get account
#curl -X POST -H "Content-Type: application/json" -d '{"phone-number":133713131371}' https://socialgaming.deine.cloud/api/register

# verify
curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"sms-key":"1234"}' https://socialgaming.deine.cloud/api/validate

# post a point
#curl -X POST -H "melion-key: $API_KEY" -H "Content-Type: application/json" -d '{"longitude":123,"latitude":89,"size":0.001,"color":"FF001100"}' https://socialgaming.deine.cloud/api/update

# get global
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/global


# get status
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/status



# get friends
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/friends/list/

# add friend
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/friends/add/1

# accept friend
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/friends/accept/2

# remove friend
#curl -X GET -H "melion-key: $API_KEY" https://socialgaming.deine.cloud/api/friends/add/1



#profile
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/profile/1"


# get map
MODE="solo"
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/map/$MODE/1.0,0.0000000000;180.0"


#search
#curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/search/name/2"
