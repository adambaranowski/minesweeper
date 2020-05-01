package pl.adambaranowski.minesweeper.controller.multi;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.RequestSender;
import pl.adambaranowski.minesweeper.functions.Scenes;
import pl.adambaranowski.minesweeper.functions.ScenesChanger;
import pl.adambaranowski.minesweeper.utils.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerLobbyPaneController implements ScenesChanger, RequestSender {

    @FXML
    private TableView<Player> playersTable;

    @FXML
    private Label gameCounterLabel;

    @FXML
    private Button leaveRoomButton;

    private final String PLAYER_NAME_COLUMN = "Player";
    private final String PLAYER_TOTAL_COLUMN = "Total";
    private final String PLAYER_SCORE_COLUMN = "Board %";

    Task refreshingTask;
    Thread refresh;

    public void initialize() {

        configureLeaveRoomButton();
        configureTableColumns();
        updateTable();
        startTask();
    }

    private void startTask() {
        newTask();
        refresh = new Thread(refreshingTask);
        refresh.setDaemon(true);
        refresh.start();
    }

    private void newTask() {
        refreshingTask = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    if (isCancelled()) {
                        break;
                    }
                    updateTable();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return null;
            }
        };
    }

    private void configureTableColumns() {

        TableColumn<Player, String> playerNameColumn = new TableColumn<Player, String>(PLAYER_NAME_COLUMN);
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Player, String> playerTotalColumn = new TableColumn<Player, String>(PLAYER_TOTAL_COLUMN);
        playerTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<Player, String> playerPercentageColumn = new TableColumn<Player, String>(PLAYER_SCORE_COLUMN);
        playerPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        playersTable.getColumns().add(playerNameColumn);
        playersTable.getColumns().add(playerPercentageColumn);
        playersTable.getColumns().add(playerTotalColumn);

    }

    private void updateTable() {
        ObservableList<Player> items = playersTable.getItems();

        JSONObject request = new JSONObject();
        request.put("request", "GET_INFO");

        String response = sendRequest(request.toString());
        JSONObject responseJson = new JSONObject(response);
        String status = responseJson.get("message").toString();

        if (status.equals("KICKED")) {

            refreshingTask.cancel();

            //https://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-not-on-fx-application-thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    changeScene(Scenes.MULTI_CHOOSE_OPTION_SCENE, leaveRoomButton);
                }
            });


        } else {

            String data = responseJson.get("data").toString();
            JSONObject room_info = new JSONObject(data);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gameCounterLabel.setText(room_info.get("start_in").toString());
                }
            });

            JSONArray playersArray = new JSONArray(room_info.get("room_members").toString());
            ArrayList<JSONObject> playersList = new ArrayList<>();

            for (int i = 0; i < playersArray.length(); i++) {
                playersList.add(playersArray.getJSONObject(i));
            }
            List<String> playersInTableViewSessionId = new ArrayList<>();
            for (Player player : items
            ) {
                playersInTableViewSessionId.add(player.getSessionId());
            }

            //try catch because server not always send score and total score
            boolean changedScore = false;
            int i = 0;
            for (JSONObject o : playersList) {
                try {
                    if (items.get(i).getScore() != Math.round(o.getDouble("score") * 100) ||
                            items.get(i).getTotal() != Math.round(o.getDouble("total_score") * 100)
                    ) {
                        changedScore = true;
                    }

                    i++;
                } catch (Exception e) {
                }
            }

            if (playersInTableViewSessionId.size() != playersList.size() || changedScore) {
                items.clear();

                for (JSONObject o : playersList
                ) {
                    try {
                        items.add(new Player(o.get("username").toString(), o.get("session_id").toString(), Math.round(o.getDouble("score") * 100), Math.round(o.getDouble("total_score") * 100)));
                    } catch (JSONException e) {
                        items.add(new Player(o.get("username").toString(), o.get("session_id").toString(), 0, 0));
                    }
                }
            }

            if (!gameCounterLabel.getText().equals("999")) {
                gameCounterLabel.setVisible(true);
            }
            if (gameCounterLabel.getText().equals("0")) {
                refreshingTask.cancel();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        changeToMultiPlayerGameScene();

                    }
                });
            }
        }
    }

    private void configureLeaveRoomButton() {
        leaveRoomButton.setOnAction(actionEvent -> {

            refreshingTask.cancel();
            JSONObject request = new JSONObject();
            request.put("request", "LEAVE_ROOM");

            sendRequest(request.toString());

            changeScene(Scenes.MULTI_CHOOSE_OPTION_SCENE, leaveRoomButton);
        });
    }

    private void changeToMultiPlayerGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(Scenes.MULTI_GAME_SCENE.getFxmlPath()));

            Parent multiPlayerGameParent = loader.load();
            Scene multiPlayerGameScene = new Scene(multiPlayerGameParent);

            //access the method initData to put board size and bombs count
            MultiPlayerGamePaneController multiPlayerGamePaneController = loader.getController();
            multiPlayerGamePaneController.initData(false);

            Stage window = (Stage) (leaveRoomButton.getScene().getWindow());
            window.setScene(multiPlayerGameScene);
            window.show();
            window.centerOnScreen();

        } catch (IOException e) {
            System.out.println();
            System.err.println("Error while changing scene to Game Scene. Probably fxml loading fail");
            e.printStackTrace();
            System.out.println();
        }
    }
}
