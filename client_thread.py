from client_states import *

from abstract_collections import *

all_rooms = RoomsCollection()
all_players = PlayersCollection()


def client_thread(conn, addr, all_players, all_rooms):

    username = ""
    session_id = ""

    print("[", datetime.now(), "] [INFO] ", addr, "connected")

    data = conn.recv(2048)

    if not data:
        return
    else:
        """
        open a new session
        """
        try:

            data_json = json.loads(data.decode())
            username = data_json["username"]

            new_player = Player(username)
            session_id = new_player.session_id

            all_players.add_player(new_player)

            reply = json.dumps({"session_id": session_id})
            reply = java_format(reply)
            conn.sendall(str.encode(reply))


        except Exception as e:
            print(e)
            reply = json.dumps({"message": "INCORRECT DATA FORMAT"})
            reply = java_format(reply)
            conn.sendall(str.encode(reply))
            return


    while True:

        alive = True
        player = all_players.get_player(session_id)
        if player.room_id:

            alive, game_active = in_lobby(conn, session_id, all_players, all_rooms)

            if alive and game_active:
                alive = in_game(conn, session_id, all_players, all_rooms)

        else:
            alive = pre_lobby(conn, session_id, all_players, all_rooms)

        if not alive: break



    p = all_players.get_player(session_id)

    if p.room_id:
        r = all_rooms.get_room(p.room_id)
        r.remove_player(session_id)
        all_rooms.update_room(r)

    all_players.delete_player(session_id)

    print("[", datetime.now(), "] [INFO] ", addr, "disconnected")
