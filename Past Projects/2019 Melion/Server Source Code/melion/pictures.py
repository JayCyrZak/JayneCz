import melion,base64,time,json,random
from melion import *

@app.route("/picture/list/",methods=["GET"],defaults={"target_id":0})
@app.route("/picture/list/<int:target_id>",methods=["GET"])
def ListPicturesByID(target_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not permitted")
    if target_id == 0:
        target_id = uid
    request.mcursor.execute("SELECT p.id,p.name,p.description,p.profile,SUM(pr.value),COUNT(pr.id) FROM picture p LEFT JOIN picture_rating pr ON (p.id=pr.id_p) WHERE creator=%s GROUP BY p.id",(target_id,))
    rows = request.mcursor.fetchall()
    js_pictures = []
    for row in rows:
        js_pictures.append({
            "id":row[0],
            "name":row[1],
            "description":row[2],
            "profile":row[3],
            "rating":int(row[4])/row[5] if row[5] != 0 else 0,
            "num_ratings":row[5]
        })
    return json.dumps({"status":"ok",
        "pictures":js_pictures})
@app.route("/picture/list/<string:whom>",methods=["GET"])
def ListPicturesByCategory(whom):
    if whom != "global":
        return GetErrorJSON("invalid path")
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not permitted")
    request.mcursor.execute("SELECT p.id,p.name,p.description,p.profile,SUM(pr.value) _sum,COUNT(pr.id),creator FROM picture p LEFT JOIN picture_rating pr ON (p.id=pr.id_p) GROUP BY p.id ORDER BY _sum")
    rows = request.mcursor.fetchall()
    js_pictures = []
    for row in rows:
        js_pictures.append({
            "id":row[0],
            "creator":row[6],
            "name":row[1],
            "description":row[2],
            "profile":row[3],
            "rating":int(row[4])/row[5] if row[5] != 0 else 0,
            "num_ratings":row[5]
        })
    return json.dumps({"status":"ok",
        "pictures":js_pictures})
@app.route("/picture/submit/",methods=["POST"],defaults={"challenge_id":None})
@app.route("/picture/submit/<int:challenge_id>",methods=["POST"])
def PictureSubmit(challenge_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not permitted")
    post = melion.PostToDict()
    filename = defines.UPLOAD_DIRECTORY+str(time.time())+str(random.randint(100000,999999))
    f = open(filename,"wb")
    f.write(base64.b64decode(post["image"]))
    f.close()
    latitude = post["latitude"]
    longitude = post["longitude"]
    name = post["name"]
    profile = post["profile"]
    description = post["description"]
    request.mcursor.execute("INSERT INTO picture (creator,id_challenge,latitude,longitude,time,name,description,path,profile) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)",
            (uid,challenge_id,latitude,longitude,time.time(),name,description,filename,profile))
    request.mcon.commit()
    return """{"status":"ok"}"""
@app.route("/picture/<int:picture_id>")
def GetPicture(picture_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not permitted")
    request.mcursor.execute("SELECT p.creator,p.latitude,p.longitude,p.time,p.id_challenge,p.name,p.description,p.path,SUM(pr.value),COUNT(pr.id) FROM picture p LEFT JOIN picture_rating pr ON (pr.id_p=p.id) WHERE p.id=%s",(picture_id,))
    row = request.mcursor.fetchone()
    f = open(row[7],"rb")
    raw_data = f.read()
    f.close()
    return json.dumps({
            "status":"ok",
            "creator":row[0],
            "latitude":row[1],
            "longitude":row[2],
            "time":row[3],
            "challenge_id":row[4],
            "name":row[5],
            "description":row[6],
            "image":str(base64.b64encode(raw_data)),
            "rating":int(row[8])/row[9] if row[9] != 0 else 0,
            "num_ratings":row[9]
        })
    return GetErrorJSON("not implemented")
@app.route("/picture/rate/<int:picture_id>/<int:rating>")
def PictureRate(picture_id,rating):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("not permitted")
    request.mcursor.execute("INSERT INTO picture_rating (id_p,id_u,value) VALUES (%s,%s,%s)",(picture_id,uid,rating))
    request.mcon.commit()
    return """{"status":"ok"}"""
