package pl.adambaranowski.minesweeper.functions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public interface ScenesChanger {

    default void changeScene(Scenes scene, Button button) {
        try {

            Parent sceneParent = FXMLLoader.load(getClass().getResource(scene.getFxmlPath()));
            Scene newScene = new Scene(sceneParent);

            Stage window = (Stage) (button.getScene().getWindow());

            window.setScene(newScene);
            window.show();
            window.centerOnScreen();

        } catch (IOException e) {
            System.out.println();
            System.err.println("Error while changing scene to" + scene.name() + "Probably fxml loading fail");
            e.printStackTrace();
            System.out.println();
        }
    }
}
