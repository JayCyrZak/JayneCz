#!/usr/bin/env python3

import sys,time,mysql.connector,mysql.connector.cursor,hashlib,json,random,traceback

from flask import Flask,request

app = Flask(__name__)

log_file = "/var/www/html/gajhaiuwgahuibv/"+str(time.time())+".log"

def LogError(msg):
    f = open(log_file,"a")
    f.write(time.ctime()+" "+msg+"\nTrace:\n"+traceback.format_exc()+"\n"*5)
    f.close()

def GetErrorJSON(error):
    return json.dumps({"status":"failed","reason":error})


try:
    import defines
    import achievements,competitive,config,data,friends,map,pictures,profiles,search,user,leaderboards
except:
    LogError("Exception import failed")



@app.errorhandler(Exception)
def ExceptionHandler(e):
    LogError("Exception occured: "+str(e))
    return GetErrorJSON("failed")

def uhash(str):
    sha512 = hashlib.sha512()
    sha512.update(str.encode())
    return sha512.digest()

def PostToDict():
    return json.loads(request.data.decode())
def ParseAPIKey(bSafeRet=False):
    apikey = request.headers["melion-key"]
    bapikey = uhash(apikey)
    request.mcursor.execute("SELECT id,status FROM user WHERE apikey=%s",(bapikey,))
    res = request.mcursor.fetchone()
    if bSafeRet and res == None:
        return False
    elif res == None:
        raise Exception("invalid api key") 
    return (res[0],defines.UserStatus(res[1]))

# flask part for serving requests
# setup
def flask_setup():
    LogError("flask: started")
app.before_first_request(flask_setup)
# shutdown
def flask_shutdown(x):
    request.mcon.close()
    return x
app.after_request(flask_shutdown)
# request serving
@app.before_request
def before_request():
    request.mcon = mysql.connector.connect(host="localhost",user="melion",password="abcdefghijklmnopqrstuvwxyz",
        database="melion",use_pure=True)
    request.mcursor = request.mcon.cursor(cursor_class=mysql.connector.cursor.MySQLCursorPrepared)
    
@app.route("/",defaults={"path":""})
@app.route("/<path:path>")
def ApiMain(path):
    return GetErrorJSON("invalid path")
