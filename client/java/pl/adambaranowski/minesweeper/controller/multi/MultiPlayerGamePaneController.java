package pl.adambaranowski.minesweeper.controller.multi;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.adambaranowski.minesweeper.board.Board;
import pl.adambaranowski.minesweeper.functions.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MultiPlayerGamePaneController implements ScenesChanger, RequestSender {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressNumber;

    @FXML
    private Label timerLabel;

    @FXML
    private Button toMenuButton;

    @FXML
    private VBox playersStats;

    Task refreshingTask;
    Thread refresh;

    private HashMap<String, ToggleButton> buttons;
    private HashMap<String, ProgressBar> playersProgressBars;
    private HashMap<String, Label> playersProgressLabels;

    private Integer boardArray[][];

    private int size;
    private final static int SQUARE_SIZE = 25;
    private boolean host;
    private int startX;
    private int startY;

    private Board board;


    public void initialize() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        //wysyłanie zapytania żeby przerwać petle na serwerze
        JSONObject request = new JSONObject();
        request.put("request", "GET_INFO");

        String response = sendRequest(request.toString());

        try {


            request = new JSONObject();
            request.put("request", "GET_MAP");

            response = sendRequest(request.toString());


            JSONObject responseJson = new JSONObject(response);

            if(!responseJson.getString("message").equals("OK")){
                Thread.sleep(100);

                request = new JSONObject();
                request.put("request", "GET_INFO");
                response = sendRequest(request.toString());

                request = new JSONObject();
                request.put("request", "GET_MAP");
                response = sendRequest(request.toString());
                responseJson = new JSONObject(response);
            }


            String data = responseJson.get("data").toString();







            JSONObject dataJson = new JSONObject(data);

            size = Integer.parseInt(dataJson.get("size").toString());
            boardArray = new Integer[size][size];

            JSONArray boardJsonArray = new JSONArray(dataJson.get("map").toString());
            JSONArray row;

            JSONArray startPoint = new JSONArray(dataJson.get("start_point").toString());

            startX = startPoint.getInt(0);
            startY = startPoint.getInt(1);

            for (int i = 0; i < boardArray.length; i++) {
                row = new JSONArray(boardJsonArray.get(i).toString());
                for (int j = 0; j < boardArray[0].length; j++) {
                    boardArray[i][j] = row.getInt(j);
                }
            }

        } catch (Exception e) {
            System.out.println("nie udało się pobrać mapy");
            e.printStackTrace();
        }
        addButtons();

        this.board = new Board(size, buttons, boardArray);
        buttons.get(startX + " " + startY).setStyle("-fx-background-color: lightgreen");
        buttons.get(startX + " " + startY).setSelected(true);


        timerLabel.setVisible(true);
        startTask();
    }

    public void initData(boolean host) {
        this.host = host;
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
                    updateInfo();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return null;
            }
        };
    }


    private void updateInfo() {

        JSONObject request = new JSONObject();
        request.put("request", "UPDATE_SCORE");

        request.put("data", (double) Math.round(board.unhideBoardPercentage * 10000) / 10000.0);

        String response = sendRequest(request.toString());

        request = new JSONObject();
        request.put("request", "GET_INFO");

        response = sendRequest(request.toString());

        JSONObject responseJson = new JSONObject(response);
        String data = responseJson.get("data").toString();
        JSONObject infoJson = new JSONObject(data);

        JSONArray playersArray = new JSONArray(infoJson.get("room_members").toString());

        ArrayList<JSONObject> playersList = new ArrayList<>();

        for (int i = 0; i < playersArray.length(); i++) {
            playersList.add(playersArray.getJSONObject(i));
        }
        //create labels

        if (playersProgressBars == null && playersProgressLabels == null) {

            System.out.println("initialisation of bars");
            playersProgressLabels = new HashMap<>();
            playersProgressBars = new HashMap<>();
            //playersScore = new HashMap<>();

            for (JSONObject player : playersList) {
                double score = Double.parseDouble(player.get("score").toString());
                String progressBarFxId = player.getString("username") + "ProgressBar";
                String progressLabelFxId = player.getString("username") + "ProgressLabel";

                ProgressBar progressBar = new ProgressBar();
                progressBar.setId(progressBarFxId);
                progressBar.setProgress(score);
                progressBar.setPadding(new Insets(0, 0, 30, 0));

                Label progressLabel = new Label();
                progressLabel.setId(progressLabelFxId);
                progressLabel.setText(player.getString("username") + ": " + Math.round(score * 10000) / 100 + "%");

                Platform.runLater(() -> {
                    playersProgressBars.put(progressBarFxId, progressBar);
                    playersProgressLabels.put(progressLabelFxId, progressLabel);

                    playersStats.getChildren().add(progressLabel);
                    playersStats.getChildren().add(progressBar);

                    if(infoJson.get("time_left").toString().equals("-1")){
                        timerLabel.setVisible(false);
                    }
                    timerLabel.setText(infoJson.get("time_left").toString());
                });
            }
        } else {
            for (JSONObject player : playersList) {
                double score = Double.parseDouble(player.get("score").toString());
                String progressBarFxId = player.getString("username") + "ProgressBar";
                String progressLabelFxId = player.getString("username") + "ProgressLabel";

                Platform.runLater(() -> {
                    timerLabel.setText(infoJson.get("time_left").toString());

                    playersProgressLabels.get(progressLabelFxId).setText(player.getString("username") + ": " + Math.round(score * 100) + "%");
                    playersProgressBars.get(progressBarFxId).setProgress(score);

                });
            }

            Platform.runLater(() -> {
                if (infoJson.getInt("time_left")<=0) {
                    refreshingTask.cancel();

                    if (host) {
                        changeScene(Scenes.MULTI_HOST_LOBBY_SCENE, toMenuButton);
                    } else {
                        changeScene(Scenes.MULTI_PLAYER_LOBBY_SCENE, toMenuButton);
                    }
                }
            });

        }
    }

    private void addButtons() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        String buttonId;
        buttons = new HashMap<>();

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {
                ToggleButton toggleButton = new ToggleButton();
                //i=row, j=position in row
                buttonId = i + " " + j;
                toggleButton.setId(buttonId);
                toggleButton.setMinSize(SQUARE_SIZE, SQUARE_SIZE);
                toggleButton.setMaxSize(SQUARE_SIZE, SQUARE_SIZE);

                //final because lambda require this
                int finalI = i;
                int finalJ = j;

                toggleButton.setOnMouseClicked(mouseEvent -> {

                    MouseButton mouseButton = mouseEvent.getButton();
                    if (mouseButton == MouseButton.PRIMARY) {


                        if (board.getBoard()[finalI][finalJ].getStatus().equals("o")) {
                            board.unhideButtonsWhenLose();
                            lose();

                        } else {
                            board.click(finalI, finalJ);
                            toggleButton.setDisable(true);
                            progressNumber.setText(Math.round(board.unhideBoardPercentage * 100) + "%");
                            progressBar.setProgress(board.unhideBoardPercentage);
                        }
                        board.printBoard();
                    }
                    if (mouseButton == MouseButton.SECONDARY) {
                        toggleButton.setText("P");
                    }
                });
                //inverted because in gridPane: j=column  i=row
                gridPane.add(toggleButton, j, i);
                buttons.put(buttonId, toggleButton);
            }
        }
        gridPane.setCursor(Cursor.DEFAULT);
        borderPane.setCursor(Cursor.DEFAULT);
        borderPane.setCenter(gridPane);
    }

    private void lose() {
        stopTask();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            //no need to catch, its only to avoid json crashing(two responses from server can come together)
        }
        JSONObject request = new JSONObject();
        request.put("request", "LOST");
        sendRequest(request.toString());
        startTask();
    }
}

