#!/bin/sh



API_KEY="A"
TARGET_UID=4

# make sure not friends with $TARGET_UID
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/remove/$TARGET_UID" 2>&1 > /dev/null

# verify not friends
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/" 2>&1 | grep "\"id\": $TARGET_UID," 2>&1 > /dev/null
if [ "$?" -eq "0" ]; then
	echo "Friendship cleanup failed"
	exit 1
fi

# add
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/add/$TARGET_UID" 2>&1 | grep "{\"status\":\"ok\"}" 2>&1 > /dev/null
if [ "$?" -ne "0" ]; then
	echo "add failed"
	exit 1
fi

# verify entry in friends list
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/" 2>&1 | grep "\"id\": $TARGET_UID," | grep "\"relation\": 1" | grep "\"direction\": 2" 2>&1 > /dev/null
if [ "$?" -ne "0" ]; then
	echo "not in friends list target=$TARGET_UID [PENDING]"
	exit 1
fi

# switch ids
API_KEY="AAAA"
TARGET_UID=1


# verify entry in friends list
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/" 2>&1 | grep "\"id\": $TARGET_UID," | grep "\"relation\": 1" | grep "\"direction\": 1" 2>&1 > /dev/null
if [ "$?" -ne "0" ]; then
	echo "not in friends list target=$TARGET_UID [check request]"
	exit 1
fi

# accept
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/accept/$TARGET_UID" 2>&1 | grep "{\"status\":\"ok\"}" 2>&1 > /dev/null
if [ "$?" -ne "0" ]; then
	echo "accept failed"
	exit 1
fi


# verify entry in friends list
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/" 2>&1 | grep "\"id\": $TARGET_UID," | grep "\"relation\": 3" 2>&1 > /dev/null
if [ "$?" -ne "0" ]; then
	echo "not in friends list target=$TARGET_UID [check added]"
	exit 1
fi



#switch back
API_KEY="A"
TARGET_UID=4
# verify entry in friends list
curl -X GET -H "melion-key: $API_KEY" "https://socialgaming.deine.cloud/api/friends/list/" 2>&1 | grep "\"id\": $TARGET_UID," | grep "\"relation\": 3" 2>&1 > /dev/null
if [ "$?" -ne "0" ]; then
	echo "not in friends list target=$TARGET_UID FINAL"
	exit 1
fi

echo "success"
