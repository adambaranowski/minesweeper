package pl.adambaranowski.minesweeper.functions;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

public class TimerSetter {

    private Label timerLabel;
    private int seconds;
    private Timer timer;

    public void setTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                seconds++;
                Platform.runLater(() -> timerLabel.setText(String.valueOf(seconds)));
            }
        }, 1000, 1000);
    }

    public void stopTimer() {
        timer.cancel();
    }

    public TimerSetter(Label timerLabel) {
        this.timerLabel = timerLabel;
        this.seconds = 0;
        timer = new Timer();
    }
}
