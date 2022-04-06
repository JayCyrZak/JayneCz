import melion,json
from melion import *
from threading import Lock

umutex = Lock()
new_users = []
@app.route("/register",methods=["POST"])
def ClientRegister():
    try:
        umutex.acquire()
        d = melion.PostToDict()
        phone = d["phone-number"]
        #status = int(defines.UserStatus.PendingSMS)
        apikey = ''.join(random.choice("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789") for i in range(0,30))
        #request.mcursor.execute("INSERT INTO user (phone,status,apikey) VALUES (%s,%s,%s)",(phone,status,melion.uhash(apikey)))
        #request.mcon.commit()
        new_users.append({"phone":phone,"key":melion.uhash(apikey)})
        return json.dumps({"status":"ok","key":apikey})
    except:
        return GetErrorJSON("invalid data")
    finally:
        umutex.release()
@app.route("/validate",methods=["POST"])
def ClientVerify():
    try:
        umutex.acquire()
        d = melion.PostToDict()
        #uid,status = melion.ParseAPIKey()
        sms_code = d["sms-key"]
        apikey = melion.uhash(request.headers["melion-key"]) 
        phone = None
        for u in new_users:
            if u["key"] == apikey:
                phone = u["phone"]
                new_users.remove(u)
                break
        if phone == None:
            raise Exception("not a valid user")
        #if status != defines.UserStatus.PendingSMS:
        #    raise Exception("invalid user")
        if sms_code != "1234":
                return GetErrorJSON("invalid")
        #request.mcursor.execute("UPDATE user SET status=%s WHERE id=%s",(int(defines.UserStatus.Valid),uid))
        try:
            request.mcursor.execute("INSERT INTO user (phone,status,apikey) VALUES (%s,%s,%s)",(phone,defines.UserStatus.Valid,apikey))
        except:
            request.mcursor.execute("UPDATE user SET apikey=%s WHERE phone=%s",(apikey,phone))
        request.mcon.commit()
        if request.mcursor.rowcount != 1:
                raise Exception("didnt catch a user")
        return """{"status":"ok"}"""
    #except:
    #    return GetErrorJSON("invalid data")
    finally:
        umutex.release()
@app.route("/status",methods=["GET"])
def ClientStatus():
    pak = melion.ParseAPIKey(True)
    if not pak:
        try:
            umutex.acquire()
            apikey = melion.uhash(request.headers["melion-key"])
            for u in new_users:
                if u["key"] == apikey:
                    return """{"status":"ok","value":"pending"}"""
        finally:
            umutex.release()
        return GetErrorJSON("unknown")
    uid,status = pak
    if status == defines.UserStatus.Valid:
        return """{"status":"ok","value":"ready"}"""
    elif status == defines.UserStatus.PendingSMS:
        return """{"status":"ok","value":"pending"}"""
    return GetErrorJSON("unknown")
