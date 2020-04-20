import json
import socket
from _thread import start_new_thread
from room_thread import room_thread
from datetime import datetime
from abstracts import Room


def java_format(data):
    """
    :type data: str
    """

    return data + '~'


def pre_lobby(conn, session_id, all_players, all_rooms):
    """
    Either join a room or create a new one.

    :param conn: connection socket
    :param session_id: player session ID
    :return:
    """

    while True:

        try:
            data = conn.recv(2048).decode()
            if not data:
                return False

            data = json.loads(data)

            player = all_players.get_player(session_id)

            # managing requests from now (eg. CREATE_ROOM, JOIN_ROOM etc.)
            if data["request"] == "CREATE_ROOM":

                if not player.room_id:
                    room_name = data["data"]["name"]
                    room_size = data["data"]["size"]

                    new_room = Room(player.session_id, room_name)

                    player.set_room_id(new_room.room_id)
                    new_room.add_player(player)
                    new_room.set_players_limit(room_size)
                    
                    all_rooms.add_room(new_room)
                    all_players.update_player(player)

                    start_new_thread(room_thread, (new_room.room_id, all_rooms, ))


                    reply = json.dumps({"message": "OK", "room_id": new_room.room_id})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))
                    break

            elif data["request"] == "JOIN_ROOM":
                room_id = data["data"]

                if not room_id in Room.taken_ids:
                    reply = json.dumps({"message": "ROOM NOT FOUND"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))

                    #break

                elif player.room_id:
                    reply = json.dumps({"message": "PLAYER ALREADY GOT A ROOM"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))

                    break


                else:
                    room = all_rooms.get_room(room_id)

                    if len(room.players) == room.players_limit:
                        reply = json.dumps({"message": "ROOM FULL"})
                        reply = java_format(reply)
                        conn.sendall(str.encode(reply))

                    else:

                        player.set_room_id(room_id)
                        room.add_player(player)

                        all_rooms.update_room(room)

                        room_info = room.get_info()
                        reply = json.dumps({"message": "OK", "room_info": json.dumps(room_info)})

                        reply = java_format(reply)

                        conn.sendall(str.encode(reply))
                        return True

            elif data["request"] == "ALL_ROOMS":
                rooms_info = all_rooms.get_info()
                reply = json.dumps({"message": "OK", "data": rooms_info})

                reply = java_format(reply)

                conn.sendall(str.encode(reply))

            elif data["request"] == "GET_INFO":

                reply = json.dumps({"message": "KICKED"})

                reply = java_format(reply)

                conn.sendall(str.encode(reply))

            else:
                reply = json.dumps({"message": "INCORRECT REQUEST"})

                reply = java_format(reply)

                conn.sendall(str.encode(reply))
                break

        except socket.error:
            return False

        except Exception as e:
            print("err", e)
            reply = "INCORRECT DATA FORMAT"
            reply = java_format(reply)
            conn.sendall(str.encode(reply))
            break

    return True



def in_lobby(conn, session_id, all_players, all_rooms):
    """
    also calls in_game, ends when the player can go back to pre_lobby

    :param conn: connection socket
    :param username: player username
    :param session_id: player session ID
    :return:
    """


    p = all_players.get_player(session_id)
    lobby = all_rooms.get_room(p.room_id)

    while not lobby.start_game:

        try:
            data = conn.recv(2048).decode()
            msg = json.loads(data)

            #check if not kicked/lobby about to be deleted
            if not p.room_id:
                reply = json.dumps({"message": "KICKED"})
                reply = java_format(reply)
                conn.sendall(str.encode(reply))
                return True, False


            if msg["request"] == "LEAVE_ROOM":

                if lobby.host_id == session_id:
                    #kick everyone if host leaves
                    for p in lobby.players:
                        print(len(lobby.players))

                        p.set_room_id(None)
                        all_players.update_player(p)

                    lobby.players = []

                    all_rooms.update_room(lobby)


                else:
                    lobby.remove_player(session_id)

                    p.set_room_id(None)

                    all_rooms.update_room(lobby)
                    all_players.update_player(p)

                reply = json.dumps({"message": "OK"})

                reply = java_format(reply)

                conn.sendall(str.encode(reply))
                return True, False


            elif msg["request"] == "GET_INFO":

                p = all_players.get_player(session_id)

                if not p.room_id:
                    reply = json.dumps({"message": "KICKED"})
                    reply = java_format(reply)

                    conn.sendall(str.encode(reply))
                    return True, False

                else:
                    room_info = lobby.get_info()
                    reply = json.dumps({"message": "OK", "data": room_info})

                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))


            elif msg["request"] == "START_GAME":
                if lobby.host_id == session_id:

                    lobby.start_countdown()

                    reply = json.dumps({"message": "OK"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))

            elif msg["request"] == "ADD_TIME":

                if lobby.host_id == session_id:
                    lobby.add_time()

                    all_rooms.update_room(lobby)

                    reply = json.dumps({"message": "OK"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))
                else:
                    reply = json.dumps({"message": "ONLY HOST CAN ADD TIME"})
                    conn.sendall(str.encode(reply))

            elif msg["request"] == "KICK_PLAYER":

                if lobby.host_id == session_id:
                    kicked_id = msg["data"]

                    if kicked_id != session_id:

                        kicked_player = all_players.get_player(kicked_id)

                        kicked_player.set_room_id(None)
                        lobby.remove_player(kicked_id)

                        all_rooms.update_room(lobby)
                        all_players.update_player(kicked_player)

                        reply = json.dumps({"message": "OK"})
                        reply = java_format(reply)
                        conn.sendall(str.encode(reply))

                    else:
                        reply = json.dumps({"message": "CANT KICK YOURSELF"})
                        reply = java_format(reply)
                        conn.sendall(str.encode(reply))

            elif msg["request"] == "SET_SIZE":
                if lobby.host_id == session_id:
                    new_size = msg["data"]
                    lobby.set_map_size(new_size)
                    all_rooms.update_room(lobby)

                    reply = json.dumps({"message": "OK"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))

            elif msg["request"] == "SET_LIMIT":
                if lobby.host_id == session_id:
                    new_limit = msg["data"]
                    lobby.set_players_limit(new_limit)
                    all_rooms.update_room(lobby)

                    reply = json.dumps({"message": "OK"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))

                else:
                    reply = json.dumps({"message": "ONLY HOST CAN EDIT ROOM SETTINGS"})
                    reply = java_format(reply)
                    conn.sendall(str.encode(reply))

            else:
                reply = json.dumps({"message": "INCORRECT REQUEST TYPE"})
                reply = java_format(reply)
                conn.sendall(str.encode(reply))



        except socket.error as e:
            print("[", datetime.now(), "]  [ERROR]:", e)
            return False, False

        except Exception as e:
            print("lobby error", e)
            reply = json.dumps({"message": "INCORRECT DATA FORMAT"})
            reply = java_format(reply)
            conn.sendall(str.encode(reply))
            return True, False

        lobby = all_rooms.get_room(p.room_id)
        p = all_players.get_player(session_id)

    #print("loop ended")
    return True, True



def in_game(conn, session_id, all_players, all_rooms):

    player = all_players.get_player(session_id)
    lobby = all_rooms.get_room(player.room_id)

    while lobby.end_time > datetime.now():

        try:

            request = conn.recv(2048).decode()

            json_data = json.loads(request)

            if json_data["request"] == "GET_INFO":
                reply = json.dumps({"message": "OK", "data": lobby.get_info()})
                reply = java_format(reply)
                conn.sendall(str.encode(reply))

            elif json_data["request"] == "CLICK":
                x, y = json_data["data"]["x"], json_data["data"]["y"]
                print("Click on", x, y)

                #validate

                reply = json.dumps({"message": "OK", "data": 10})
                reply = java_format(reply)
                conn.sendall(str.encode(reply))
            else:
                print(request.decode())
                reply = json.dumps({"message": "REQUEST RECEIVED"})
                reply = java_format(reply)
                conn.sendall(str.encode(reply))


        except socket.error as e:
            print("[", datetime.now(), "]  [ERROR]:", e)
            return False

        except Exception as e:
            print(e)
            reply = json.dumps({"message": "INCORRECT DATA FORMAT"})
            reply = java_format(reply)
            conn.sendall(str.encode(reply))

        lobby = all_rooms.get_room(player.room_id)


    all_rooms.delete_room(player.room_id)
    
    player.set_room_id(None)
    player.set_score(absolute=0)
    all_players.update_player(player)

    return True

