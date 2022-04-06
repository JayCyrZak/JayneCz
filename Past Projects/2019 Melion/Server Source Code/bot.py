import os,json,time,subprocess,math,random,sys

melion_key = "AAAAAAAA"

def RequestPOST(path,data):
	args = ["curl",
		"-X",
		"POST",
		"-d",
		data,
		"-H",
		"Content-Type: application/json",
		"-H",
		"Melion-Key: "+melion_key,
		"https://socialgaming.deine.cloud/api"+path
	]
	x = subprocess.Popen(args,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
	x.wait()
	print("stdout:",x.stdout.read())
	return True
	pass
def RequestGET(path):
	args = ["curl",
		"-X",
		"GET",
		"-H",
		"Melion-Key: "+melion_key,
		"https://socialgaming.deine.cloud/api"+path
	]
	x = subprocess.Popen(args,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
	x.wait()
	print("stdout:",x.stdout.read())
	return True
#
def DeleteTiles(num_to_keep):
	return RequestGET("/map/bot-delete/"+str(num_to_keep))
def InsertTile(lat,long,color,size):
	return RequestPOST("/map/update",json.dumps({
		"latitude":lat,
		"longitude":long,
		"color":color,
                "size":float(size)
	}))
#

TILE_SIZE = 5

def BotCircle(middle_lat,middle_long,radius,num_tiles):
	step = 2 * 3.1415926 / num_tiles
	print("step: ",step)
	while True:
		time.sleep(1)
		for i in range(0,num_tiles):
			time.sleep(0.01)
			p = i * step
			lat = middle_lat + math.sin(p) * radius
			long = middle_long + math.cos(p) * radius
			#print("off: ",math.sin(p),math.cos(p))
			print("lat/long:",lat,long)
			color = 0xFF000000 + 0x10000 * random.randint(0,255) + 0x100 * random.randint(0,255) + random.randint(0,255)
			InsertTile(lat,long,color,TILE_SIZE)
			DeleteTiles(num_tiles-3)
def BotRect(middle_lat,middle_long,side_length,num_tiles):
	while True:
		time.sleep(1)
		for i in range(num_tiles):
			time.sleep(5)
			side = i % num_tiles
			off = side_length * i/num_tiles / 4
			lat = middle_lat - side_length/2
			long = middle_long - side_length/2
			if side == 0:
				lat += off
			elif side == 1:
				lat += side_length
				long += off
			elif side == 2:
				lat += side_length - off
				long += side_length
			elif side == 3:
				long += side_length - off
			#print("lat/long:",lat,long)
			color = 0xFF000000 + 0x10000 * random.randint(0,255) + 0x100 * random.randint(0,255) + random.randint(0,255)
			InsertTile(lat,long,color,TILE_SIZE)
			DeleteTiles(num_tiles)
	pass

latitude = float(sys.argv[1])
longitude = float(sys.argv[2])
radius = float(sys.argv[3])
points = int(sys.argv[4])

BotCircle(latitude,longitude,radius,points)
#BotRect(latitude,longitude,radius,points)
