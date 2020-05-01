package pl.adambaranowski.minesweeper.controller.multi;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.board.Board;
import pl.adambaranowski.minesweeper.functions.RequestSender;
import pl.adambaranowski.minesweeper.functions.Scenes;
import pl.adambaranowski.minesweeper.functions.ScenesChanger;
import pl.adambaranowski.minesweeper.utils.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HostLobbyPaneController implements ScenesChanger, RequestSender {

    @FXML
    private Label bombsCountLabel;

    @FXML
    private Button bombsIncButton;

    @FXML
    private Button bombsDcrButton;

    @FXML
    private Label sizeValueLabel;

    @FXML
    private Label timeValueLabel;

    @FXML
    private Button sizeIncButton;

    @FXML
    private Button sizeDcrButton;

    @FXML
    private Button timeDcrButton;

    @FXML
    private Button timeIncButton;

    @FXML
    private Button kickPlayerButton;

    @FXML
    private Button startGameButton;

    @FXML
    private Button leaveRoomButton;

    @FXML
    private Slider bombsSlider;

    @FXML
    private Slider sizeSlider;

    @FXML
    private Slider timeSlider;

    @FXML
    private Label gameCounterLabel;


    @FXML
    private TableView<Player> playersTable;


    private int bombsCount;
    private int size;
    private int time;

    private final int MAX_SIZE = 20;
    private final int MIN_SIZE = 7;

    private final int MIN_TIME = 10;
    private final int MAX_TIME = 180;

    private int maxBombsCount;
    private final int MIN_BOMBS_COUNT = 1;

    Thread refresh;
    Task refreshingTask;

    private final String PLAYER_NAME_COLUMN = "Player";
    private final String PLAYER_TOTAL_COLUMN = "Total %";
    private final String PLAYER_SCORE_COLUMN = "Board %";


    public void initialize() {
        updateMaxBombs();
        updateBombs();
        updateSize();
        updateTime();
        configureButtons();
        gameCounterLabel.setVisible(false);
        configureTableColumns();

        startTask();
    }


    private void startTask() {
        newTask();
        refresh = new Thread(refreshingTask);
        refresh.setDaemon(true);
        refresh.start();
    }

    private void stopTask() {
        refreshingTask.cancel();
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
        String data = responseJson.get("data").toString();
        JSONObject room_info = new JSONObject(data);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    gameCounterLabel.setText(room_info.get("start_in").toString());
                } catch (Exception e) {}
            }
        });

        if (!gameCounterLabel.getText().equals("999")) {
            gameCounterLabel.setVisible(true);
        }

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

        boolean changedScore = false;
        int i = 0;
        for (JSONObject o : playersList) {
            //try catch because server not always send score and total_score
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
                    items.add(new Player(o.get("username").toString(), o.get("session_id").toString(), Math.round(o.getDouble("score") * 100), Math.round(o.getDouble("total_score") * 100)));//Double.parseDouble(o.get("score").toString())*100 , Double.parseDouble(o.get("total_score").toString())*100));
                } catch (JSONException e) {
                    e.printStackTrace();
                    items.add(new Player(o.get("username").toString(), o.get("session_id").toString(), 0, 0));
                }
            }
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

    private void configureButtons() {
        configureSizeDcrButton();
        configureSizeIncButton();
        configureBombDcrButton();
        configureBombIncButton();
        configureLeaveRoomButton();
        configureTimeDcrButton();
        configureTimeIncButton();
        configureTimeSlider();
        configureSizeSlider();
        configureBombsSlider();
        configureKickButton();
        configureStartGameButton();
    }

    private void configureStartGameButton() {
        startGameButton.setOnAction(actionEvent -> {

            stopTask();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //no need to catch, its only to avoid json crashing(two responses from server can come together)
            }

            JSONObject request = new JSONObject();
            request.put("request", "SET_SIZE");
            request.put("data", Integer.parseInt(sizeValueLabel.getText()));

            sendRequest(request.toString());

            request = new JSONObject();
            request.put("request", "SET_TIME");
            request.put("data", Integer.parseInt(timeValueLabel.getText()));

            sendRequest(request.toString());

            Board board = new Board(Integer.parseInt(sizeValueLabel.getText()), Integer.parseInt(bombsCountLabel.getText()));
            Integer[][] tableInt = board.getBoardInteger();

            request = new JSONObject();
            request.put("request", "START_GAME");

            JSONArray boardArray = new JSONArray();
            JSONArray row = new JSONArray();

            //getting start point and generating map
            //searching empty fields, emptySquareCounter helps to not get first empty field

            int startX = 0;
            int startY = 0;
            int emptySquareCounter = 0;
            int allEmptySquareCounter = 0;

            for (int i = 0; i < tableInt[0].length; i++) {
                for (int j = 0; j < tableInt[0].length; j++) {


                    if (tableInt[i][j]==0) {
                        allEmptySquareCounter++;
                    }
                }
            }


            for (int i = 0; i < tableInt[0].length; i++) {
                for (int j = 0; j < tableInt[0].length; j++) {

                    row.put(tableInt[i][j]);

                    if (tableInt[i][j]==0) {
                        emptySquareCounter++;
                        if (emptySquareCounter > allEmptySquareCounter / 3 && startX == 0 && startY == 0) {
                            startX = i;
                            startY = j;
                        }
                    }
                }
                boardArray.put(row);
                row = new JSONArray();
            }

            JSONObject data = new JSONObject();
            data.put("map", boardArray);

            data.put("start_point", "[" + startX + "," + startY + "]");
            request.put("data", data);

            String response = sendRequest(request.toString());
            JSONObject responseJson = new JSONObject(response);
            if (!responseJson.getString("message").equals("INCORRECT DATA FORMAT")) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startGameButton.setDisable(true);
            }

            startTask();

        });
    }

    private void configureKickButton() {
        kickPlayerButton.setOnAction(actionEvent -> {
            if (playersTable.getSelectionModel().getSelectedItem() != null) {

                stopTask();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    //no need to catch, its only to avoid json crashing(two responses from server can come together)
                }

                JSONObject request = new JSONObject();
                request.put("request", "KICK_PLAYER");


                request.put("data", playersTable.getSelectionModel().getSelectedItem().getSessionId());
                //String response =
                String response = sendRequest(request.toString());
                JSONObject responseJson = new JSONObject(response);
                if (!responseJson.getString("message").equals("INCORRECT DATA FORMAT")) {
                    startGameButton.setDisable(true);
                }
                startTask();

            }
        });
    }

    private void configureBombsSlider() {
        bombsSlider.setBlockIncrement(1);
        bombsSlider.setMin(MIN_BOMBS_COUNT);
        bombsSlider.setMax(maxBombsCount);
        bombsSlider.setValue(bombsCount);
        bombsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            bombsCountLabel.setText(String.valueOf(newValue.intValue()));
            updateBombs();
        });
    }


    private void configureSizeSlider() {
        sizeSlider.setBlockIncrement(1);
        sizeSlider.setMin(MIN_SIZE);
        sizeSlider.setMax(MAX_SIZE);
        sizeSlider.setValue(size);

        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sizeValueLabel.setText(String.valueOf(newValue.intValue()));
            updateSize();
            updateMaxBombs();
            bombsSlider.setMax(maxBombsCount);
        });
    }

    private void configureTimeSlider() {
        timeSlider.setBlockIncrement(1);
        timeSlider.setMin(MIN_TIME);
        timeSlider.setMax(MAX_TIME);
        timeSlider.setValue(time);

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            timeValueLabel.setText(String.valueOf(newValue.intValue()));
        });
    }

    private void updateBombs() {
        bombsCount = Integer.parseInt(bombsCountLabel.getText());
    }


    private void updateSize() {
        size = Integer.parseInt(sizeValueLabel.getText());
    }

    private void updateTime() {
        time = Integer.parseInt(timeValueLabel.getText());
    }

    private void updateMaxBombs() {
        updateBombs();
        maxBombsCount = (int) Math.pow(Double.parseDouble(sizeValueLabel.getText()), 2) / 6;
        if (bombsCount > maxBombsCount) {
            bombsCount = maxBombsCount;
            bombsCountLabel.setText(String.valueOf(bombsCount));
        }
    }

    private void configureSizeIncButton() {
        sizeIncButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(sizeValueLabel.getText()) < MAX_SIZE) {
                sizeValueLabel.setText(String.valueOf(Integer.parseInt(sizeValueLabel.getText()) + 1));
                updateSize();
                updateMaxBombs();
                sizeSlider.setValue(size);
                bombsSlider.setMax(maxBombsCount);
            }
        });
    }

    private void configureSizeDcrButton() {
        sizeDcrButton.setOnAction(actionEvent -> {

            if (Integer.parseInt(sizeValueLabel.getText()) > MIN_SIZE) {
                sizeValueLabel.setText(String.valueOf(Integer.parseInt(sizeValueLabel.getText()) - 1));
                updateSize();
                updateMaxBombs();
                bombsSlider.setMax(maxBombsCount);
                sizeSlider.setValue(size);
            }
        });
    }

    private void configureBombIncButton() {
        bombsIncButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(bombsCountLabel.getText()) < maxBombsCount) {
                bombsCountLabel.setText(String.valueOf(Integer.parseInt(bombsCountLabel.getText()) + 1));
                updateBombs();
                bombsSlider.setValue(bombsCount);
            }
        });
    }

    private void configureBombDcrButton() {
        bombsDcrButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(bombsCountLabel.getText()) > MIN_BOMBS_COUNT) {
                bombsCountLabel.setText(String.valueOf(Integer.parseInt(bombsCountLabel.getText()) - 1));
                updateBombs();
                bombsSlider.setValue(bombsCount);
            }
        });
    }

    private void configureTimeIncButton() {
        timeIncButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(timeValueLabel.getText()) < MAX_TIME) {
                timeValueLabel.setText(String.valueOf(Integer.parseInt(timeValueLabel.getText()) + 1));
                updateTime();
                timeSlider.setValue(time);
            }
        });
    }

    private void configureTimeDcrButton() {
        timeDcrButton.setOnAction(actionEvent -> {
            if (Integer.parseInt(timeValueLabel.getText()) > MIN_TIME) {
                timeValueLabel.setText(String.valueOf(Integer.parseInt(timeValueLabel.getText()) - 1));
                updateTime();
                timeSlider.setValue(time);
            }
        });
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
            multiPlayerGamePaneController.initData(true);

            Stage window = (Stage) (startGameButton.getScene().getWindow());
            window.setScene(multiPlayerGameScene);
            window.show();
            window.centerOnScreen();
        } catch (IOException e) {
            System.out.println();
            System.err.println("Error while changing scene to Game Scene. Probably fxml loading fail");
            e.printStackTrace();
            System.out.println();
            startGameButton.fire();
        }
    }
}
