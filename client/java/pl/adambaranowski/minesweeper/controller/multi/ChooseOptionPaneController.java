package pl.adambaranowski.minesweeper.controller.multi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;

import java.io.IOException;

public class ChooseOptionPaneController extends SceneChanger {

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
            try {
                changeToCreateRoomScene(createRoomButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void configureToMenuButton() {
        toMenuButton.setOnAction(actionEvent -> {
            try {
                WebSocketConnector.getInstance().disconnect();
                changeToMenuScene(toMenuButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureJoinRoomButton() {
        joinRoomButton.setOnAction(actionEvent -> {
            try {
                changeToJoinRoomScene(joinRoomButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}