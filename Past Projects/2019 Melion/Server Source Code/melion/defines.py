from enum import IntEnum

DEFAULT_TILE_SIZE = 0.0001
FRIEND_SQUARE_SIZE = 0.001

UPLOAD_DIRECTORY = "/var/www-uploads/"

class UserStatus(IntEnum):
    AddedByOther = 1
    PendingSMS = 2
    Valid = 3
class FriendStatus(IntEnum):
    Pending = 1
    Rejected = 2
    Valid = 3
class FriendRequestDirection(IntEnum):
    Unknown = 0
    Incoming = 1
    Outgoing = 2
