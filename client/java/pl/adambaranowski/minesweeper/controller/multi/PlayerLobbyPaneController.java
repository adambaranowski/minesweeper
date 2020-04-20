package pl.adambaranowski.minesweeper.controller.multi;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;
import pl.adambaranowski.minesweeper.utils.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerLobbyPaneController extends SceneChanger {

    @FXML
    private TableView<Player> playersTable;

    @FXML
    private Label gameCounterLabel;

    @FXML
    private Button leaveRoomButton;

    @FXML
    private Label roomNameLabel;

    private final String PLAYER_NAME_COLUMN = "Player";
    private final String PLAYER_TIME_COLUMN = "Time";
    private final String PLAYER_PERCENTAGE_COLUMN = "Board %";


    public void initialize() {
        configureLeaveRoomButton();
        configureTableColumns();
        updateTable();

        Thread refresh = new Thread(refreshingTask);
        refresh.setDaemon(true);
        refresh.start();
    }

    Task refreshingTask = new Task() {
        @Override
        protected Object call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }
                updateTable();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
            return null;
        }
    };

    private void configureTableColumns() {

        TableColumn<Player, String> playerNameColumn = new TableColumn<Player, String>(PLAYER_NAME_COLUMN);
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Player, String> playerTimeColumn = new TableColumn<Player, String>(PLAYER_TIME_COLUMN);
        playerTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Player, String> playerPercentageColumn = new TableColumn<Player, String>(PLAYER_PERCENTAGE_COLUMN);
        playerPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("boardPercentage"));

        playersTable.getColumns().add(playerNameColumn);
        playersTable.getColumns().add(playerTimeColumn);
        playersTable.getColumns().add(playerPercentageColumn);

    }

    private void updateTable() {
        ObservableList<Player> items = playersTable.getItems();

        JSONObject request = new JSONObject();
        request.put("request", "GET_INFO");
        System.out.println(request.toString());
        try {
            String message = WebSocketConnector.getInstance().dataTransfer(request.toString());
            System.out.println(message);
            JSONObject reply = new JSONObject(message);
            String status = reply.get("message").toString();

            if (status.equals("KICKED")) {


                refreshingTask.cancel();


                //https://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-not-on-fx-application-thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            changeToChooseOptionScene(leaveRoomButton);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } else {

                String data = reply.get("data").toString();

                JSONObject room_info = new JSONObject(data);

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

                if (playersInTableViewSessionId.size() != playersList.size()) {
                    items.clear();

                    for (JSONObject o : playersList
                    ) {
                        items.add(new Player(o.get("username").toString(), o.get("session_id").toString(), 0, 0));
                    }

                }
            }

        } catch (Exception e) {
            System.out.println("brak połaczenia");
            e.printStackTrace();
        }

    }

    private void configureLeaveRoomButton() {
        leaveRoomButton.setOnAction(actionEvent -> {
            try {
                refreshingTask.cancel();
                JSONObject request = new JSONObject();
                request.put("request", "LEAVE_ROOM");
                System.out.println(request);
                String message = WebSocketConnector.getInstance().dataTransfer(request.toString());
                System.out.println(message);
                changeToChooseOptionScene(leaveRoomButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
