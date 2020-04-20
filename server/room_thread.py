from datetime import datetime, timedelta
from time import sleep



def room_thread(room_id, all_rooms):
    #global all_rooms

    room = all_rooms.get_room(room_id)


    #PREGAME

    while room.start_in > 10:

        if not len(room.players) or not room.host_id:
            sleep(3)
            print("[", datetime.now(), "] [INFO] lobby", room_id, "deleted")
            all_rooms.delete_room(room_id)
            return

        sleep(1)

        room = all_rooms.get_room(room_id)


    while room.start_in > 0:
        room.start_in -= 1
        all_rooms.update_room(room)
        sleep(1)

    room.start_game = True
    all_rooms.update_room(room)


    """
    sleep(5) to wait for players' countdown
    """
    #sleep(5)
    print("[", datetime.now(), "] [INFO] game", room_id, "starts")

    while room.end_time > datetime.now():
        pass


    #GAME ENDS
    sleep(3) #make sure everyone receives post game information before all information is deleted
    print("[", datetime.now(), "] [INFO] game", room_id, "ends")
    all_rooms.delete_room(room_id)

    return