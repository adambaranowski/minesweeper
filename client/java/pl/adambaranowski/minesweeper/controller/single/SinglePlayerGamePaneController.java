package pl.adambaranowski.minesweeper.controller.single;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import pl.adambaranowski.minesweeper.board.Board;

import java.io.IOException;
import java.util.HashMap;

import pl.adambaranowski.minesweeper.functions.SceneChanger;
import pl.adambaranowski.minesweeper.functions.TimerSetter;


public class SinglePlayerGamePaneController extends SceneChanger {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressNumber;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button toMenuButton;

    @FXML
    private Label mainLabel;

    @FXML
    private Label timerLabel;

    private HashMap<String, ToggleButton> buttons;


    private int size;
    private int bombsCount;
    private final static int SQUARE_SIZE = 25;
    private TimerSetter timerSetter;

    private Board board;

    public void initialize() {
        configureToMenuButton();
    }

    public TimerSetter getTimerSetter() {
        return timerSetter;
    }

    public void initData(int size, int bombsCount) {
        this.size = size;
        //this.bombsCount = bombsCount;

        addButtons();

        /*
        Initializing new board which holds all the buttons
        */
        this.board = new Board(size, bombsCount, buttons);

        timerSetter = new TimerSetter(timerLabel);
        timerSetter.setTimer();


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
                buttonId = String.valueOf(i) + " " + String.valueOf(j);
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
                            progressNumber.setText(String.valueOf(Math.round(board.unhideBoardPercentage * 100) + "%"));
                            progressBar.setProgress(board.unhideBoardPercentage);

                        }

                    }
                    if (mouseButton == MouseButton.SECONDARY) {
                        toggleButton.setText("P");

                    }

                    if (progressNumber.getText().equals("100%")) {
                        win();
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

    private void configureToMenuButton() {
        toMenuButton.setOnAction(actionEvent -> {
            try {
                changeToMenuScene(toMenuButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void lose() {
        mainLabel.setText("Game Over!");
        toMenuButton.setDisable(false);
        timerSetter.stopTimer();

    }

    private void win() {
        mainLabel.setText("You Win!");
        toMenuButton.setDisable(false);
        board.unhideButtonsWhenWin();
        timerSetter.stopTimer();

    }

}