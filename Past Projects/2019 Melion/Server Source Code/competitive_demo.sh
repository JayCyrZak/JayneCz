#!/bin/sh

curl -X GET -H "melion-key: A" https://socialgaming.deine.cloud/api/competitive/create/10,0
curl -X GET -H "melion-key: AAA" https://socialgaming.deine.cloud/api/competitive/create/20,20

curl -X GET -H "melion-key: AAAA" https://socialgaming.deine.cloud/api/competitive/search

curl -X GET -H "melion-key: AA" https://socialgaming.deine.cloud/api/competitive/join/1
curl -X GET -H "melion-key: AAAA" https://socialgaming.deine.cloud/api/competitive/join/3

curl -X GET -H "melion-key: A" https://socialgaming.deine.cloud/api/competitive/start
curl -X GET -H "melion-key: AAA" https://socialgaming.deine.cloud/api/competitive/start


curl -X GET -H "melion-key: A" https://socialgaming.deine.cloud/api/competitive/update/5,5
curl -X GET -H "melion-key: AA" https://socialgaming.deine.cloud/api/competitive/update/5,6

curl -X GET -H "melion-key: AAA" https://socialgaming.deine.cloud/api/competitive/update/4,4
sleep 1

curl -X GET -H "melion-key: A" https://socialgaming.deine.cloud/api/competitive/update/5,7
curl -X GET -H "melion-key: AAAA" https://socialgaming.deine.cloud/api/competitive/update/16,16
curl -X GET -H "melion-key: AA" https://socialgaming.deine.cloud/api/competitive/update/5,8

sleep 1

curl -X GET -H "melion-key: AAA" https://socialgaming.deine.cloud/api/competitive/update/5,4
curl -X GET -H "melion-key: A" https://socialgaming.deine.cloud/api/competitive/update/5,9
curl -X GET -H "melion-key: AA" https://socialgaming.deine.cloud/api/competitive/update/5,10

sleep 1

curl -X GET -H "melion-key: AAAA" https://socialgaming.deine.cloud/api/competitive/update/16,16
curl -X GET -H "melion-key: AAAA" https://socialgaming.deine.cloud/api/competitive/update/15,16

sleep 5

curl -X GET -H "melion-key: A" https://socialgaming.deine.cloud/api/competitive/end
curl -X GET -H "melion-key: AA" https://socialgaming.deine.cloud/api/competitive/end
curl -X GET -H "melion-key: AAA" https://socialgaming.deine.cloud/api/competitive/end
curl -X GET -H "melion-key: AAAA" https://socialgaming.deine.cloud/api/competitive/end