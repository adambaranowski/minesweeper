package pl.adambaranowski.minesweeper.controller.multi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.StringValidator;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;

import java.io.IOException;
import java.util.Random;

public class LoginPaneController extends SceneChanger {

    @FXML
    private TextField nickTextField;

    @FXML
    private TextField serverTextField;

    @FXML
    private Button logInButton;

    @FXML
    private Button toMenuButton;

    private String serverAddress = "";

    public void initialize() {

        configureToMenuButton();
        configureLogInButton();
        randomPlayerNick();
        serverAddress = serverTextField.getText();
    }


    private void configureLogInButton() {
        logInButton.setOnAction(actionEvent -> {

            if (StringValidator.getInstance().checkTextCorrect(nickTextField.getText()) &&
                    StringValidator.getInstance().checkTextCorrect(serverTextField.getText())) {

                JSONObject user = new JSONObject();
                user.put("username", nickTextField.getText());
                System.out.println(user.toString());

                serverAddress = serverTextField.getText();
                try {
                    String message = WebSocketConnector.createInstance(serverAddress, 1337).dataTransfer(user.toString());
                    System.out.println(message);
                    changeToChooseOptionScene(logInButton);

                } catch (Exception e) {
                    System.out.println("brak połączenia");
                    e.printStackTrace();
                }

            }

        });
    }

    private void configureToMenuButton() {
        toMenuButton.setOnAction(actionEvent -> {
            try {
                changeToMenuScene(toMenuButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void randomPlayerNick() {
        Random random = new Random();
        nickTextField.setText("Puciak" + random.nextInt(10_000));
    }

}