import socket
from _thread import start_new_thread
from abstract_collections import *
from client_thread import client_thread
from time import sleep

HOST = "127.0.0.1"
PORT = 1337

print("[", datetime.now(), "] [INFO] server started")

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind((HOST, PORT))
server.listen()

all_rooms = RoomsCollection()
all_players = PlayersCollection()

while True:
    conn, addr = server.accept()
    conn.settimeout(180)
    start_new_thread(client_thread, (conn, addr, all_players, all_rooms,))
    sleep(0.005)