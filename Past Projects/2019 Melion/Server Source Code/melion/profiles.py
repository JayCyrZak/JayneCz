import melion,json
from melion import *


@app.route("/profile/",methods=["GET"],defaults={"target_id":0})
@app.route("/profile/<int:target_id>",methods=["GET"])
def GetProfile(target_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not allowed")
    if target_id == 0:
        target_id = uid
    request.mcursor.execute("""SELECT u.id,u.name,u.team,u.phone,u.message,count(f.id),sum(f.painted_together)
                FROM user u
                LEFT JOIN friends f on (u.id=f.id_accepted or u.id=f.id_added) AND f.status="""+str(int(defines.FriendStatus.Valid))+"""
                WHERE u.id=%s AND u.status=%s
                GROUP BY u.id""",(target_id,defines.UserStatus.Valid))
    row = request.mcursor.fetchone()
    request.mcursor.execute("""SELECT painted_together,status,id_added,id_accepted,since FROM friends WHERE (id_added=%s AND id_accepted=%s) OR (id_added=%s AND id_accepted=%s)""",(target_id,uid,uid,target_id))
    frows = request.mcursor.fetchall()
    request.mcursor.execute("""SELECT COUNT(id) FROM achievement_unlocked WHERE id_u=%s""",(target_id,))
    num_achievements = request.mcursor.fetchone()[0]
    request.mcursor.execute("""SELECT COUNT(id) FROM point WHERE creator=%s""",(target_id,))
    num_points = request.mcursor.fetchone()[0]
    frow = (0,0,0,0,0)
    if len(frows) != 0:
        frow = frows[0]
    return json.dumps({"status":"ok",
            "profile":{
                "id":row[0],
                "name":row[1],
                "team":row[2],
                "phone":row[3] if frow[1] == defines.FriendStatus.Valid or target_id==uid else -1,
                "message":row[4],
                "painted-with-friends":row[6],
                "num-friends":row[5],
                "num-achievements":num_achievements,
                "num-points":num_points,
                "painted-together":frow[0],
                "relation":frow[1],
                "since":frow[4],
                "direction": defines.FriendRequestDirection.Incoming if uid == frow[3] else defines.FriendRequestDirection.Outgoing if uid == frow[2] else defines.FriendRequestDirection.Unknown
            }
        })
