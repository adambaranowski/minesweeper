import uuid
import random
from datetime import datetime, timedelta


class Player:
    def __init__(self, username):
        self.session_id = str(uuid.uuid1())
        self.username = username
        self.room_id = None
        self.score = 0

    def set_room_id(self, room_id):
        self.room_id = room_id

    def member_of(self, room_id):
        return self.room_id == room_id

    def set_score(self, absolute=0, increment=0):
        if absolute: self.score = absolute
        else: self.score += increment

class Room:
    taken_ids = []

    def __init__(self, host_id, name):
        self.host_id = host_id
        self.players = []
        self.players_limit = 5
        #self.start_time = datetime.now() + timedelta(seconds=10)
        self.game_duration = 10  # game duration in seconds
        #self.end_time = self.start_time + timedelta(seconds=self.game_duration)
        self.end_time = datetime.now()
        self.start_game = False
        self.map_size = 10
        self.start_in = 999
        self.name = name

        #setting unique room_id
        id_tmp = random.randint(1000, 10000)

        while id_tmp in Room.taken_ids:
            id_tmp = random.randint(1000, 10000)

        Room.taken_ids.append(id_tmp)

        self.room_id = id_tmp


    def free_slot(self):
        return self.players_limit > len(self.players)

    # def add_time(self, delta = 30):
    #     self.start_time += timedelta(seconds=delta)

    def add_player(self, p):
        self.players.append(p)

    def remove_player(self, session_id):
        if self.host_id == session_id: self.host_id = None

        self.players[:] = [p for p in self.players if p.session_id != session_id]

    def list_players(self):
        collection = []

        if not self.start_game:
            for p in self.players:
                collection.append({"session_id": p.session_id, "username": p.username})
        else:
            for p in self.players:
                collection.append({"session_id": p.session_id, "username": p.username, "score": p.score})

        return collection

    def get_info(self):

        if not self.start_game:
            return {"room_members": self.list_players(), "start_in": self.start_in, "host_id": self.host_id}

        elif self.end_time > datetime.now():
            return {"room_members": self.list_players(), "time_left": (self.end_time - datetime.now()).seconds, "host_id": self.host_id}

        else:
            return {"room_members": self.list_players(), "time_left": 0, "host_id": self.host_id}

    def start_countdown(self):
        self.start_in = 5
        self.end_time = datetime.now() + timedelta(seconds = self.game_duration+5) #+5 for players' countdown

    def set_map_size(self, new_size):
        #verify if size correct
        self.map_size = new_size

    def set_players_limit(self, new_limit):
        self.players_limit = new_limit