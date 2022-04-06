from melion import *
import melion,json,time

@app.route("/friends/list/",methods=["GET"],defaults={"target_id":0})
@app.route("/friends/list/<int:target_id>",methods=["GET"])
def FriendsList(target_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    if target_id == 0:
        target_id = uid
    request.mcursor.execute("""SELECT u.id,u.name,u.team,u.phone,f.painted_together,f.status,f.since,f.id_added,f.id_accepted,u.message
            FROM user u
            INNER JOIN friends f on (u.id=f.id_accepted or u.id=f.id_added)
            WHERE (f.id_added=%s OR f.id_accepted=%s) AND u.id!=%s AND u.status=%s AND f.status!=%s""",
            (target_id,target_id,target_id,defines.UserStatus.Valid,defines.FriendStatus.Rejected))
    res = request.mcursor.fetchall()
    friends = []
    for row in res:
        if row[5] != defines.FriendStatus.Valid and uid != target_id:
            continue
        friends.append({"id":row[0],
            "name":        row[1],
            "team":        row[2],
            "message":     row[9],
            "phone":    row[3] if uid == target_id else -1,
            "painted-together":    row[4],
            "relation":    row[5],
            "since":    row[6],
            # Auf Wunsch: 0 -> "NONE", 1 -> "INC", 2 -> "OUT"
            "direction": defines.FriendRequestDirection.Incoming if uid == row[8] else defines.FriendRequestDirection.Outgoing if uid == row[7] else defines.FriendRequestDirection.Unknown,
        })
    return json.dumps({"status":"ok","friends":friends})
def GetFriendStatus(id1,id2):
    request.mcursor.execute("SELECT id_added,id_accepted,status FROM friends WHERE ((id_added=%s AND id_accepted=%s) OR (id_added=%s AND id_accepted=%s)) AND status!=%s",(id1,id2,id2,id1,defines.FriendStatus.Rejected))
    rows = request.mcursor.fetchall()
    if len(rows) > 1:
        melion.LogError("FRIENDS: duplicate entries for "+str(id1)+","+str(id2))

    for row in rows:
        if row[0] == id1:
            return (defines.FriendRequestDirection.Outgoing,row[2])
        else:
            return (defines.FriendRequestDirection.Incoming,row[2])
    return None
@app.route("/friends/add/<int:target_id>",methods=["GET"])
def FriendsAdd(target_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    fs = GetFriendStatus(uid,target_id)
    if fs != None:
        return melion.GetErrorJSON("already in some relation")
    request.mcursor.execute("INSERT INTO friends (id_added,id_accepted,status) VALUES (%s,%s,%s)",(uid,target_id,defines.FriendStatus.Pending))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        return melion.GetErrorJSON("add failed")
    return """{\"status\":\"ok\"}"""
@app.route("/friends/accept/<int:target_id>",methods=["GET"])
def FriendsAccept(target_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    fs = GetFriendStatus(uid,target_id)
    if fs == None:
        return melion.GetErrorJSON("not in any relation")
    elif fs[0] != defines.FriendRequestDirection.Incoming or fs[1] != defines.FriendStatus.Pending:
        return melion.GetErrorJSON("no pending request")
    request.mcursor.execute("UPDATE friends SET status=%s,since=%s WHERE id_added=%s AND id_accepted=%s AND status=%s",(defines.FriendStatus.Valid,time.time(),target_id,uid,defines.FriendStatus.Pending))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        return GetErrorJSON("acceot failed")
    return """{\"status\":\"ok\"}"""
@app.route("/friends/remove/<int:target_id>",methods=["GET"])
def FriendsRemove(target_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    fs = GetFriendStatus(uid,target_id)
    if fs == None:
        return melion.GetErrorJSON("not in any relation")
    request.mcursor.execute("UPDATE friends SET status=%s WHERE ((id_added=%s AND id_accepted=%s) OR (id_added=%s AND id_accepted=%s))",(defines.FriendStatus.Rejected,target_id,uid,uid,target_id))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        return GetErrorJSON("remove failed")
    return """{\"status\":\"ok\"}"""
