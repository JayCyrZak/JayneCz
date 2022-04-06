from melion import *
import melion
import json
import defines


@app.route("/leaderboards/<string:range>/distance/", methods=["GET"])
def leaderboard_distance(range):
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    if range == "solo":
        request.mcursor.execute("SELECT id,name,distance FROM user ORDER BY distance DESC")
    if range == "global":
        request.mcursor.execute("SELECT id,name,distance FROM user ORDER BY distance DESC")
    if range == "friends":
        request.mcursor.execute(
            """SELECT r.id, r.name, r.distance
            FROM user u INNER JOIN friends f ON (f.id_accepted=u.id) OR (f.id_added=u.id) INNER JOIN 
            (SELECT id,name,distance 
            FROM user 
            ORDER BY distance DESC) r ON (f.id_accepted=r.id) OR (f.id_added=r.id)
            WHERE u.status=%s AND f.status=%s 
            AND u.id = %s AND (u.id=%s XOR r.id=%s)
            ORDER BY r.distance DESC""",
            (defines.UserStatus.Valid,defines.FriendStatus.Valid,uid,uid,uid))
    rank = 0
    leaderboard = []
    request_result = request.mcursor.fetchall()
    if range == "solo":
        for row in request_result:
            rank += 1
            if uid == row[0]:
                leaderboard.append({"rank": rank, "name": row[1],"points": row[2]})
    else:
        for row in request_result:
            rank += 1
            leaderboard.append({"rank": rank, "name": row[1],"points": row[2]})
    return json.dumps({"status": "ok", "user_ranks": leaderboard})


@app.route("/leaderboards/<string:range>/paintedtogether/", methods=["GET"])
def leaderboard_painted_together(range):
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    if range == "solo":
        request.mcursor.execute(
            """SELECT u.id,u.name,SUM(f.painted_together) AS points 
            FROM user u 
            INNER JOIN friends f ON (f.id_accepted=u.id) OR (f.id_added=u.id) 
            WHERE u.status=%s AND f.status=%s
            GROUP BY u.id 
            ORDER BY points DESC""",(defines.UserStatus.Valid,defines.FriendStatus.Valid,))
    if range == "global":
        request.mcursor.execute(
            """SELECT u.id,u.name,SUM(f.painted_together) AS points 
            FROM user u 
            INNER JOIN friends f ON (f.id_accepted=u.id) OR (f.id_added=u.id) 
            WHERE u.status=%s AND f.status=%s
            GROUP BY u.id 
            ORDER BY points DESC""",(defines.UserStatus.Valid,defines.FriendStatus.Valid,))
    if range == "friends":
        request.mcursor.execute(
            """SELECT r.id, r.name, r.painted
            FROM user u INNER JOIN friends f ON (f.id_accepted=u.id) OR (f.id_added=u.id) INNER JOIN 
            (SELECT u.id,u.name,SUM(f.painted_together) AS painted
            FROM user u INNER JOIN friends f ON (f.id_accepted=u.id) OR (f.id_added=u.id) 
            WHERE u.status=%s AND f.status=%s 
            GROUP BY u.id
            ORDER BY painted DESC) r ON (f.id_accepted=r.id) OR (f.id_added=r.id)
            WHERE u.id = %s AND (u.id = %s XOR r.id=%s)
            ORDER BY r.painted DESC""",
            (defines.UserStatus.Valid, defines.FriendStatus.Valid,uid,uid,uid))

    rank = 0
    leaderboard = []
    request_result = request.mcursor.fetchall()
    if range == "solo":
            for row in request_result:
                rank += 1
                if uid == row[0]:
                    leaderboard.append({"rank": rank, "name": row[1], "points": row[2]})
    else:
        for row in request_result:
            rank += 1
            leaderboard.append({"rank": rank, "name": row[1], "points": row[2]})
    return json.dumps({"status": "ok", "user_ranks": leaderboard})

@app.route("/leaderboards/<string:range>/achievements/", methods=["GET"])
def achievements_distance(range):
    uid, status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid apikey")
    if range == "solo":
        request.mcursor.execute("SELECT u.id,u.name,COUNT(a.id) AS amt FROM user u INNER JOIN achievement_unlocked a ON u.id = a.id_u GROUP BY u.id ORDER BY amt DESC")
    if range == "global":
        request.mcursor.execute("SELECT u.id,u.name,COUNT(a.id) AS amt FROM user u INNER JOIN achievement_unlocked a ON u.id = a.id_u GROUP BY u.id ORDER BY amt DESC")
    if range == "friends":
        request.mcursor.execute(
            """SELECT r.id, r.name, r.amt
            FROM user u INNER JOIN friends f ON (f.id_accepted=u.id) OR (f.id_added=u.id) INNER JOIN 
            (SELECT u.id,u.name,COUNT(a.id) AS amt 
            FROM user u 
            INNER JOIN achievement_unlocked a ON u.id = a.id_u 
            GROUP BY u.id ORDER BY amt DESC) r ON (f.id_accepted=r.id) OR (f.id_added=r.id)
            WHERE u.id = %s AND (u.id = %s XOR r.id= %s)
            AND u.status=%s AND f.status=%s 
            ORDER BY r.amt DESC""", (uid, uid,uid,defines.UserStatus.Valid, defines.FriendStatus.Valid))
    rank = 0
    leaderboard = []
    request_result = request.mcursor.fetchall()
    if range == "solo":
        for row in request_result:
            rank += 1
            if uid == row[0]:
                leaderboard.append({"rank": rank, "name": row[1], "points": row[2]})
    else:
        for row in request_result:
            rank += 1
            leaderboard.append({"rank": rank, "name": row[1],"points": row[2]})
    return json.dumps({"status": "ok", "user_ranks": leaderboard})

