import melion
from melion import *


@app.route("/search/name/<string:target_name>",methods=["GET"])
def SearchUser(target_name):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not allowed")
    request.mcursor.execute("""SELECT u.id,u.name,u.team,u.phone,f.painted_together,f.status,f.since,f.id_added,f.id_accepted,u.message
                FROM user u
                LEFT JOIN friends f on (u.id=f.id_accepted and f.id_added=%s) or (f.id_accepted=%s AND u.id=f.id_added)
                WHERE u.status=%s AND u.name LIKE %s""",
                (uid,uid,int(defines.UserStatus.Valid),"%"+target_name+"%"))
    rows = request.mcursor.fetchall()
    users = []
    for row in rows:
        users.append({"id":row[0],
            "name":row[1],
            "team":row[2],
            #"phone":row[3] if row[5]==defines.FriendStatus.Valid or target_id==uid else -1,
            "message":row[9],
            "painted-together":row[4],
            "relation":row[5],
            "since":row[6],
            # Auf Wunsch: 0 -> "NONE", 2 -> "INC", 3 -> "OUT"
            "direction": defines.FriendRequestDirection.Incoming if uid == row[8] else defines.FriendRequestDirection.Outgoing if uid == row[7] else defines.FriendRequestDirection.Unknown
        })
    return json.dumps({"status":"ok","users":users})
