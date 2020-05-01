package pl.adambaranowski.minesweeper.controller.about;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.adambaranowski.minesweeper.functions.Scenes;
import pl.adambaranowski.minesweeper.functions.ScenesChanger;


public class AboutPaneController implements ScenesChanger {

    @FXML
    private Button toMenuButton;

    public void initialize() {
        configureButtons();
    }

    private void configureButtons(){
        configureToMenuButton();
    }

    private void configureToMenuButton() {
        toMenuButton.setOnAction(actionEvent -> {
            changeScene(Scenes.MENU_SCENE, toMenuButton);
        });
    }
}
