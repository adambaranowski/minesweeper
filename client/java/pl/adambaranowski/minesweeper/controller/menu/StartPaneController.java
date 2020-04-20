package pl.adambaranowski.minesweeper.controller.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.adambaranowski.minesweeper.functions.SceneChanger;

import java.io.IOException;

public class StartPaneController extends SceneChanger {

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

    @FXML
    private Button aboutButton;

    public void initialize() {
        configureButtons();
    }

    private void configureButtons() {
        configureSinglePlayerButton();
        configureMultiPlayerButton();
        configureAboutButton();
    }

    private void configureSinglePlayerButton() {
        singlePlayerButton.setOnAction(actionEvent -> {
            try {
                changeToSinglePlayerScene(singlePlayerButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureMultiPlayerButton() {
        multiPlayerButton.setOnAction(actionEvent -> {
            try {
                changeToMultiPlayerLoginScene(multiPlayerButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureAboutButton() {
        aboutButton.setOnAction(actionEvent -> {
            try {
                changeToAboutScene(aboutButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}