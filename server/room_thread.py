from datetime import datetime, timedelta
from time import sleep



def room_thread(room_id, all_rooms):

    while True:

        room = all_rooms.get_room(room_id)


        while room.start_in > 10:

            if not len(room.players) or not room.host_id:
                sleep(3)
                print("[", datetime.now(), "] [INFO] lobby", room_id, "deleted")
                all_rooms.delete_room(room_id)
                return

            sleep(0.5)

            room = all_rooms.get_room(room_id)


        while room.start_in > 0:
            room.start_in -= 1
            all_rooms.update_room(room)
            sleep(1)

        room.start_game = True

        all_rooms.update_room(room)


        print("[", datetime.now(), "] [INFO] game", room_id, "starts")

        while room.end_time > datetime.now():

            if not len(room.players) > room.players_over or not room.host_id:
                room.can_request = True
                room.end_time = datetime.now()
                all_rooms.update_room(room)

            sleep(0.05)
            room = all_rooms.get_room(room_id)




        #GAME ENDS
        sleep(0.5) #make sure everyone receives post game information before all information is deleted
        print("[", datetime.now(), "] [INFO] game", room_id, "ends")

        room.reset()
        all_rooms.update_room(room)
        print("[", datetime.now(), "] [INFO] room", room_id, "reset")
        print(room)
