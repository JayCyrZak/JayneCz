import melion
import json
import time
from threading import Thread, Lock
import defines
from melion import *

mapSize = 20 #x==y #20
maxTeamSize = 5
matchDuration = 300#sec # 300

def inCircle(x,y):
    dist = ((mapSize/2 - (x + 0.5 ))**2 + (mapSize / 2 - (y + 0.5 ))**2)**0.5
    if dist < mapSize/2:
        return True
    return False

def getUName(uid):
    request.mcursor.execute("SELECT name FROM user WHERE id = %s",(uid,))
    return request.mcursor.fetchone()

class Match:
    '''
    represents a Match
    match status: 
        0 not started
        1 running
        2 finished
    '''
    def __init__(self, team1, team2):
        self.teams = (team1, team2)
        self.startTime = int(time.time())
        self.map = []
        self.generateField()
        self.status = 1

    def generateField(self):
        for i in range(0, mapSize):
            self.map.append([])
            for j in range(0, mapSize):
                if inCircle(i,j):
                    self.map[i].append(0)
                else:
                    self.map[i].append(-1)
    
    def getFields(self):
        f = []
        for x in range(0, mapSize):
            for y in range(0, mapSize):
                f.append({"x": x, "y": y, "val": self.map[x][y]})
        return f

    def inField(self, x, y):
        return (x >= 0 and x < mapSize and y >= 0 and y < mapSize)

    #if multi paint 9 fields
    def paint(self, tid, pos, multi):
        x = pos[0]
        y = pos[1]
        if multi:
            for i in range(-1, 2):
                for j in range(-1, 2):
                    x = pos[0] + i
                    y = pos[1] + j
                    if self.inField(x, y):
                        if (self.map[x][y] > -1) and (self.map[x][y] != tid + 1):
                            self.map[x][y] = tid + 1
                            self.teams[tid].updateScore()  
        else:
            if (self.map[x][y] > -1) and (self.map[x][y] != tid + 1):
                self.map[x][y] = tid + 1
                self.teams[tid].updateScore()

    def getNumberOfTeamTiles(self):
        s1 = 0
        s2 = 0
        for i in self.map:
            for j in i:
                if j == 1:
                    s1 +=1
                if j == 2:
                    s2 +=1
        return (s1,s2)

class Team:
    '''
    represents a team
    team status:
        0 building (adding new members)
        1 searching
        2 playing
        3 ended
    '''
    def __init__(self, leaderId, midCoord):
        self.leader = leaderId
        self.leaderName = getUName(leaderId)
        self.players = [leaderId]
        self.midCoord = midCoord
        self.lastPos = {}
        self.status = 0
        self.score = 0
        self.friendshipMultiplier = 1.0
        self.empty = False

    def isMember(self, uid):
        for i in self.players:
            if i == uid:
                return True
        return False

    def isClose(self, p1, p2):
        dist = ( (p1[0] - p2[0]) ** 2 + (p1[1]  - p2[1] )  ** 2 ) ** 0.5 #TODO vielleicht + 0.5
        if dist < 4:
            return True
        return False

    def playerClose(self, uid):
        for key in self.lastPos:
            if key is not uid:
                if self.isClose(self.lastPos[uid], self.lastPos[key]):
                    return True
        return False

    def updateScore(self, s=1):
        self.score += s

    def setFriendshipMultiplier(self, numberOfFriendConnections):
        self.friendshipMultiplier = 1.0 + 0.1 * numberOfFriendConnections

matches = []
teams = []
mutex = Lock()

def joinTeamByLeaderId(playerId, leaderId):
    '''
    not possible to join a running match
    returns True if Player joind Team
    '''
    for i in teams:
        if i.status == 0:
            if i.leader == leaderId:
                if len(i.players) < maxTeamSize:
                    i.players.append(playerId)
                    return True
    return False

def getTeamByUId(uid):
    for i in teams:
        if i.isMember(uid):
            return i
    return None

def getTeamByLeaderId(uid):
    for i in teams:
        if i.leader == uid:
            return i
    return None

def getMatchAndTeamIdByUId(uid):
    '''
    returns a reference to the match and the team id (0 or 1)
    '''
    for i in matches:
        for j in range(0, 2):
            if i.teams[j].isMember(uid):
                return (i, j)
    return (None, -1)

def matchmaking():
    searching_teams = []
    for i in teams:
        if i.status == 1:
            searching_teams.append(i)
    if len(searching_teams) > 1:
            searching_teams[0].status = 2
            searching_teams[1].status = 2
            matches.append(Match(searching_teams[0], searching_teams[1]))
            searching_teams.pop(0)
            searching_teams.pop(0)

'''
Status Messages
'''
def statusMsg(success, reason ="none", matchStatus = 0):
    msg = {}
    msg["status"] = "ok" if success else "failed"
    msg["reason"] = reason
    msg["match_status"] = matchStatus
    return msg

def gameStatusMsg(match, teamId, timer):
    msg = {}
    msg["score_own_team"] = match.teams[teamId].score
    msg["score_competitor"] = ( match.teams[1].score if teamId == 0 else match.teams[0].score )
    msg["timer"] = timer if timer >= 0 else 0
    msg["fields"] = match.getFields()
    msg["match_status"] = match.status
    msg["status"] = "ok"
    return msg

def gameFinalStatusMsg(match, teamId, timer):
    ownFScore = int(match.teams[teamId].score * match.teams[teamId].friendshipMultiplier)
    compFScore = int( ( match.teams[1].score *  match.teams[1].friendshipMultiplier if teamId == 0 else match.teams[0].score * match.teams[1].friendshipMultiplier ) )
    msg = {}
    msg["score_own_team"] = ownFScore
    msg["score_competitor"] = compFScore
    msg["timer"] = timer if timer >= 0 else 0
    msg["fields"] = match.getFields()
    msg["match_status"] = match.status
    msg["status"] = "ok"
    return msg

def emptyGameStatusMsg():
    msg = {}
    msg["score_own_team"] = 0
    msg["score_competitor"] = 0
    msg["timer"] = matchDuration
    msg["fields"] = []
    msg["match_status"] = 0
    msg["status"] = "ok"
    return msg

@app.route("/competitive/create/<string:midLat>,<string:midLon>",methods=["GET"])
def createTeam(midLat,midLon):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed",0))
    
    if getTeamByUId(uid) != None:
        return json.dumps(statusMsg(False, "You are already in a team",0))

    midLat = float(midLat)
    midLon = float(midLon)
    
    mutex.acquire()
    try:
        teams.append(Team(uid, (midLat,midLon)))
    finally:
        mutex.release()

    return json.dumps(statusMsg(True,matchStatus=0))

@app.route("/competitive/join/<int:creator_id>",methods=["GET"])
def joinTeam(creator_id):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed",0))

    if getTeamByUId(uid) != None:
        return json.dumps(statusMsg(False, "You are already in a team",0))
    
    res = False
    mutex.acquire()
    try:  
        res = joinTeamByLeaderId(uid, creator_id)
    finally:
        mutex.release()

    if not res:
        return json.dumps(statusMsg(False, "failed to join team",0))

    t = getTeamByUId(uid)
    msg = {}
    msg["match_status"] = 0
    msg["status"] = "ok"
    msg["mid_lat"] = t.midCoord[0]
    msg["mid_lon"] = t.midCoord[1]

    return json.dumps(msg)

@app.route("/competitive/search",methods=["GET"])
def searchTeam():
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed",0))

    leaders = []

    mutex.acquire()
    try:
        for i in teams:
            if i.status == 0:
                    leaders.append({"id" : i.leader, "name": i.leaderName})
    finally:
        mutex.release()

    msg = {}
    msg["teams"] = leaders
    msg["status"] = "ok"
    msg["match_status"] = 0

    return json.dumps(msg)

@app.route("/competitive/team",methods=["GET"])
def teamStatus():
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed", 0))
    
    t = getTeamByUId(uid)
    if t == None:
        return json.dumps(statusMsg(False, "not in a team", 0))

    l = []
    msg = {}
    mutex.acquire()
    try:
        for i in t.players:
            l.append({"name": getUName(i)})
    finally:
        mutex.release()

    msg["status"] = "ok"
    msg["team"] = l
    return json.dumps(msg)


@app.route("/competitive/start",methods=["GET"])
def startMatch():
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed", 0))

 
    t = getTeamByLeaderId(uid)
    if t is None:
        return json.dumps(statusMsg(False, "the match can only be started by the team creator", 0))
    
    mutex.acquire()
    try:
        t.status = 1
        friendConnectionsCtr = 0
        for i in t.players:
            for j in t.players:
                request.mcursor.execute("SELECT id FROM friends WHERE id_added  = %s AND id_accepted = %s and status=%s",(i,j,defines.FriendStatus.Valid))
                if request.mcursor.fetchone() is not None:
                    friendConnectionsCtr += 1
        
        t.setFriendshipMultiplier(friendConnectionsCtr)
        matchmaking()
    finally:
        mutex.release()
    return json.dumps(statusMsg(True, "Your team is in the matchmaking queue",0))



@app.route("/competitive/update/<int:x>,<int:y>",methods=["GET"])
def update(x, y):
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed",0))
    
    m,tid = getMatchAndTeamIdByUId(uid)
    if m is None: #match has not jet started
        return json.dumps(emptyGameStatusMsg())
        
    timer = m.startTime + matchDuration - int(time.time())

    mutex.acquire()
    try:
        if timer < 0:
            m.status = 2
            
        if m.status == 1:
            #update map if field was panted by other team
            if not(x < 0 or x >= mapSize or y < 0 or y >= mapSize):
                m.teams[tid].lastPos[uid] = (x, y)
                m.paint(tid, (x,y), m.teams[tid].playerClose(uid))
                
        msg = gameStatusMsg(m,tid,timer)
    except Exception as e:
        msg = statusMsg(False,str(e),1)
    finally:
        mutex.release()

    return json.dumps(msg)
    



# player leaves game
# can only start a new game after the end request is send
@app.route("/competitive/end",methods=["GET"])
def end():
    uid,status = melion.ParseAPIKey()
    if status != defines.UserStatus.Valid:
        return json.dumps(statusMsg(False, "authentication failed",0))

    m,tid = getMatchAndTeamIdByUId(uid)

    if m is None:
        #remove from teams list
        t = getTeamByUId(uid)
        if t != None:
            if t.leader == uid:
                teams.remove(t)
            else:
                t.players.remove(uid)
        
        return json.dumps(statusMsg(False, "player left the queue",0))

    #if m.status != 2:
    #    return json.dumps(statusMsg(False, "game has not ended",0))

    #Friendship Bonus-Points at the end of Match
    #s1, s2 = m.getNumberOfTeamTiles()
    msg = gameFinalStatusMsg(m, tid, 0)

    #remove player from team
    mutex.acquire()
    try:
        m.teams[tid].players.remove(uid)
        m.teams[tid].lastPos.pop(uid, None)
        if  m.teams[tid].leader == uid:
            m.teams[tid].leader = -1
        #if team is empty remove it
        if len(m.teams[tid].players) == 0:
            teams.remove(m.teams[tid])
            m.teams[tid].empty = True
        #if team is empty remove match
        if m.teams[0].empty and m.teams[1].empty:
            matches.remove(m)
    finally:
        mutex.release()
    return json.dumps(msg)
    
@app.route("/competitive/debug_status",methods=["GET"])
def debugStatus():
    t = []
    for i in teams:
        t.append(i.players)

    m = []
    for i in matches:
        t1,t2 = i.getNumberOfTeamTiles()
        m.append({
            "score_team1" : i.teams[0].score,
            "score_team2" : i.teams[1].score,
            "tiles_team1" : t1,
            "tiles_team2" : t2,
            "friendship_team1" : i.teams[0].friendshipMultiplier,
            "friendship_team2" : i.teams[1].friendshipMultiplier,
            "timer" : i.startTime + matchDuration - int(time.time()),
            "match_status" : i.status
            })
    
    return str(t) + "\n\n" + str(m)

@app.route("/competitive/debug_clear",methods=["GET"])
def clearLists():
    nt = len(teams)
    nm = len(matches)
    teams.clear()
    matches.clear()
    return "nt:" + str(nt) + " nm:" + str(nm)



