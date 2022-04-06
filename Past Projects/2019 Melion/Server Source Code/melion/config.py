from melion import *
import melion


@app.route("/config/name/<string:name>",methods=["GET"])
def ConfigUsername(name):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    request.mcursor.execute("UPDATE user SET name=%s WHERE id=%s",(name,uid))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        raise Exception("fatal query")
    return """{\"status\":\"ok\"}"""
@app.route("/config/team/<int:teamid>",methods=["GET"])
def ConfigTeam(teamid):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    request.mcursor.execute("UPDATE user SET team=%s WHERE id=%s",(teamid,uid))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        raise Exception("fatal query")
    return """{\"status\":\"ok\"}"""
@app.route("/config/message",methods=["POST"])
def ConfigMessage():
    uid,status = melion.ParseAPIKey()
    d = melion.PostToDict()
    msg = d["msg"]
    if status != defines.UserStatus.Valid:
        return GetErrorJSON("invalid")
    request.mcursor.execute("UPDATE user SET message=%s WHERE id=%s",(msg,uid))
    request.mcon.commit()
    if request.mcursor.rowcount < 1:
        raise Exception("fatal query")
    return """{\"status\":\"ok\"}"""
