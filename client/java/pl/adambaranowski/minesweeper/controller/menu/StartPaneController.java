package pl.adambaranowski.minesweeper.controller.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.adambaranowski.minesweeper.functions.Scenes;
import pl.adambaranowski.minesweeper.functions.ScenesChanger;


public class StartPaneController implements ScenesChanger {

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
            changeScene(Scenes.SINGLE_START_SCENE, singlePlayerButton);
        });
    }

    private void configureMultiPlayerButton() {
        multiPlayerButton.setOnAction(actionEvent -> {
            changeScene(Scenes.MULTI_LOGIN_SCENE, multiPlayerButton);
        });
    }

    private void configureAboutButton() {
        aboutButton.setOnAction(actionEvent -> {
            changeScene(Scenes.ABOUT_SCENE, aboutButton);
        });

    }
}