import uuid
import random
from datetime import datetime, timedelta


class Player:
    def __init__(self, username):
        self.session_id = str(uuid.uuid1())
        self.username = username
        self.room_id = None
        self.score = 0
        self.total_score = 0

    def __str__(self):
        return f"{self.username}, room: {self.room_id}, score: {self.score}, total_score: {self.total_score}"

    def set_room_id(self, room_id):
        self.room_id = room_id

    def member_of(self, room_id):
        return self.room_id == room_id

    def set_score(self, new):
        self.score = new

    def update_total(self):
        self.total_score += self.score

    def clear_stats(self):
        self.score = 0
        self.total_score = 0

class Room:
    taken_ids = []

    def __init__(self, host_id, name):
        self.host_id = host_id
        self.players = [] #store IDs of players, all data manipulation takes place in all_players
        self.players_limit = 5
        self.game_duration = 10  # game duration in seconds
        self.end_time = datetime.now()
        self.start_game = False
        self.map_size = 10
        self.start_in = 999
        self.name = name
        self.map = ""
        self.players_over = 0
        self.can_request = False
        self.start_point = ""

        #setting unique room_id
        id_tmp = random.randint(1000, 10000)

        while id_tmp in Room.taken_ids:
            id_tmp = random.randint(1000, 10000)

        Room.taken_ids.append(id_tmp)

        self.room_id = id_tmp

    def __str__(self):
        return f"[ROOM INFO] Name: {self.name}, Players: {len(self.players)}/{self.players_limit}, Start_in: {self.start_in}"


    def reset(self):
        self.start_game = False
        self.start_in = 999
        self.players_over = 0
        self.can_request = False
        self.map = ""
        self.start_point = ""

    def free_slot(self):
        return self.players_limit > len(self.players)

    def add_player(self, session_id):
        self.players.append(session_id)

    def remove_player(self, session_id):
        if self.host_id == session_id: self.host_id = None

        self.players[:] = [p for p in self.players if p != session_id]

    def list_players(self, all_players):
        pls = []

        if not self.start_game:
            for s_id in self.players:
                for player in all_players.players:
                    if s_id == player.session_id:
                        pls.append({"session_id": player.session_id, "username": player.username, "score": player.score, "total_score": player.total_score})
                        break
        else:
            for s_id in self.players:
                for player in all_players.players:
                    if s_id == player.session_id:
                        pls.append({"session_id": player.session_id, "username": player.username, "score": player.score})
                        break


        return pls

    def get_info(self, all_players):

        if not self.start_game:
            return {"room_members": self.list_players(all_players), "start_in": self.start_in, "host_id": self.host_id}

        elif self.end_time > datetime.now():
            return {"room_members": self.list_players(all_players), "time_left": (self.end_time - datetime.now()).seconds, "host_id": self.host_id}

        else:
            return {"room_members": self.list_players(all_players), "time_left": -1 , "host_id": self.host_id}

    def start_countdown(self):
        self.start_in = 4
        self.end_time = datetime.now() + timedelta(seconds = self.game_duration+4) #+ for players' countdown

    def set_map_size(self, new_size):
        #verify if size correct
        self.map_size = new_size

    def set_players_limit(self, new_limit):
        self.players_limit = new_limit

    def set_map(self, map, start_point):
        self.map = map
        self.start_point = start_point

    def set_game_duration(self, duration):
        self.game_duration = duration