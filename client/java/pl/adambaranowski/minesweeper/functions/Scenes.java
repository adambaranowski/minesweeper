package pl.adambaranowski.minesweeper.functions;

public enum Scenes {
    ABOUT_SCENE("/fxml/about/aboutPane.fxml"),
    MENU_SCENE("/fxml/menu/startPane.fxml"),
    SINGLE_START_SCENE("/fxml/single/singlePlayerStartPane.fxml"),
    SINGLE_GAME_SCENE("/fxml/single/singlePlayerGamePane.fxml"),
    MULTI_LOGIN_SCENE("/fxml/multi/multiPlayerLoginPane.fxml"),
    MULTI_CHOOSE_OPTION_SCENE("/fxml/multi/multiPlayerChooseOptionPane.fxml"),
    MULTI_JOIN_ROOM_SCENE("/fxml/multi/multiPlayerRoomJoinPane.fxml"),
    MULTI_CREATE_ROOM_SCENE("/fxml/multi/multiPlayerCreateRoomPane.fxml"),
    MULTI_HOST_LOBBY_SCENE("/fxml/multi/multiPlayerHostLobbyPane.fxml"),
    MULTI_PLAYER_LOBBY_SCENE("/fxml/multi/multiPlayerPlayerLobbyPane.fxml"),
    MULTI_GAME_SCENE("/fxml/multi/multiPlayerGamePane.fxml");

    private String fxmlPath;

    Scenes(String fxmlPath){
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}
