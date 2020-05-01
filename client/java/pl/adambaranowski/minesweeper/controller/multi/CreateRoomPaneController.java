package pl.adambaranowski.minesweeper.controller.multi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.*;

public class CreateRoomPaneController implements ScenesChanger, RequestSender {

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
            if (StringValidator.getInstance().checkTextCorrectness(roomNameTextField.getText())) {
                JSONObject request = new JSONObject();
                JSONObject roomParametres = new JSONObject();

                roomParametres.put("name", roomNameTextField.getText());
                roomParametres.put("size", playersLimit);

                request.put("request", "CREATE_ROOM");
                request.put("data", roomParametres);

                sendRequest(request.toString());

                changeScene(Scenes.MULTI_HOST_LOBBY_SCENE, createRoomButton);

            }
        });
    }

    private void configureBackButton() {
        backButton.setOnAction(actionEvent -> {
            changeScene(Scenes.MULTI_CHOOSE_OPTION_SCENE, backButton);
        });
    }

    private void updatePlayersLimit() {
        playersLimit = Integer.parseInt(playersLimitLabel.getText());
    }
}