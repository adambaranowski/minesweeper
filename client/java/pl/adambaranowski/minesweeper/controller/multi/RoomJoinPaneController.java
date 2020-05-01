package pl.adambaranowski.minesweeper.controller.multi;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.RequestSender;
import pl.adambaranowski.minesweeper.functions.Scenes;
import pl.adambaranowski.minesweeper.functions.ScenesChanger;
import pl.adambaranowski.minesweeper.utils.Room;


import java.util.ArrayList;
import java.util.List;

public class RoomJoinPaneController implements ScenesChanger, RequestSender {
    @FXML
    private Button joinRoomButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<Room> roomsTable;

    Task refreshingTask;

    private final String ROOM_NAME_COLUMN = "Room";
    private final String PLAYERS_COLUMN = "Players";
    private Integer roomId = 7959;

    private Thread refresh;

    public void initialize() {
        configureTableColumns();
        configureBackButton();
        configureJoinButton();
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
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
                return null;
            }
        };
    }


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

        String response = sendRequest(request.toString());
        JSONObject allRooms = new JSONObject(response);
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
    }

    private void configureJoinButton() {
        joinRoomButton.setOnAction(actionEvent -> {

            refreshingTask.cancel();
            if (roomsTable.getSelectionModel().getSelectedItem() != null) {
                if (roomsTable.getSelectionModel().getSelectedItem().getPlayers() < roomsTable.getSelectionModel().getSelectedItem().getMaxPlayers()) {
                    roomId = roomsTable.getSelectionModel().getSelectedItem().getRoomId();


                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        //no need to catch, its only to avoid json crashing(two responses from server can come together)
                    }

                    JSONObject request = new JSONObject();
                    request.put("request", "JOIN_ROOM");
                    request.put("data", roomId);
                    String response = sendRequest(request.toString());
                    JSONObject responseJson = new JSONObject(response);
                    if (responseJson.get("message").equals("OK")) {
                        changeScene(Scenes.MULTI_PLAYER_LOBBY_SCENE, joinRoomButton);
                    }
                }
            }
        });
    }

    private void configureBackButton() {
        backButton.setOnAction(actionEvent -> {
            refreshingTask.cancel();
            changeScene(Scenes.MULTI_CHOOSE_OPTION_SCENE, backButton);
        });
    }
}