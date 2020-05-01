# MINESWEEPER 

## SERVER

### Workflow

1. Open a connection.

2. Send data in JSON format: 
    ```json
    {
      "username": "NAME"
    }
    ```
    
    On success server replies with: 
    
    ```json
    {
      "session_id": "session_id <string>"
    }
    ```
   
3. *pre_lobby* state - 3 requests
    - **CREATE_ROOM**
        ```json
           {
            "request": "CREATE_ROOM",
            "data": "ROOM_NAME <string>"
           }
        ``` 
        On success server replies with:
        ```json
           {
            "message": "OK",
            "room_id": "ROOM_ID <int>"
           }
        ``` 
        If ID is taken then message = "ID_TAKEN"
       
    - **JOIN_ROOM**
        ```json
           {
            "request": "JOIN_ROOM",
            "data": "ROOM_ID <int>"
           }
        ``` 
   - **ALL_ROOMS**
        ```json
           {
            "request": "ALL_ROOMS"
           }
        ```    
       On success server replies with:
        ```json
           {
            "message": "OK",
            "data": 
            [
               {
                 "room_id": "ROOM_ID<int>",
                 "room_name": "ROOM_NAME<string>",
                 "players": "NUMBER_OF_MEMBERS<int>",
                 "max_players": "MAX_PLAYERS<int>"
               }   
            ]
           }
        ```    

4. *in_lobby* - 2 lobby member requests + lobby host requests
    - **GET_INFO**
        ```json
           {
            "request": "GET_INFO"
           }    
        ```
        On success server replies with:
        ```json
        {
           "message": "OK", 
           "data": "room_info"
        }
        ```
        *room_info* looks something like this:
        ```json
        {
           "room_members": 
               [
                   {
                     "session_id": "USER_SESSION_ID <string>", 
                     "username": "NAME <string>"
                   }
               ], 
           "start_countdown": "TRUE_WHEN_GAME_STARTS <bool>", 
           "host_id": "HOST_SESSION_ID <string>"
        }
        ```
   
   - **LEAVE_ROOM**
        ```json
           {
             "request": "LEAVE_ROOM"
           }    
        ```
       On success client goes back to *pre_lobby*.
   
   **HOST REQUESTS**
   - **START_GAME**
        ```json
           {
             "request": "START_GAME",
             "data": 
               {    
                 "map": "MAP<string>",
                 "start_point": "POINT" 
               }  
           }    
        ```

   - **KICK_PLAYER**
        ```json
           {
             "request": "KICK_PLAYER",
             "data": "PLAYER_SESSION_ID"
           }    
        ```
   
   - **SET_SIZE**
        ```json
           {
             "request": "SET_SIZE",
             "data": "MAP_SIZE<int>"
           }    
        ```
   - **SET_TIME**
        ```json
           {
             "request": "SET_TIME",
             "data": "GAME_DURATION_IN_SECS<int>"
           }    
        ```
      
5. *in_game* - actual game
    - **GET_INFO**
        ```json
           {
            "request": "GET_INFO"
           }    
        ```
        On success server replies with:
        ```json
            {
                "message": "OK", 
                "data": "room_info <defined below>"
            }
        ```
        *room_info*:
        ```json
            {
                "room_members": [
                       {
                         "session_id": "USER_SESSION_ID <string>", 
                         "username": "NAME <string>",
                         "score": "SCORE<int>"
                       }
                   ], 
                "time_left": "TIME_LEFT_IN_SECS <int>", 
                "host_id": "HOST_ID<string>"
            }
        ```
    - **GET_MAP**
        ```json
           {
              "request": "GET_MAP"
           }    
        ```    
        Server reply:
        ```json
            {
                "message": "OK", 
                "data": 
                    {
                        "map": "MAP<string>", 
                        "size": "SIZE<int>",
                        "start_point": "START_POINT"
                    }
            }
        ```
    - **UPDATE_SCORE** - float in range 0-1.
        ```json
            {
                "request": "UPDATE_SCORE", 
                "data": "NEW_SCORE<double>"
            }
        ```
        Server reply:
        ```json
            {
                "message": "OK"
            }
        ```
    - **LOST** - when player clicked on a bomb
        ```json
            {
                "request": "LOST"
            }
        ```
        Server reply:
        ```json
            {
                "message": "OK"
            }
        ```
