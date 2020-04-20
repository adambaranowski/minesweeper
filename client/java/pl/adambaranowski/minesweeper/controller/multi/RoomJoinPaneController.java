package pl.adambaranowski.minesweeper.controller.multi;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;
import pl.adambaranowski.minesweeper.utils.Player;
import pl.adambaranowski.minesweeper.utils.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomJoinPaneController extends SceneChanger {
    @FXML
    private Button joinRoomButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<Room> roomsTable;

    private final String ROOM_NAME_COLUMN = "Room";
    private final String PLAYERS_COLUMN = "Players";
    private Integer roomId = 7959;

    public void initialize() {
        configureTableColumns();
        configureBackButton();
        configureJoinButton();
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
                    e.printStackTrace();
                }
            }
            return null;
        }

    };

    private void configureTableColumns() {

        TableColumn<Room, String> roomNameColumn = new TableColumn<Room, String>(ROOM_NAME_COLUMN);
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Room, String> roomPlayersColumn = new TableColumn<Room, String>(PLAYERS_COLUMN);
        roomPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("playersString"));
        roomsTable.getColumns().add(roomNameColumn);
        roomsTable.getColumns().add(roomPlayersColumn);

    }


    private void updateTable() {
        ObservableList<Room> items = roomsTable.getItems();
        JSONObject request = new JSONObject();
        request.put("request", "ALL_ROOMS");
        System.out.println(request.toString());
        try {

            String message = WebSocketConnector.getInstance().dataTransfer(request.toString());
            System.out.println(message);
            JSONObject allRooms = new JSONObject(message);
            JSONArray roomsArray = new JSONArray(allRooms.get("data").toString());
            ArrayList<JSONObject> roomsList = new ArrayList<>();

            for (int i = 0; i < roomsArray.length(); i++) {
                roomsList.add(roomsArray.getJSONObject(i));
            }

            List<Integer> roomsInTableViewId = new ArrayList<>();

            for (Room room : items
            ) {
                roomsInTableViewId.add(room.getRoomId());
            }

            if (roomsInTableViewId.size() != roomsList.size()) {
                items.clear();
                for (JSONObject o : roomsList
                ) {
                    items.add(new Room(Integer.parseInt(o.get("room_id").toString()), o.get("room_name").toString(), Integer.parseInt(o.get("max_players").toString()), Integer.parseInt(o.get("players").toString())));
                }
            }

        } catch (Exception e) {
            System.out.println("brak połaczenia");
            e.printStackTrace();
        }
    }

    private void configureJoinButton() {
        joinRoomButton.setOnAction(actionEvent -> {
            try {
                refreshingTask.cancel();
                if (roomsTable.getSelectionModel().getSelectedItem() != null) {
                    if (roomsTable.getSelectionModel().getSelectedItem().getPlayers() < roomsTable.getSelectionModel().getSelectedItem().getMaxPlayers()) {
                        roomId = roomsTable.getSelectionModel().getSelectedItem().getRoomId();

                        JSONObject request = new JSONObject();
                        request.put("request", "JOIN_ROOM");
                        request.put("data", roomId);
                        System.out.println(request);
                        String response = WebSocketConnector.getInstance().dataTransfer(request.toString());
                        System.out.println(request);

                        JSONObject responseJson = new JSONObject(response);
                        if (responseJson.get("message").equals("OK")) {
                            changeToPlayerLobbyScene(joinRoomButton);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureBackButton() {
        backButton.setOnAction(actionEvent -> {
            try {
                refreshingTask.cancel();
                changeToChooseOptionScene(backButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}