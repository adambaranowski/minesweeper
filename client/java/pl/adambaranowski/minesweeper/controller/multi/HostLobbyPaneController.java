package pl.adambaranowski.minesweeper.controller.multi;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.WebSocketConnector;
import pl.adambaranowski.minesweeper.utils.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HostLobbyPaneController extends SceneChanger {

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
    private Label roomNameLabel;

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

    private final String PLAYER_NAME_COLUMN = "Player";
    private final String PLAYER_TIME_COLUMN = "Time";
    private final String PLAYER_PERCENTAGE_COLUMN = "Board %";


    public void initialize() {
        updateMaxBombs();
        updateBombs();
        updateSize();
        updateTime();
        configureButtons();
        gameCounterLabel.setVisible(false);
        configureTableColumns();

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
                    items.add(new Player(o.get("username").toString(), o.getString("session_id").toString(), 0, 0));
                }
            }
        } catch (Exception e) {
            System.out.println("brak połaczenia");
            e.printStackTrace();
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
    }

    private void configureKickButton() {
        kickPlayerButton.setOnAction(actionEvent -> {
            try {

                if (playersTable.getSelectionModel().getSelectedItem() != null) {
                    JSONObject request = new JSONObject();
                    request.put("request", "KICK_PLAYER");
                    request.put("data", playersTable.getSelectionModel().getSelectedItem().getSessionId());

                    System.out.println(request.toString());
                    String response = WebSocketConnector.getInstance().dataTransfer(request.toString());
                    System.out.println(response);
                }


            } catch (IOException e) {
                e.printStackTrace();
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
        ;
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
        maxBombsCount = (int) Math.pow(Double.parseDouble(sizeValueLabel.getText()), 2) / 4;
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
