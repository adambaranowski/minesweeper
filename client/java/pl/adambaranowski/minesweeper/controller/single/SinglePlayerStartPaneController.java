package pl.adambaranowski.minesweeper.controller.single;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.adambaranowski.minesweeper.functions.SceneChanger;

import java.io.IOException;

public class SinglePlayerStartPaneController extends SceneChanger {

    @FXML
    private Label bombsCountLabel;

    @FXML
    private Button bombsIncButton;

    @FXML
    private Button bombsDcrButton;

    @FXML
    private Label sizeValueLabel;

    @FXML
    private Button sizeIncButton;

    @FXML
    private Button sizeDcrButton;

    @FXML
    private Button playButton;

    @FXML
    private Button toMenuButton;

    @FXML
    private Slider bombsSlider;

    @FXML
    private Slider sizeSlider;

    private int bombsCount;
    private int size;

    private final int MAX_SIZE = 20;
    private final int MIN_SIZE = 7;

    private int maxBombsCount;
    private final int MIN_BOMBS_COUNT = 1;


    public void initialize() {
        updateMaxBombs();
        updateBombs();
        updateSize();
        configureButtons();
    }


    private void configureButtons() {
        configureSizeDcrButton();
        configureSizeIncButton();
        configureBombDcrButton();
        configureBombIncButton();
        configureToMenuButton();
        configurePlayButton();
        configureSizeSlider();
        configureBombsSlider();
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

    private void updateBombs() {
        bombsCount = Integer.parseInt(bombsCountLabel.getText());
    }

    private void updateSize() {
        size = Integer.parseInt(sizeValueLabel.getText());

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


    private void configureToMenuButton() {
        toMenuButton.setOnAction(actionEvent -> {
            try {
                changeToMenuScene(toMenuButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configurePlayButton() {
        playButton.setOnAction(actionEvent -> {
            try {
                changeToSinglePlayerGameScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void changeToSinglePlayerGameScene() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/single/singlePlayerGamePane.fxml"));

        Parent singlePlayerGameParent = loader.load();
        Scene singlePlayerGameScene = new Scene(singlePlayerGameParent);

        //access the method initData to put board size and bombs count
        SinglePlayerGamePaneController singlePlayerGamePaneController = loader.getController();
        singlePlayerGamePaneController.initData(size, bombsCount);

        Stage window = (Stage) (playButton.getScene().getWindow());
        window.setScene(singlePlayerGameScene);
        window.show();

        //stop timer when closing window
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                singlePlayerGamePaneController.getTimerSetter().stopTimer();
            }
        });

    }

}