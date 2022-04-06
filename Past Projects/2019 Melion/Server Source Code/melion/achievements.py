from melion import *
import melion
import json
import time
import defines

# personal distance walked
@app.route("/achievement/distance", methods=["GET"])
def distance():
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("SELECT distance FROM user WHERE id=%s", (uid,))
    row = request.mcursor.fetchone()
    result = "{\"status\": \"ok\",\"distance\":" + str(row[0]) + "}"
    return result

# amount of friends
@app.route("/achievement/amountoffriends", methods=["GET"])
def amount_of_friends():
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("SELECT COUNT(id) FROM friends WHERE id=%s", (uid,))
    row = request.mcursor.fetchone()
    result = "{\"status\": \"ok\",\"friends\":" + str(row[0]) + "}"
    return result


# longest distance painted with a friend
@app.route("/achievement/mostpaintedtoghether", methods=["GET"])
def friends_painted_together():
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("SELECT MAX(painted_together) FROM friends WHERE id_added=%s OR id_accepted=%s", (uid, uid))
    row = request.mcursor.fetchone()
    result = "{\"status\": \"ok\",\"painted_together\":" + str(row[0]) + "}"
    return result

# returns earliest friend in the fl
@app.route("/achievement/longestfriend", methods=["GET"])
def longest_friends():
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("SELECT MIN(f.since) FROM friends f WHERE f.id_added=%s OR f.id_accepted = %s", (uid, uid))
    row = request.mcursor.fetchone()
    result = "{\"status\": \"ok\",\"longest_friend\":" + str(row[0]) + "}"
    return result

# returns unlocked achievements
@app.route("/achievement/unlockedachievements", methods=["GET"])
def unlocked_achievements():
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("SELECT a.id,a.name,a.description,a.value,a.objective,a.objectiveValue FROM achievement_unlocked au INNER JOIN achievement a ON au.id_a = a.id WHERE id_u=%s", (uid,))
    request_result = request.mcursor.fetchall()
    achiev = []
    for row in request_result:
        achiev.append({"id": row[0], "name": row[1], "description": row[2], "value": row[3], "objective": row[4],
                       "objectiveValue": row[5]})
    return json.dumps({"status": "ok", "achievements": achiev})

# insert accomplished achievement
@app.route("/achievement/unlock/<int:unlock_id>", methods=["POST"])
def unlock_achievement(unlock_id):
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("INSERT INTO achievement_unlocked (id_u,id_a,time)"
                            "VALUES (%s,%s,%s)", (uid, unlock_id, time.time()))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        return GetErrorJSON("unlock failed")
    return json.dumps({"status": "ok"})


@app.route("/achievement/allachievements", methods=["GET"])
def all_achievements():
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    request.mcursor.execute("SELECT id,name,description,value,objective,objectiveValue FROM achievement")
    request_result = request.mcursor.fetchall()
    achiev = []
    for row in request_result:
        achiev.append({"id": row[0], "name": row[1], "description": row[2], "value": row[3], "objective": row[4], "objectiveValue": row[5]})
    return json.dumps({"status": "ok", "achievements": achiev})
