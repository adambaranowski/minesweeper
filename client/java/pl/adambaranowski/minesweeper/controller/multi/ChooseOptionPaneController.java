package pl.adambaranowski.minesweeper.controller.multi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.adambaranowski.minesweeper.functions.Scenes;
import pl.adambaranowski.minesweeper.functions.ScenesChanger;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;


public class ChooseOptionPaneController implements ScenesChanger {

    @FXML
    private Button createRoomButton;

    @FXML
    private Button joinRoomButton;

    @FXML
    private Button toMenuButton;

    public void initialize() {
        configureToMenuButton();
        configureJoinRoomButton();
        configureCreateRoomButton();
    }

    private void configureCreateRoomButton() {
        createRoomButton.setOnAction(actionEvent -> {
            changeScene(Scenes.MULTI_CREATE_ROOM_SCENE, createRoomButton);
        });
    }

    private void configureToMenuButton() {
        toMenuButton.setOnAction(actionEvent -> {
            changeScene(Scenes.MENU_SCENE, toMenuButton);
            WebSocketConnector.getInstance().disconnect();
        });
    }

    private void configureJoinRoomButton() {
        joinRoomButton.setOnAction(actionEvent -> {
            changeScene(Scenes.MULTI_JOIN_ROOM_SCENE, joinRoomButton);
        });
    }
}