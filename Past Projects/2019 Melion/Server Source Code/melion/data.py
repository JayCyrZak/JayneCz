import json
from typing import List

class Tile:
    def __init__(self, color: str, latitude: float, longitude: float, size: float):
        self.color = color
        self.latitude = latitude
        self.longitude = longitude
        self.size = size

    @classmethod
    def from_json(cls, json_str: str) -> "Tile":
    	t = json.loads(json_str)
    	return cls(t["color"], t["latitude"], t["longitude"], t["size"])

    def as_dict(self):
        return {"color": self.color, "latitude":  self.latitude, "longitude": self.longitude}

def get_tile_dict(tile):
    return {
        "color": tile.color,
        "latitude":  tile.latitude,
        "longitude": tile.longitude,
        "size": tile.size
    }

class Global:
    def __init__(self, tiles: List[Tile] = []):
        self.tiles = tiles

    def add_tile(self, tile: Tile):
        self.tiles.append(tile)

    def to_json(self) -> str:
        d = {}
        d["tiles"] = self.tiles
        return json.dumps(d, default=get_tile_dict)

   
        


'''demo'''
def demo_response():
    return Global([Tile("ffaaaaaa", -35.016, 143.321, 1.1),Tile("ffaaaaaa",-34.747, 145.592, 1.1),Tile("ffaaaaaa",-34.364, 147.891, 1.1),Tile("ffaaaaaa",-33.501, 150.217, 1.1),Tile("ffaaaaaa",-32.306, 149.248, 1.1),Tile("ffaaaaaa",-32.491, 147.309, 1.1)])
