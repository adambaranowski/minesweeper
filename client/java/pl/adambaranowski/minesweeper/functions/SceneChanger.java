package pl.adambaranowski.minesweeper.functions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneChanger {

    public void changeToAboutScene(Button button) throws IOException {
        Parent aboutSceneParent = FXMLLoader.load(getClass().getResource("/fxml/about/aboutPane.fxml"));
        Scene aboutScene = new Scene(aboutSceneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(aboutScene);
        window.show();

    }

    public void changeToSinglePlayerScene(Button button) throws IOException {
        Parent singlePlayerSceneParent = FXMLLoader.load(getClass().getResource("/fxml/single/singlePlayerStartPane.fxml"));
        Scene singlePlayerScene = new Scene(singlePlayerSceneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(singlePlayerScene);
        window.show();

    }

    public void changeToMultiPlayerLoginScene(Button button) throws IOException {
        Parent multiPlayerSceneParent = FXMLLoader.load(getClass().getResource("/fxml/multi/multiPlayerLoginPane.fxml"));
        Scene multiPlayerScene = new Scene(multiPlayerSceneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(multiPlayerScene);
        window.show();

    }

    public void changeToMenuScene(Button button) throws IOException {
        Parent startPaneParent = FXMLLoader.load(getClass().getResource("/fxml/menu/startPane.fxml"));
        Scene startPaneScene = new Scene(startPaneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(startPaneScene);
        window.show();

    }

    public void changeToJoinRoomScene(Button button) throws IOException {
        Parent joinRoomPaneParent = FXMLLoader.load(getClass().getResource("/fxml/multi/multiPlayerRoomJoinPane.fxml"));
        Scene joinRoomPaneScene = new Scene(joinRoomPaneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(joinRoomPaneScene);
        window.show();

    }

    public void changeToCreateRoomScene(Button button) throws IOException {
        Parent createRoomPaneParent = FXMLLoader.load(getClass().getResource("/fxml/multi/multiPlayerCreateRoomPane.fxml"));
        Scene createRoomPaneScene = new Scene(createRoomPaneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(createRoomPaneScene);
        window.show();

    }

    public void changeToHostLobbyScene(Button button) throws IOException {
        Parent hostLobbyPaneParent = FXMLLoader.load(getClass().getResource("/fxml/multi/multiPlayerHostLobbyPane.fxml"));
        Scene hostLobbyPaneScene = new Scene(hostLobbyPaneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(hostLobbyPaneScene);
        window.show();
    }

    public void changeToPlayerLobbyScene(Button button) throws IOException {
        Parent playerLobbyPaneParent = FXMLLoader.load(getClass().getResource("/fxml/multi/multiPlayerPlayerLobbyPane.fxml"));
        Scene playerLobbyPaneScene = new Scene(playerLobbyPaneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(playerLobbyPaneScene);
        window.show();
    }

    public void changeToChooseOptionScene(Button button) throws IOException {
        Parent chooseOptionPaneParent = FXMLLoader.load(getClass().getResource("/fxml/multi/multiPlayerChooseOptionPane.fxml"));
        Scene chooseOptionPaneScene = new Scene(chooseOptionPaneParent);

        Stage window = (Stage) (button.getScene().getWindow());
        window.setScene(chooseOptionPaneScene);
        window.show();
    }

}
