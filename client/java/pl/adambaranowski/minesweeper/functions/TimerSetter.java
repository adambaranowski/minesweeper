package pl.adambaranowski.minesweeper.functions;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.Timer;
import java.util.TimerTask;

public class TimerSetter implements ScenesChanger {

    private Label timerLabel;
    private int seconds;
    private Timer timer;
    private boolean countDown = false;
    boolean host;
    private boolean toGame;

    private Button button;
    private Scenes scenes;

    public void setTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (countDown) {
                    seconds--;
                } else {
                    seconds++;
                }

                Platform.runLater(() -> timerLabel.setText(String.valueOf(seconds)));
                timerLabel.setVisible(true);
            }
        }, 1000, 1000);

    }


    public void stopTimer() {
        timer.cancel();
    }

    public TimerSetter(Label timerLabel, boolean countDown, int seconds) {
        this.timerLabel = timerLabel;
        this.countDown = countDown;
        this.seconds = seconds;
        timer = new Timer();
    }
}
