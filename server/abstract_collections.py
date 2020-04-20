from abstracts import *

class RoomsCollection:
    def __init__(self):
        self.rooms = []

    def add_room(self, r):
        self.rooms.append(r)

    def delete_room(self, room_id):
        self.rooms[:] = [r for r in self.rooms if r.room_id != room_id]
        Room.taken_ids[:] = [id for id in Room.taken_ids if id != room_id]


    def get_room(self, room_id):
        for r in self.rooms:
            if r.room_id == room_id: return r

        return None

    def update_room(self, updated_room):
        for r in self.rooms:
            if r.room_id == updated_room.room_id:
                r = updated_room
                break

    def get_info(self):
        info = []

        for r in self.rooms:
            if not r.start_game:
                info.append({"room_id": r.room_id, "room_name": r.name, "players": len(r.players), "max_players": r.players_limit})

        return info

class PlayersCollection:
    def __init__(self):
        self.players = []

    def add_player(self, p):
        self.players.append(p)

    def get_player(self, session_id):
        for p in self.players:
            if p.session_id == session_id: return p

        return None

    def delete_player(self, session_id):
        self.players[:] = [p for p in self.players if p.session_id != session_id]

    def update_player(self, updated_player):
        for p in self.players:
            if p.session_id == updated_player.session_id:
                p = updated_player
                break