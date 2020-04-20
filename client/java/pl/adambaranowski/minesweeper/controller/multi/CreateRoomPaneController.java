package pl.adambaranowski.minesweeper.controller.multi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.StringValidator;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;

import java.io.IOException;

public class CreateRoomPaneController extends SceneChanger {

    @FXML
    private TextField roomNameTextField;

    @FXML
    private Label playersLimitLabel;

    @FXML
    private Button playersLimitIncrButton;

    @FXML
    private Button playersLimitDcrButton;

    @FXML
    private Button createRoomButton;

    @FXML
    private Button backButton;

    private final int MAX_PLAYERS = 8;
    private int playersLimit;

    public void initialize() {
        configureBackButton();
        configurePlayersLimitDcrButton();
        configurePlayersLimitIncButton();
        configureCreateButton();
        updatePlayersLimit();

    }

    private void configurePlayersLimitIncButton() {
        playersLimitIncrButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(playersLimitLabel.getText()) < MAX_PLAYERS) {
                playersLimitLabel.setText(String.valueOf(Integer.parseInt(playersLimitLabel.getText()) + 1));
                updatePlayersLimit();

            }
        });
    }

    private void configurePlayersLimitDcrButton() {
        playersLimitDcrButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(playersLimitLabel.getText()) > 2) {
                playersLimitLabel.setText(String.valueOf(Integer.parseInt(playersLimitLabel.getText()) - 1));
                updatePlayersLimit();

            }
        });
    }

    private void configureCreateButton() {
        createRoomButton.setOnAction(actionEvent -> {
            if (roomNameValidation()) {
                try {

                    if (StringValidator.getInstance().checkTextCorrect(roomNameTextField.getText())) {

                        JSONObject room = new JSONObject();
                        JSONObject roomParameteres = new JSONObject();

                        roomParameteres.put("name", roomNameTextField.getText());
                        roomParameteres.put("size", playersLimit);

                        room.put("request", "CREATE_ROOM");
                        room.put("data", roomParameteres);

                        System.out.println(room.toString());
                        String message = WebSocketConnector.getInstance().dataTransfer(room.toString());
                        System.out.println(message);
                        changeToHostLobbyScene(createRoomButton);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void configureBackButton() {
        backButton.setOnAction(actionEvent -> {
            try {
                changeToChooseOptionScene(backButton);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updatePlayersLimit() {
        playersLimit = Integer.parseInt(playersLimitLabel.getText());
    }

    private boolean roomNameValidation() {
        if (!roomNameTextField.getText().equals("")) {
            return true;
        } else {
            return false;
        }

    }
}