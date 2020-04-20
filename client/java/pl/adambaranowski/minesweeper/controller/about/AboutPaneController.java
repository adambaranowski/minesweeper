package pl.adambaranowski.minesweeper.controller.about;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.adambaranowski.minesweeper.functions.SceneChanger;

import java.io.IOException;

public class AboutPaneController extends SceneChanger {

    @FXML
    private Button toMenuButton;

    public void initialize() {
        configureToMenuButton();
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
}
