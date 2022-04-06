import melion
from melion import *
from threading import Lock


@app.route("/map/<string:maptype>/<string:latitude>,<string:longitude>;<float:angle>",methods=["GET"])
def GetMap(maptype,latitude,longitude,angle):
    latitude = float(latitude)
    longitude = float(longitude)
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not allowed")
    la_min = latitude - angle
    la_max = latitude + angle
    lo_min = longitude - angle
    lo_max = longitude + angle
    lo_min2 = longitude - angle + 360
    lo_max2 = longitude + angle - 360
    if maptype == "global":
        request.mcursor.execute("SELECT p.latitude,p.longitude,p.size,HEX(p.color),p.id,p.team FROM point p WHERE (p.latitude > %s AND p.latitude < %s) AND (p.longitude > %s OR p.longitude > %s) AND (p.longitude < %s OR p.longitude < %s)",(la_min,la_max,lo_min,lo_min2,lo_max,lo_max2))
    elif maptype == "solo":
        request.mcursor.execute("SELECT p.latitude,p.longitude,p.size,HEX(p.color),p.id,p.team FROM point p WHERE p.creator=%s AND (p.latitude > %s AND p.latitude < %s) AND (p.longitude > %s OR p.longitude > %s) AND (p.longitude < %s OR p.longitude < %s)",(uid,la_min,la_max,lo_min,lo_min2,lo_max,lo_max2))
    elif maptype == "friends":
        request.mcursor.execute("""SELECT p.latitude,p.longitude,p.size,HEX(p.color),p.id,p.team
                FROM point p
                INNER JOIN friends f on (f.status=%s AND (p.creator=f.id_added AND f.id_accepted=%s) OR (p.creator=f.id_accepted AND f.id_added=%s))
                WHERE (p.latitude > %s AND p.latitude < %s) AND (p.longitude > %s OR p.longitude > %s)
                    AND (p.longitude < %s OR p.longitude < %s) 
            UNION ALL
            SELECT p.latitude,p.longitude,p.size,HEX(p.color),p.id,p.team
            FROM point p
            WHERE p.creator=%s AND (p.latitude > %s AND p.latitude < %s)
                AND (p.longitude > %s OR p.longitude > %s) AND (p.longitude < %s OR p.longitude < %s)""",(defines.FriendStatus.Valid,uid,uid,la_min,la_max,lo_min,lo_min2,lo_max,lo_max2,
                    uid,la_min,la_max,lo_min,lo_min2,lo_max,lo_max2))
    else:
        return GetErrorJSON("forbidden")
    res = request.mcursor.fetchall()
    tiles = []
    for row in res:
        tiles.append({"size":row[2],"latitude":row[0],"longitude":row[1],"color":row[3],"id":row[4],"team":row[5]})
    return json.dumps({"status":"ok","tiles":tiles})
mutex = Lock()
g_UserPositions = []
def SetLastPosition(uid,lat,lon):
    mutex.acquire()
    try:
        for user in g_UserPositions:
            if user["id"] == uid:
                user["lat"] = lat
                user["lon"] = lon
                user["time"] = time.time()
                return None
        g_UserPositions.append({'id':uid,
            "lat":lat,
            "lon":lon,
            "time":time.time()
        })
    finally:
        mutex.release()
def GetLastPosition(uid):
    mutex.acquire()
    try:
        for user in g_UserPositions:
            if user["id"] == uid:
                return user["lat"],user["lon"],user["time"]
    finally:
        mutex.release()
    return (0,0,0)
@app.route("/update",methods=["POST"])
@app.route("/map/update",methods=["POST"])
def ClientUpdatePosition():
    try:
        d = melion.PostToDict()
        uid,status = melion.ParseAPIKey()
        lat = d["latitude"]
        lon = d["longitude"]
        clr = d["color"]
        size = d["size"]
        #size = defines.DEFAULT_TILE_SIZE
        if status != defines.UserStatus.Valid:
            raise Exception("invalid user")
        #get friends around
        SetLastPosition(uid,lat,lon)
        min_lat = lat - defines.FRIEND_SQUARE_SIZE
        max_lat = lat + defines.FRIEND_SQUARE_SIZE
        min_lon = lon - defines.FRIEND_SQUARE_SIZE
        max_lon = lon + defines.FRIEND_SQUARE_SIZE
        request.mcursor.execute("SELECT id_added,id_accepted FROM friends WHERE status=%s AND (id_added=%s or id_accepted=%s)",(defines.FriendStatus.Valid,uid,uid))
        rows = request.mcursor.fetchall()
        js_uids_friends = []
        friend_tuples = ()
        for row in rows:
            fid = row[0] if uid==row[1] else row[1]
            flat,flon,ftime = GetLastPosition(fid)
            if flat > min_lat and flat < max_lat and flon > min_lon and flon < max_lon and ftime > time.time()-60:
                js_uids_friends.append(fid)
                friend_tuples += (fid,)

        #size += defines.DEFAULT_TILE_SIZE * len(js_uids_friends)
        request.mcursor.execute("INSERT INTO point (creator,latitude,longitude,size,color) VALUES (%s,%s,%s,%s,UNHEX(%s))",(uid,lat,lon,size,clr))
        if len(friend_tuples) > 0:
            sArgs = ("%s,"*len(friend_tuples))[0:-1]
            request.mcursor.execute("UPDATE friends SET painted_together=painted_together+1 WHERE ((id_added=%s and id_accepted in ("+sArgs+")) OR (id_accepted=%s and id_added in ("+sArgs+"))) AND status=%s",(uid,)+friend_tuples+(uid,)+friend_tuples+(defines.FriendStatus.Valid,))
        request.mcon.commit()
        return json.dumps(
                {
                    "status":"ok",
                    "painting-with":js_uids_friends
                })
    except:
        return GetErrorJSON("update failed")
@app.route("/map/bot-delete/<int:keep>",methods=["GET"])
def MapBotDelete(keep):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return melion.GetErrorJSON("User not valid")
    request.mcursor.execute("SELECT id FROM point WHERE creator=%s ORDER BY id DESC LIMIT %s,1",(uid,keep))
    res = request.mcursor.fetchall()
    if res != []:
        id_max = res[0][0]
        request.mcursor.execute("DELETE FROM point WHERE creator=%s and id<=%s",(uid,id_max))
        request.mcon.commit()
    return """{"status":"ok"}"""
